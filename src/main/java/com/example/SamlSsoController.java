package com.example;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.maestrano.Maestrano;
import com.maestrano.configuration.Preset;
import com.maestrano.exception.MnoConfigurationException;
import com.maestrano.exception.MnoException;
import com.maestrano.saml.AuthRequest;
import com.maestrano.saml.Response;
import com.maestrano.sso.Group;
import com.maestrano.sso.Session;
import com.maestrano.sso.User;

/**
 * Controller demonstrating Saml SSO authentication with Maestrano
 *
 */
@Controller
public class SamlSsoController {

	private static final Logger logger = LoggerFactory.getLogger(SamlSsoController.class);

	@Autowired
	private HttpSession httpSession;

	@RequestMapping(value = "/maestrano/auth/saml/init/{marketplace}", method = RequestMethod.GET)
	public ModelAndView init(@PathVariable("marketplace") String marketplace, @RequestParam Map<String, String> allRequestParams) throws MnoConfigurationException, XMLStreamException, IOException {
		logger.trace("/maestrano/auth/saml/init/" + marketplace + " received: " + allRequestParams);
		Preset preset = Maestrano.get(marketplace);
		AuthRequest authReq = new AuthRequest(preset, allRequestParams);

		String ssoUrl = authReq.getRedirectUrl();
		return new ModelAndView("redirect:" + ssoUrl);

	}

	@RequestMapping(value = "/maestrano/auth/saml/consume/{marketplace}", method = RequestMethod.POST)
	public ModelAndView consume(@PathVariable("marketplace") String marketplace, @RequestParam(value = "SAMLResponse") String samlResponse) throws MnoException {

		logger.debug("/maestrano/auth/saml/consume/" + marketplace);

		Preset preset = Maestrano.get(marketplace);
		Response authResp = preset.getSso().buildResponse(samlResponse);
		logger.trace("/maestrano/auth/saml/consume/" + marketplace + " received: " + authResp);
		if (authResp.isValid()) {

			// Build maestrano user and group objects
			User user = new User(authResp);
			Group group = new Group(authResp);

			// No database model in this project. We just keep the relevant details in session
			httpSession.setAttribute("loggedIn", true);
			httpSession.setAttribute("name", user.getFirstName());
			httpSession.setAttribute("surname", user.getLastName());
			httpSession.setAttribute("groupName", group.getName());
			httpSession.setAttribute("groupId", group.getUid());
			httpSession.setAttribute("marketplace", preset.getMarketplace());
			// Set Maestrano session (used for Single Logout)
			Session session = new Session(preset, user);
			session.save(httpSession);

			// Redirect to you application home page
			return new ModelAndView("redirect:/");
		} else {
			return new ModelAndView("SAML Response is invalid");
		}

	}

}
