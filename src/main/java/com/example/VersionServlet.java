package com.example;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.maestrano.Maestrano;

@WebServlet("/version")
public class VersionServlet extends AbstractJsonServlet {
	private static final Date STARTING_TIME = new Date();
	private static final long serialVersionUID = 1L;

	@Override
	protected Object get(HttpServletRequest request) {
		Map<String, Object> version = new LinkedHashMap<String, Object>();
		version.put("CI_COMMIT_ID", BuildInformationHelper.getGitSha1());
		version.put("CI_BUILD_TIMESTAMP", BuildInformationHelper.getBuildTimestamp());
		version.put("CI_VERSION", BuildInformationHelper.getVersion());
		version.put("STARTING_TIME", STARTING_TIME);
		version.put("MAESTRANO_VERSION", Maestrano.getVersion());
		return version;
	}
}
