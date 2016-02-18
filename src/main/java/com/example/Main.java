package com.example;

import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import com.maestrano.Maestrano;

/**
 * 
 * This class launches the web application in an embedded Jetty container. This is the entry point to your application. The Java command that is used for launching should fire this main method.
 * 
 */
public class Main {

	public static void main(String[] args) throws Exception {
		String webappDirLocation = "src/main/webapp/";
		String webPort = System.getenv("DEMO_PORT");
		if (webPort == null || webPort.isEmpty()) {
			webPort = "8080";
		}
		System.out.println("webPort: " + webPort);
		String appHost = System.getenv("DEMO_APP_HOST");
		if (appHost == null || appHost.isEmpty()) {
			appHost = "http://localhost:" + webPort;
		}
		System.out.println("appHost: " + appHost);
		System.out.println("Using Maestrano: " + Maestrano.getVersion());
		// Configure Maestrano API
		Properties props = new Properties();

		// Environment configuration (Maestrano UAT environment)
		props.setProperty("app.environment", "test");
		props.setProperty("app.host", appHost);
		// Authentication
		props.setProperty("api.id", "app-1");
		props.setProperty("api.key", "gfcmbu8269wyi0hjazk4t7o1sndpvrqxl53e1");
		// Add Connec! webhook notification path for your application
		// Subscribe to certain entities (to receive updates from Connec!)
		// These settings will automatically appear in your Metadata endpoint
		// (see maestrano-java README)
		props.setProperty("webhook.connec.notificationsPath", "/maestrano/connec/notifications");
		props.setProperty("webhook.connec.subscriptions.company", "true");
		props.setProperty("webhook.connec.subscriptions.organizations", "true");
		props.setProperty("webhook.connec.subscriptions.people", "true");
		//Configure maestrano for the default preset
		Maestrano.configure(props);

		// For multi-tenant sintegration, define different presets using a properties file
		Maestrano.configure("other-tenant", "other-tenant-config.properties");

		// The port that we should run on can be set into an environment
		// variable
		// Look for that variable and default to 8080 if it isn't there.

		Server server = new Server(Integer.valueOf(webPort));
		WebAppContext root = new WebAppContext();

		root.setContextPath("/");
		root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
		root.setResourceBase(webappDirLocation);

		// Parent loader priority is a class loader setting that Jetty accepts.
		// By default Jetty will behave like most web containers in that it will
		// allow your application to replace non-server libraries that are part
		// of the container. Setting parent loader priority to true changes this
		// behavior.
		// Read more here:
		// http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading
		root.setParentLoaderPriority(true);

		server.setHandler(root);

		server.start();
		server.join();
	}

}
