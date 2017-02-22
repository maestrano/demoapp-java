package com.example;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.example.WebhookManager.WebhookInfo;
import com.maestrano.Maestrano;
import com.maestrano.configuration.Preset;
import com.maestrano.exception.MnoException;

/**
 * Controller used to demonstrate Connec! webhook process See https://maestrano.atlassian.net/wiki/display/DEV/Connec%21+Webhook
 *
 */
@Controller
public class WebhookController {
	
	private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
	
	@Autowired
	WebhookManager webhookManager;

	@Autowired
	private HttpSession httpSession;
	
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Invalid Maestrano Credentials") // 401
	public class UnauthorizedException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	
	@RequestMapping(value = "/webhooks", method = RequestMethod.GET)
	public ModelAndView webhook(ModelMap model) throws MnoException {
		Collection<WebhookInfo> webhooks = null;
		if (Boolean.TRUE.equals(httpSession.getAttribute("loggedIn"))) {
			String groupId = (String) httpSession.getAttribute("groupId");
			webhooks = webhookManager.get(groupId);
		}	
		model.addAttribute("webhooks", webhooks);
		return new ModelAndView("webhooks");
	}
	
	@RequestMapping(value = "/maestrano/connec/notifications/{marketplace}", method = RequestMethod.POST)
	public String process(HttpServletRequest httpRequest, @PathVariable("marketplace") String marketplace, @RequestBody Map<String, List<Map<String, Object>>> entitiesPerEntityName)
			throws MnoException, UnauthorizedException {
		// Manual Basic authentication to demonstrate that this endpoint needs to be password protected
		// The best approach would be to use spring security
		// https://spring.io/guides/gs/securing-web/
		// http://www.baeldung.com/spring-security-authentication-provider
		Preset preset = Maestrano.get(marketplace);
		if (preset.authenticate(httpRequest)) {
			// Process webhook
			logger.trace("/maestrano/connec/notifications/" + marketplace + " received: " + entitiesPerEntityName);
			// {
			// "organizations": [
			// { "id": "e32303c1-5102-0132-661e-600308937d74", name: "DoeCorp Inc.", group_id: "cld-1234", ... }
			// ],
			// "people": [
			// { "id": "a34303d1-4142-0152-362e-610408337d74", first_name: "John", last_name: "Doe", group_id: "cld-5678", ... }
			// ]
			// }
			Date now = new Date();
			for (Entry<String, List<Map<String, Object>>> entry : entitiesPerEntityName.entrySet()) {
				String entityName = entry.getKey();
				List<Map<String, Object>> entities = entry.getValue();
				for (Map<String, Object> entity : entities) {
					String groupId = (String) entity.get("group_id");
					webhookManager.add(groupId, new WebhookInfo(now, entityName, entity));
				}
			}

			return "OK";
		} else {
			logger.trace("/maestrano/connec/notifications/" + marketplace + " - authentication failed");
			throw new UnauthorizedException();
		}
	}
}
