package com.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.maestrano.Maestrano;
import com.maestrano.account.MnoBill;
import com.maestrano.exception.ApiException;
import com.maestrano.exception.AuthenticationException;
import com.maestrano.exception.InvalidRequestException;
import com.maestrano.exception.MnoConfigurationException;
import com.maestrano.sso.MnoSession;

/**
 * Controller demonstrating calls to MnoBill client
 *
 */
@Controller
public class BillsController {

	@Autowired
	private HttpSession httpSession;

	@RequestMapping(value = "/bills", method = RequestMethod.GET)
	public ModelAndView bills(ModelMap model) throws AuthenticationException, ApiException, InvalidRequestException, MnoConfigurationException {
		List<MnoBill> billList = null;

		if (Boolean.TRUE.equals(httpSession.getAttribute("loggedIn"))) {
			// Example of Single Logout guarding
			// Check the user session is still valid
			String marketplace = (String) httpSession.getAttribute("marketplace");

			MnoSession mnoSession = new MnoSession(marketplace, httpSession);
			if (!mnoSession.isValid()) {
				String initUrl = Maestrano.get(marketplace).ssoService().getInitUrl();
				return new ModelAndView("redirect:" + initUrl);
			}
			// Fetch the bills related to the user group
			Map<String, String> filter = new HashMap<String, String>();
			filter.put("groupId", (String) httpSession.getAttribute("groupId"));
			billList = MnoBill.client(marketplace).all(filter);

		}
		model.addAttribute("billList", billList);

		return new ModelAndView("bills");

	}
}
