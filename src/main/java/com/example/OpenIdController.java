package com.example;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openid4java.association.AssociationException;
import org.openid4java.association.AssociationSessionType;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.Parameter;
import org.openid4java.message.ParameterList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.maestrano.Maestrano;
import com.maestrano.configuration.Preset;
import com.maestrano.exception.MnoConfigurationException;
import com.maestrano.exception.MnoException;
import com.maestrano.helpers.MnoDateHelper;
import com.maestrano.sso.Group;
import com.maestrano.sso.Session;
import com.maestrano.sso.User;

/**
 * Controller demonstrating Open SSO authentication with Maestrano
 *
 */
@Controller
public class OpenIdController {

	private static final Logger logger = LoggerFactory.getLogger(OpenIdController.class);

	@Autowired
	private HttpSession httpSession;

	private final ConsumerManager manager;

	public OpenIdController() {
		manager = new ConsumerManager();
		manager.setAssociations(new InMemoryConsumerAssociationStore());
		manager.setNonceVerifier(new InMemoryNonceVerifier(5000));
		manager.setMinAssocSessEnc(AssociationSessionType.DH_SHA256);
	}

	@RequestMapping(value = "/maestrano/auth/openid/init/{marketplace}", method = RequestMethod.GET)
	public ModelAndView init(ModelMap model, @PathVariable("marketplace") String marketplace, @RequestParam Map<String, String> allRequestParams)
			throws MnoConfigurationException, DiscoveryException, MessageException, ConsumerException {
		logger.trace("/maestrano/auth/openid/init/" + marketplace + " received: " + allRequestParams);
		Preset preset = Maestrano.get(marketplace);
		String apiHost = preset.getApi().getHost();

		String apiId = preset.getApi().getId();
		String host = preset.getApp().getHost();

		String openidUrl = apiHost + "/api/openid/provider/" + apiId;
		// perform discovery on the user-supplied identifier
		@SuppressWarnings("rawtypes")
		List discoveries = manager.discover(openidUrl);
		// attempt to associate with an OpenID provider
		// and retrieve one service endpoint for authentication
		DiscoveryInformation discovered = manager.associate(discoveries);

		// store the discovery information in the user's session
		httpSession.setAttribute("openid-disco", discovered);

		String returnToUrl = host + "/maestrano/auth/openid/consume/" + marketplace;
		AuthRequest authReq = manager.authenticate(discovered, returnToUrl);

		if (discovered.isVersion2()) {
			// Option 1: HTML FORM Redirection
			// Allows payloads > 255 bytes
			String opEndpoint = authReq.getOPEndpoint();
			model.addAttribute("opEndpoint", opEndpoint);
			@SuppressWarnings("rawtypes")
			Map parameterMap = authReq.getParameterMap();
			logger.trace("/maestrano/auth/openid/init/" + marketplace + " redirecting to openid for with opEndpoint" + opEndpoint + ", parameterMap: " + parameterMap);

			model.addAttribute("parameterMap", parameterMap);
			return new ModelAndView("openidform");
		} else {
			// Option 2: GET HTTP-redirect to the OpenID Provider endpoint
			// The only method supported in OpenID 1.x
			// redirect-URL usually limited ~2048 bytes
			String opEndpoint = authReq.getDestinationUrl(true);
			return new ModelAndView("redirect:" + opEndpoint);
		}

	}

