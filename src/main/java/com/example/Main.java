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

	private static final String DEFAULT_PORT = "8080";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String webappDirLocation = "src/main/webapp/";

		// The port that we should run on can be set into an environment variable
		// Look for that variable and default to 8080 if it isn't there.
		String webPort = System.getenv("PORT");
		if (webPort == null || webPort.isEmpty()) {
			System.out.println("No PORT env was given, will use port:" + DEFAULT_PORT);
			webPort = DEFAULT_PORT;
		}
		String appHost = System.getenv("APP_HOST");
		if (appHost == null || appHost.isEmpty()) {
			appHost = "http://localhost:" + webPort;
			System.out.println("No APP_HOST environment variable was given, will use: " + appHost);
		}
		// Configure Maestrano API
		Properties props = new Properties();

		// Environment configuration (Maestrano UAT environment)
		props.setProperty("app.environment", "test");
		props.setProperty("app.host", appHost);
		// props.setProperty("api.connecHost", "http://api-connec.uat.maestrano.io");
		// props.setProperty("api.accountHost", "https://uat.maestrano.io");
		// props.setProperty("sso.idp", "https://uat.maestrano.io");

		// Authentication
		props.setProperty("api.id", "app-1");
		props.setProperty("api.key", "gfcmbu8269wyi0hjazk4t7o1sndpvrqxl53e1");

		// Add Connec! webhook notification path for your application
		// Subscribe to certain entities (to receive updates from Connec!)
		// These settings will automatically appear in your Metadata endpoint (see maestrano-java README)
		props.setProperty("webhook.connec.notificationsPath", "/maestrano/connec/notifications");
		props.setProperty("webhook.connec.subscriptions.company", "true");
		props.setProperty("webhook.connec.subscriptions.organizations", "true");
		props.setProperty("webhook.connec.subscriptions.people", "true");

		Maestrano.configure("default", props);

		// For multi-tenant sintegration, define different presets
		Properties otherProps = new Properties();
		otherProps.setProperty("app.environment", "test");
		otherProps.setProperty("app.host", appHost);
		// otherProps.setProperty("api.connecHost", "http://api-connec.uat.maestrano.io");
		// otherProps.setProperty("api.accountHost", "https://uat.maestrano.io");
		// otherProps.setProperty("sso.idp", "https://uat.maestrano.io");
		otherProps.setProperty("sso.initPath", "/maestrano/auth/saml/init/other-tenant");

		otherProps.setProperty("api.id", "app-2");
		otherProps.setProperty("api.key", "przbnuxvogf6th879kl30wi5ycje2sad41mq2");

		otherProps.setProperty("webhook.connec.notificationsPath", "/maestrano/connec/notifications");
		otherProps.setProperty("webhook.connec.subscriptions.company", "true");
		otherProps.setProperty("webhook.connec.subscriptions.organizations", "true");
		otherProps.setProperty("webhook.connec.subscriptions.people", "true");

		Maestrano.configure("other-tenant", otherProps);

		Server server = new Server(Integer.valueOf(webPort));
		WebAppContext root = new WebAppContext();

		root.setContextPath("/");
		root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
		root.setResourceBase(webappDirLocation);

		// Parent loader priority is a class loader setting that Jetty accepts.
		// By default Jetty will behave like most web containers in that it will
		// allow your application to replace non-server libraries that are part of the
		// container. Setting parent loader priority to true changes this behavior.
		// Read more here: http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading
		root.setParentLoaderPriority(true);

		server.setHandler(root);

		server.start();
		server.join();
	}

}
