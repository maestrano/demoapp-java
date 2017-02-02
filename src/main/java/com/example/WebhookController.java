package com.example;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.maestrano.Maestrano;
import com.maestrano.exception.MnoException;
/**
 * Controller used to demonstrate Connec! webhook process
 *
 */
@Controller
public class WebhookController {

	@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Invalid Maestrano Credentials") // 401
	public class UnauthorizedException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

	@RequestMapping(value = "/maestrano/connec/notifications/{marketplace}", method = RequestMethod.POST)
	public String process(HttpServletRequest httpRequest, @PathVariable("marketplace") String marketplace, @RequestBody Map<String, List<Map<String, Object>>> entities)
			throws MnoException, UnauthorizedException {
		// Manual Basic authentication to demonstrate that this endpoint needs to be password protected
		// The best approach would be to use spring security
		// https://spring.io/guides/gs/securing-web/
		// http://www.baeldung.com/spring-security-authentication-provider
		Maestrano maestrano = Maestrano.get(marketplace);
		if (maestrano.authenticate(httpRequest)) {
			// Process webhook
			logger.trace("/maestrano/connec/notifications/" + marketplace + " received: " + entities);
			return "OK";
		} else {
			logger.trace("/maestrano/connec/notifications/" + marketplace + " - authentication failed");
			throw new UnauthorizedException();
		}
	}
}
