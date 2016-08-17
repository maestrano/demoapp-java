package com.example.maestrano;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.maestrano.Maestrano;
import com.maestrano.exception.MnoException;
import com.maestrano.saml.AuthRequest;
import com.maestrano.saml.Response;
import com.maestrano.sso.MnoGroup;
import com.maestrano.sso.MnoSession;
import com.maestrano.sso.MnoUser;

@WebServlet(urlPatterns = { "/maestrano/auth/saml/init", "/maestrano/auth/saml/init/*", "/maestrano/auth/saml/consume", "/maestrano/auth/saml/consume/*" })
public class SamlSsoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Pattern INIT_PATTERN = Pattern.compile("/maestrano/auth/saml/init/([a-zA-Z0-9\\-]*)");
	private static final Pattern CONSUME_PATTERN = Pattern.compile("/maestrano/auth/saml/consume/([a-zA-Z0-9\\-]*)");

	/**
	 * <ul>
	 * <li>GET/maestrano/auth/saml/init</li>
	 * <li>GET/maestrano/auth/saml/init/[marketplace]</li>
	 * <ul>
	 * 
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Check for ID case first, since the All pattern would also match
		String marketplace = "default";
		Matcher matcher = INIT_PATTERN.matcher(req.getRequestURI());
		if (matcher.find()) {
			marketplace = matcher.group(1);
		}
		AuthRequest authReq;
		try {
			authReq = new AuthRequest(Maestrano.get(marketplace), req);
		} catch (MnoException e) {
			throw new ServletException("Maestrano was not well configured", e);
		}
		try {
			String ssoUrl = authReq.getRedirectUrl();
			resp.sendRedirect(ssoUrl);

		} catch (Exception e) {
			e.printStackTrace();
			ServletOutputStream out = resp.getOutputStream();
			out.write(e.getMessage().getBytes());
			out.flush();
			out.close();
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
			String marketplace = "default";
			Matcher matcher = CONSUME_PATTERN.matcher(request.getRequestURI());
			if (matcher.find()) {
				marketplace = matcher.group(1);
			}

			Response authResp = new Response();
			authResp.loadXmlFromBase64(request.getParameter("SAMLResponse"));

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
				sess.setAttribute("marketplace", marketplace);
				// Set Maestrano session (used for Single Logout)
				MnoSession mnoSession = new MnoSession(request.getSession(), mnoUser);
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
			ServletOutputStream out = response.getOutputStream();
			e.printStackTrace();
			out.write(e.getMessage().getBytes());
			out.flush();
			out.close();
		}
	}

}
