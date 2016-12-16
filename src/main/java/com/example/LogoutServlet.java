package com.example;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestrano.exception.MnoConfigurationException;
import com.maestrano.sso.MnoSession;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(LogoutServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String redirectUrl = "/";
		HttpSession session = request.getSession();
		if (ServletHelper.isLoggedIn(request)) {
			String marketplace = (String) session.getAttribute("marketplace");
			try {
				MnoSession mnoSession = new MnoSession(marketplace, session);
				redirectUrl = mnoSession.getLogoutUrl();
			} catch (MnoConfigurationException e) {
				logger.error("Could not create MnoSession for marketplace: " + marketplace, e);
			}

		}
		session.invalidate();
		response.sendRedirect(redirectUrl);
	}

}
