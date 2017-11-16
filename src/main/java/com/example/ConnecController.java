package com.example;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.dto.Organization;
import com.example.dto.Organizations;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maestrano.Maestrano;
import com.maestrano.configuration.Preset;
import com.maestrano.exception.MnoException;
import com.maestrano.net.ConnecClient;
import com.maestrano.sso.Session;

/** Controller used to demonstrate calls to Connec! API */
@Controller
public class ConnecController {

	@Autowired
	private HttpSession httpSession;

	@RequestMapping(value = "/connec", method = RequestMethod.GET)
	public ModelAndView connec(ModelMap model) throws MnoException {
		List<Organization> organizations = Collections.emptyList();
		String organizationsJson = null;
		if (Boolean.TRUE.equals(httpSession.getAttribute("loggedIn"))) {
			String marketplace = (String) httpSession.getAttribute("marketplace");

			Preset preset = Maestrano.get(marketplace);
			Session session = Session.loadFromHttpSession(preset, httpSession);
			if (!session.isValid()) {
				String initUrl = preset.getSso().getInitUrl();
				return new ModelAndView("redirect:" + initUrl);
			}
			String groupId = (String) httpSession.getAttribute("groupId");
			ConnecClient connecClient = new ConnecClient(marketplace);

			Map<String, Object> organizationsMap = connecClient.all("organizations", groupId);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			organizationsJson = gson.toJson(organizationsMap);

			Organizations organizationsResult = connecClient.all("organizations", groupId, Organizations.class);
			organizations = organizationsResult.getOrganizations();
		}

		model.addAttribute("organizations", organizations);
		model.addAttribute("organizationsJson", organizationsJson);

		return new ModelAndView("connec");
	}
}
