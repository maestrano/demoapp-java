package com.example;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
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
import org.xml.sax.SAXException;

import com.maestrano.Maestrano;
import com.maestrano.exception.MnoConfigurationException;
import com.maestrano.saml.AuthRequest;
import com.maestrano.saml.Response;
import com.maestrano.sso.MnoGroup;
import com.maestrano.sso.MnoSession;
import com.maestrano.sso.MnoUser;

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
		Maestrano maestrano = Maestrano.get(marketplace);
		AuthRequest authReq = new AuthRequest(maestrano, allRequestParams);

		String ssoUrl = authReq.getRedirectUrl();
		return new ModelAndView("redirect:" + ssoUrl);

	}

	@RequestMapping(value = "/maestrano/auth/saml/consume/{marketplace}", method = RequestMethod.POST)
	public ModelAndView consume(@PathVariable("marketplace") String marketplace, @RequestParam(value = "SAMLResponse") String samlResponse)
			throws MnoConfigurationException, ParseException, CertificateException, ParserConfigurationException, SAXException, IOException {

		logger.debug("/maestrano/auth/saml/consume/" + marketplace);

		Maestrano maestrano = Maestrano.get(marketplace);
		Response authResp = Response.loadFromBase64XML(maestrano.ssoService(), samlResponse);
		logger.trace("/maestrano/auth/saml/consume/" + marketplace + " received: " + authResp);
		if (authResp.isValid()) {

			// Build maestrano user and group objects
			MnoUser mnoUser = new MnoUser(authResp);
			MnoGroup mnoGroup = new MnoGroup(authResp);

			// No database model in this project. We just keep the relevant details in session
			httpSession.setAttribute("loggedIn", true);
			httpSession.setAttribute("name", mnoUser.getFirstName());
			httpSession.setAttribute("surname", mnoUser.getLastName());
			httpSession.setAttribute("groupName", mnoGroup.getName());
			httpSession.setAttribute("groupId", mnoGroup.getUid());
			httpSession.setAttribute("marketplace", maestrano.getMarketplace());
			// Set Maestrano session (used for Single Logout)
			MnoSession mnoSession = new MnoSession(maestrano.getMarketplace(), httpSession, mnoUser);
			mnoSession.save();

			// Redirect to you application home page
			return new ModelAndView("redirect:/");
		} else {
			return new ModelAndView("SAML Response is invalid");
		}

	}

}
