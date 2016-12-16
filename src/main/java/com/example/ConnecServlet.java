package com.example;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.dto.Organization;
import com.example.dto.Organizations;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maestrano.Maestrano;
import com.maestrano.exception.MnoException;
import com.maestrano.net.ConnecClient;
import com.maestrano.sso.MnoSession;

@WebServlet("/connec")
public class ConnecServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(ConnecServlet.class);
	private static final long serialVersionUID = 1L;
	private static final String REDIRECTION_UL = "/connec/index.jsp";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		List<Organization> organizations = Collections.emptyList();
		String organizationsJson = null;
		if (ServletHelper.isLoggedIn(request)) {
			// Example of Single Logout guarding
			// Check the user session is still valid
			String marketplace = (String) session.getAttribute("marketplace");

			try {
				Maestrano maestrano = Maestrano.get(marketplace);
				MnoSession mnoSession = new MnoSession(marketplace, session);
				if (!mnoSession.isValid()) {
					response.sendRedirect(maestrano.ssoService().getInitUrl());
					return;
				}
				String groupId = (String) session.getAttribute("groupId");
				ConnecClient connecClient = ConnecClient.withPreset(marketplace);

				Map<String, Object> organizationsMap = connecClient.all("organizations", groupId);
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				organizationsJson = gson.toJson(organizationsMap);

				Organizations organizationsResult = connecClient.all("organizations", groupId, Organizations.class);
				organizations = organizationsResult.getOrganizations();
			} catch (MnoException e) {
				logger.error("Could not retrieve organizations", e);
			}
		}

		ServletContext servletContext = getServletContext();
		RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(REDIRECTION_UL);

		request.setAttribute("organizations", organizations);
		request.setAttribute("organizationsJson", organizationsJson);
		requestDispatcher.forward(request, response);
	}
}
