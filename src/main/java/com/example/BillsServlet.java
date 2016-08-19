package com.example;

import java.io.IOException;
import java.util.HashMap;
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

import com.maestrano.Maestrano;
import com.maestrano.account.MnoBill;
import com.maestrano.exception.MnoException;
import com.maestrano.sso.MnoSession;

@WebServlet("/bills")
public class BillsServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(BillsServlet.class);
	private static final long serialVersionUID = 1L;
	private static final String REDIRECTION_UL = "/bills/index.jsp";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		boolean loggedIn = (session.getAttribute("loggedIn") != null && (Boolean) session.getAttribute("loggedIn"));
		List<MnoBill> billList = null;

		if (loggedIn) {
			// Example of Single Logout guarding
			// Check the user session is still valid
			String marketplace = (String) session.getAttribute("marketplace");
			try {
				MnoSession mnoSession = new MnoSession(marketplace, session);
				if (!mnoSession.isValid()) {
					response.sendRedirect(Maestrano.get(marketplace).ssoService().getInitUrl());
					return;
				}
				// Fetch the bills related to the user group
				Map<String, String> filter = new HashMap<String, String>();
				filter.put("groupId", (String) session.getAttribute("groupId"));
				billList = MnoBill.client(marketplace).all(filter);

			} catch (MnoException e) {
				logger.error("Could not retrieve bill", e);
			}
		}

		ServletContext servletContext = getServletContext();
		RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(REDIRECTION_UL);

		request.setAttribute("billList", billList);
		requestDispatcher.forward(request, response);
	}
}
