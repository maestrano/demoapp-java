package com.example;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

import com.maestrano.Maestrano;
import com.maestrano.configuration.Preset;

/**
 * Entry point of the application
 */
@SpringBootApplication
public class WebApplication extends SpringBootServletInitializer {
	private static final Logger logger = LoggerFactory.getLogger(WebApplication.class);

	public static void main(String[] args) throws Exception {

		logger.info("Autoconfiguring Maestrano");
		Map<String, Preset> marketplaces = Maestrano.autoConfigure();
		logger.info("Marketplaces Configurations Found: " + marketplaces.keySet());
		SpringApplication.run(WebApplication.class, args);
	}

	/**
	 * Spring Boot Configuration
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WebApplication.class);
	}

}