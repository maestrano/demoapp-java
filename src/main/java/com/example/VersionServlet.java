package com.example;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.maestrano.Maestrano;

@WebServlet("/version")
public class VersionServlet extends HttpServlet {
	private static final Date STARTING_TIME = new Date();
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, Object> version = new LinkedHashMap<String, Object>();
		version.put("CI_COMMIT_ID", BuildInformationHelper.getGitSha1());
		version.put("CI_BUILD_TIMESTAMP", BuildInformationHelper.getBuildTimestamp());
		version.put("CI_VERSION", BuildInformationHelper.getVersion());
		version.put("STARTING_TIME", STARTING_TIME);
		version.put("MAESTRANO_VERSION", Maestrano.getVersion());
		ServletHelper.writeJson(response, version);
	}
}
