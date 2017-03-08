package com.example;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.maestrano.Maestrano;
import com.maestrano.configuration.Preset;
import com.maestrano.exception.MnoConfigurationException;
import com.maestrano.sso.Session;

@Controller
public class HomeController {

	@Autowired
	private HttpSession httpSession;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(ModelMap model) {
		return "home";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView logout() throws MnoConfigurationException {
		ModelAndView modelAndView = new ModelAndView("home");
		if (Boolean.TRUE.equals(httpSession.getAttribute("loggedIn"))) {
			String marketplace = (String) httpSession.getAttribute("marketplace");
			Preset preset = Maestrano.get(marketplace);
			Session session = Session.loadFromHttpSession(preset, httpSession);
			String redirectUrl = session.getLogoutUrl();
			modelAndView = new ModelAndView("redirect:" + redirectUrl);
		}
		httpSession.invalidate();
		return modelAndView;
	}

}
