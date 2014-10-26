package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.maestrano.account.MnoBill;

public class BillsServlet extends HttpServlet {
private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Boolean loggedIn = (req.getSession().getAttribute("loggedIn") != null && (Boolean) req.getSession().getAttribute("loggedIn"));
		List<MnoBill> rawBillList = null;
		List<MnoBill> billList = new ArrayList<MnoBill>();
		
		try {
			if (loggedIn) {
				rawBillList = MnoBill.all();
				for (MnoBill bill : rawBillList) {
					if (bill.getGroupId().equals(req.getSession().getAttribute("groupId"))) {
						billList.add(bill);
					}
				}
			}
			
			String url="/bills/index.jsp";
		    ServletContext sc = getServletContext();
		    RequestDispatcher rd = sc.getRequestDispatcher(url);

		    req.setAttribute("billList", billList );
		    rd.forward(req, resp);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
