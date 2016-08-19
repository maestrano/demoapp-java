package com.example;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.maestrano.Maestrano;
import com.maestrano.exception.MnoConfigurationException;
import com.maestrano.json.DateDeserializer;
import com.maestrano.json.DateSerializer;
import com.maestrano.json.TimeZoneDeserializer;
import com.maestrano.json.TimeZoneSerializer;

public class ServletHelper {
	private ServletHelper() {
	}

	private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer()).registerTypeAdapter(Date.class, new DateDeserializer())
			.registerTypeAdapter(TimeZone.class, new TimeZoneSerializer()).registerTypeAdapter(TimeZone.class, new TimeZoneDeserializer()).create();

	/**
	 * Retrieve the Maestrano Configuration from the request /url/[marketplace])
	 * 
	 * @throws ServletException
	 * @throws MnoConfigurationException
	 */
	public static Maestrano getConfiguration(Pattern pattern, HttpServletRequest request) throws MnoConfigurationException {
		Matcher matcher = pattern.matcher(request.getRequestURI());
		if (matcher.find()) {
			String marketplace = matcher.group(1);
			return Maestrano.get(marketplace);
		} else {
			return Maestrano.getDefault();
		}
	}

	public static void writeError(HttpServletResponse resp, Throwable e) {
		ServletOutputStream out = null;
		try {
			out = resp.getOutputStream();
			out.write(e.getMessage().getBytes());
			out.flush();
		} catch (IOException e1) {
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	public static void writeJson(HttpServletResponse response, Object object) throws JsonIOException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		GSON.toJson(object, response.getWriter());
	}

}
