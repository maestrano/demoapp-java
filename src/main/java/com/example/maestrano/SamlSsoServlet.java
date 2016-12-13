package com.example.maestrano;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.ServletHelper;
import com.maestrano.Maestrano;
import com.maestrano.exception.MnoConfigurationException;
import com.maestrano.saml.AuthRequest;
import com.maestrano.saml.Response;
import com.maestrano.sso.MnoGroup;
import com.maestrano.sso.MnoSession;
import com.maestrano.sso.MnoUser;

@WebServlet(urlPatterns = { "/maestrano/auth/saml/init", "/maestrano/auth/saml/init/*", "/maestrano/auth/saml/consume", "/maestrano/auth/saml/consume/*" })
public class SamlSsoServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(SamlSsoServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 * <ul>
	 * <li>GET/maestrano/auth/saml/init</li>
	 * <li>GET/maestrano/auth/saml/init/[marketplace]</li>
	 * <ul>
	 * 
	 * @throws IOException
	 * 
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Check for ID case first, since the All pattern would also match
		Maestrano maestrano;
		try {
			maestrano = ServletHelper.getConfiguration(request);
		} catch (MnoConfigurationException e) {
			ServletHelper.writeError(response, e);
			return;
		}
		AuthRequest authReq = new AuthRequest(maestrano, request);
		try {
			String ssoUrl = authReq.getRedirectUrl();
			response.sendRedirect(ssoUrl);
		} catch (XMLStreamException e) {
			logger.error("could not get redirect URL", e);
			ServletHelper.writeError(response, e);
		}

	}

	/**
	 * <ul>
	 * <li>GET/maestrano/auth/saml/consume</li>
	 * <li>GET/maestrano/auth/saml/consume/[marketplace]</li>
	 * <ul>
	 * 
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Maestrano maestrano;
			try {
				maestrano = ServletHelper.getConfiguration(request);
			} catch (MnoConfigurationException e) {
				ServletHelper.writeError(response, e);
				return;
			}
			String samlResponse = request.getParameter("SAMLResponse");
			Response authResp = Response.loadFromBase64XML(maestrano.ssoService(), samlResponse);
			logger.trace("/maestrano/auth/saml/consume received: " + authResp);
			if (authResp.isValid()) {

				// Build maestrano user and group objects
				MnoUser mnoUser = new MnoUser(authResp);
				MnoGroup mnoGroup = new MnoGroup(authResp);

				// No database model in this project. We just keep the relevant details in session
				HttpSession sess = request.getSession();
				sess.setAttribute("loggedIn", true);
				sess.setAttribute("name", mnoUser.getFirstName());
				sess.setAttribute("surname", mnoUser.getLastName());
				sess.setAttribute("groupName", mnoGroup.getName());
				sess.setAttribute("groupId", mnoGroup.getUid());
				sess.setAttribute("marketplace", maestrano.getPreset());
				// Set Maestrano session (used for Single Logout)
				MnoSession mnoSession = new MnoSession(maestrano.getPreset(), request.getSession(), mnoUser);
				mnoSession.save();

				// Redirect to you application home page
				response.sendRedirect("/");

			} else {
				ServletOutputStream out = response.getOutputStream();
				out.write("SAML Response is invalid".getBytes());
				out.flush();
				out.close();
			}

		} catch (Exception e) {
			logger.error("Could not process /maestrano/auth/saml/consume", e);
			ServletHelper.writeError(response, e);
		}
	}

}
