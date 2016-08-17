package com.example;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.maestrano.Maestrano;
import com.maestrano.exception.MnoConfigurationException;

@WebServlet("/marketplaces")
public class MarketplaceServlet extends AbstractJsonServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected Object get(HttpServletRequest request) throws ServletException {
		Set<String> presets = Maestrano.presets();
		Map<String, Map<String, Object>> marketplacesMetadata = new LinkedHashMap<String, Map<String, Object>>();
		for (String preset : presets) {
			Map<String, Object> metadataHash;
			try {
				metadataHash = Maestrano.get(preset).toMetadataHash();
			} catch (MnoConfigurationException e) {
				throw new ServletException(e);
			}
			marketplacesMetadata.put(preset, metadataHash);
		}
		return marketplacesMetadata;
	}
}
