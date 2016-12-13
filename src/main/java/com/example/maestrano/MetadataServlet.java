package com.example.maestrano;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.ServletHelper;
import com.maestrano.Maestrano;
import com.maestrano.exception.MnoConfigurationException;
import com.maestrano.exception.MnoException;

@WebServlet(urlPatterns = { "/maestrano/metadata", "/maestrano/metadata/*" })
public class MetadataServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(MetadataServlet.class);
	private static final long serialVersionUID = 1L;
	private static final Pattern METADATA_PATTERN = Pattern.compile("/maestrano/metadata/([a-zA-Z0-9\\-]*)");

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Maestrano maestrano;
		try {
			maestrano = ServletHelper.getConfiguration(request);
		} catch (MnoConfigurationException e) {
			ServletHelper.writeJson(response, "Unrecognized preset");
			return;
		}
		try {
			if (maestrano.authenticate(request)) {
				ServletHelper.writeJson(response, maestrano.toMetadataHash());
				return;
			} else {
				logger.debug("Authentication Failed with authorization: " + request.getHeader("Authorization"));
			}
		} catch (MnoException e) {
			logger.debug("Authentication Failed with authorization: " + request.getHeader("Authorization"), e);
		}
		ServletHelper.writeJson(response, "Authentication Failed");
	}

}