	@RequestMapping(value = "/maestrano/auth/openid/consume/{marketplace}", method = RequestMethod.POST)
	public ModelAndView consume(@PathVariable("marketplace") String marketplace, HttpServletRequest request) throws MnoException, MessageException, DiscoveryException, AssociationException, ParseException {

		logger.debug("/maestrano/auth/openid/consume/" + marketplace);

		Preset preset = Maestrano.get(marketplace);
		// Retrieving the parameter list
		ParameterList parameterList = new ParameterList(request.getParameterMap());
		AuthSuccess authSuccess = AuthSuccess.createAuthSuccess(parameterList);
		//Extracnt parameters from the parameters list
		Map<String, String> parameters = extractParameters(parameterList);
		User user = loadUserFromParameters(parameters);
		Group group = loadGroupFromParameters(parameters);

		httpSession.setAttribute("loggedIn", true);
		httpSession.setAttribute("name", user.getFirstName());
		httpSession.setAttribute("surname", user.getLastName());
		httpSession.setAttribute("groupName", group.getName());
		httpSession.setAttribute("groupId", group.getUid());
		httpSession.setAttribute("marketplace", preset.getMarketplace());

		// Set Maestrano session (used for Single Logout)
		Session session = new Session(preset, user);
		session.save(httpSession);
		httpSession.setAttribute("openid", authSuccess.getIdentity());
		httpSession.setAttribute("openid-claimed", authSuccess.getClaimed());

		// Redirect to you application home page
		return new ModelAndView("redirect:/");

	}

	/**
	 * 
	 * parameterList contains a map containing
	 * openid.ax.type.ext1:"http://openid.net/schema/namePerson/first"
	 * openid.ax.value.ext1:"John"
	 * openid.ax.type.ext2:"http://openid.net/schema/namePerson/last"
	 * openid.ax.value.ext2:"Doe"
	 * this returns a map
	 * "http://openid.net/schema/namePerson/first" : "John"
	 * "http://openid.net/schema/namePerson/last" : "Doe"
	 * 
	 * @return
	 */
	public Map<String, String> extractParameters(ParameterList parameterList) {
		Map<String, String> result = new HashMap<>();
		@SuppressWarnings("unchecked")
		List<Parameter> parameters = parameterList.getParameters();
		for (Parameter parameter : parameters) {
			String key = parameter.getKey();
			if (key.startsWith("openid.ax.type.")) {
				String extNumber = key.substring("openid.ax.type.".length());
				Parameter parameterValue = parameterList.getParameter("openid.ax.value." + extNumber);
				result.put(parameter.getValue(), parameterValue.getValue());
			}
		}
		return result;
	}

	private static User loadUserFromParameters(Map<String, String> parameters) throws ParseException {

		String ssoSession = parameters.get("http://openid.maestrano.com/schema/session/key");
		Date ssoSessionRecheck = MnoDateHelper.fromIso8601(parameters.get("http://openid.maestrano.com/schema/session/expiration"));
		String groupRole = parameters.get("http://openid.maestrano.com/schema/company/role");
		String uid = parameters.get("http://openid.net/schema/person/guid");
		String virtualUid = parameters.get("http://openid.maestrano.com/schema/person/vguid");
		String email = parameters.get("http://openid.net/schema/contact/internet/email");
		String virtualEmail = parameters.get("http://openid.net/schema/contact/internet/vemail");
		String country = parameters.get("http://openid.net/schema/contact/country/home");
		String firstName = parameters.get("http://openid.net/schema/namePerson/first");
		String lastName = parameters.get("http://openid.net/schema/namePerson/last");
		String companyName = parameters.get("http://openid.net/schema/company/name");
		String groupUid = parameters.get("http://openid.maestrano.com/schema/company/guid");
		return new User(ssoSession, ssoSessionRecheck, groupUid, groupRole, uid, virtualUid, email, virtualEmail, firstName, lastName, country, companyName);
	}

	private static Group loadGroupFromParameters(Map<String, String> parameters) {

		String uid = parameters.get("http://openid.maestrano.com/schema/company/guid");
		String name = parameters.get("http://openid.net/schema/company/name");
		String email = parameters.get("http://openid.maestrano.com/schema/company/email");
		boolean hasCreditCard = false;
		Date freeTrialEndAt = null;
		String companyName = parameters.get("http://openid.net/schema/company/name");
		String currency = null;
		TimeZone timezone = TimeZone.getTimeZone(parameters.get("http://openid.maestrano.com/schema/company/timezone"));
		String country = parameters.get("http://openid.maestrano.com/schema/company/country");
		String city = parameters.get("http://openid.maestrano.com/schema/company/city");

		return new Group(uid, name, email, hasCreditCard, freeTrialEndAt, companyName, currency, timezone, country, city);
	}
}
