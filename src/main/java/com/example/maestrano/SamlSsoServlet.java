package com.example.maestrano;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.maestrano.saml.AuthRequest;
import com.maestrano.saml.Response;
import com.maestrano.sso.MnoGroup;
import com.maestrano.sso.MnoSession;
import com.maestrano.sso.MnoUser;

public class SamlSsoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Pattern regExTenantPattern = Pattern.compile("/maestrano/auth/saml/init/([a-zA-Z0-9\\-]*)");

	// GET /maestrano/auth/saml/init
	// GET /maestrano/auth/saml/init/other-tenant
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Check for ID case first, since the All pattern would also match
		String preset = "default";
		Matcher matcher = regExTenantPattern.matcher(req.getRequestURI());
		if (matcher.find()) {
			preset = matcher.group(1);
		}

		AuthRequest authReq = new AuthRequest(preset, req);
		try {
			String ssoUrl = authReq.getRedirectUrl();
			resp.sendRedirect(ssoUrl);

		} catch (Exception e) {
			ServletOutputStream out = resp.getOutputStream();
			e.printStackTrace();
			out.write(e.getMessage().getBytes());
			out.flush();
			out.close();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Response authResp = null;
		try {
			authResp = new Response();
			authResp.loadXmlFromBase64(req.getParameter("SAMLResponse"));

			if (authResp.isValid()) {

				// Build maestrano user and group objects
				MnoUser mnoUser = new MnoUser(authResp);
				MnoGroup mnoGroup = new MnoGroup(authResp);

				// No database model in this project. We just keep the
				// relevant details in session
				HttpSession sess = req.getSession();
				sess.setAttribute("loggedIn", true);
				sess.setAttribute("name", mnoUser.getFirstName());
				sess.setAttribute("surname", mnoUser.getLastName());
				sess.setAttribute("groupName", mnoGroup.getName());
				sess.setAttribute("groupId", mnoGroup.getUid());

				// Set Maestrano session (used for Single Logout)
				MnoSession mnoSession = new MnoSession(req.getSession(), mnoUser);
				mnoSession.save();

				// Redirect to you application home page
				resp.sendRedirect("/");

			} else {
				ServletOutputStream out = resp.getOutputStream();
				out.write("SAML Response is invalid".getBytes());
				out.flush();
				out.close();
			}

		} catch (Exception e) {
			ServletOutputStream out = resp.getOutputStream();
			e.printStackTrace();
			out.write(e.getMessage().getBytes());
			out.flush();
			out.close();
		}

		ServletOutputStream out = resp.getOutputStream();

		out.write("Hello Servlet!".getBytes());
		out.flush();
		out.close();
	}

}
