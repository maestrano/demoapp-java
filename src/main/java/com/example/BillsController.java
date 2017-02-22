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
import com.maestrano.account.Bill;
import com.maestrano.configuration.Preset;
import com.maestrano.exception.ApiException;
import com.maestrano.exception.AuthenticationException;
import com.maestrano.exception.InvalidRequestException;
import com.maestrano.exception.MnoConfigurationException;
import com.maestrano.sso.Session;

/**
 * Controller demonstrating calls to Bill client
 *
 */
@Controller
public class BillsController {

	@Autowired
	private HttpSession httpSession;

	@RequestMapping(value = "/bills", method = RequestMethod.GET)
	public ModelAndView bills(ModelMap model) throws AuthenticationException, ApiException, InvalidRequestException, MnoConfigurationException {
		List<Bill> billList = null;

		if (Boolean.TRUE.equals(httpSession.getAttribute("loggedIn"))) {
			// Example of Single Logout guarding
			// Check the user session is still valid
			String marketplace = (String) httpSession.getAttribute("marketplace");
			Preset preset = Maestrano.get(marketplace);

			Session session = Session.loadFromHttpSession(preset, httpSession);
			if (!session.isValid()) {
				String initUrl = preset.getSso().getInitUrl();
				return new ModelAndView("redirect:" + initUrl);
			}
			// Fetch the bills related to the user group
			Map<String, String> filter = new HashMap<String, String>();
			filter.put("groupId", (String) httpSession.getAttribute("groupId"));
			billList = Bill.client(preset).all(filter);

		}
		model.addAttribute("billList", billList);

		return new ModelAndView("bills");

	}
}
