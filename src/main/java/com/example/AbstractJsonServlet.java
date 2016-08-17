package com.example;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maestrano.json.DateDeserializer;
import com.maestrano.json.DateSerializer;
import com.maestrano.json.TimeZoneDeserializer;
import com.maestrano.json.TimeZoneSerializer;

public abstract class AbstractJsonServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Date.class, new DateSerializer())
			.registerTypeAdapter(Date.class, new DateDeserializer())
			.registerTypeAdapter(TimeZone.class, new TimeZoneSerializer())
			.registerTypeAdapter(TimeZone.class, new TimeZoneDeserializer())
			.create();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf8");
		response.setContentType("application/json");
		Object object = get(request);
		GSON.toJson(object, response.getWriter());
	}

	/**
	 * return the object that will be converted to Json
	 * 
	 * @param request
	 * @return
	 */
	protected abstract Object get(HttpServletRequest request) throws ServletException;
}
