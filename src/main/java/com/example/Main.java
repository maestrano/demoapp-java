package com.example;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.EmptyResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import com.google.common.base.Strings;
import com.maestrano.Maestrano;
import com.maestrano.exception.MnoConfigurationException;

/**
 * 
 * This class launches the web application in an embedded Tomcat container.
 * 
 * This is the entry point to your application. The Java command that is used for launching should fire this main method.
 * 
 */
public class Main {

	public static void main(String[] args) throws IOException, ServletException, LifecycleException, MnoConfigurationException {
		String webPort = System.getenv("PORT");
		if (webPort == null || webPort.isEmpty()) {
			webPort = "8080";
		}
		String appHost = System.getenv("DEMO_APP_HOST");
		if (appHost == null || appHost.isEmpty()) {
			appHost = "http://localhost:" + webPort;
		}
		log("WebPort: " + webPort);
		log("AppHost: " + appHost);
		log("Using Maestrano: " + Maestrano.getVersion());
		configureMaestrano(appHost);
		startWebServer(webPort);
	}

	private static final String[] DEV_PLATFORM_ENVIRONMENT_VARIABLES = { "ENVIRONMENT_NAME", "ENVIRONMENT_KEY", "ENVIRONMENT_SECRET" };

	private static void configureMaestrano(String appHost) throws MnoConfigurationException {
		// Configure Maestrano API Using properties files
		Properties properties = new Properties();

		// Environment configuration (Maestrano UAT environment)
		properties.setProperty("environment", "test");
		properties.setProperty("app.host", appHost);
		// Authentication
		properties.setProperty("api.id", "app-1");
		properties.setProperty("api.key", "gfcmbu8269wyi0hjazk4t7o1sndpvrqxl53e1");
		// Add Connec! webhook notification path for your application
		// Subscribe to certain entities (to receive updates from Connec!)
		// These settings will automatically appear in your Metadata endpoint
		// (see maestrano-java README)
		properties.setProperty("webhook.connec.notificationsPath", "/maestrano/connec/notifications");
		properties.setProperty("webhook.connec.subscriptions.company", "true");
		properties.setProperty("webhook.connec.subscriptions.organizations", "true");
		properties.setProperty("webhook.connec.subscriptions.people", "true");
		// Configure maestrano for the default preset
		Maestrano.configure(properties);

		// For multi-tenant sintegration, define different presets using a properties file
		Maestrano.configure("other-tenant", "other-tenant-config.properties");

		if (hasEnvironments(DEV_PLATFORM_ENVIRONMENT_VARIABLES)) {
			// AutoConfigure Maestrano API Using Development platform
			Properties developmentPlatformProperties = new Properties();
			developmentPlatformProperties.setProperty("dev-platform.host", "https://dev-platform.maestrano.io/");
			Map<String, Maestrano> marketplaces = Maestrano.autoConfigure(developmentPlatformProperties);
			log("Marketplaces Configurations Found: " + marketplaces.keySet());
		} else {
			log("Marketplace autoConfigure not activated. Environment variable not found: " + Arrays.toString(DEV_PLATFORM_ENVIRONMENT_VARIABLES));
		}

	}

	private static void startWebServer(String webPort) throws IOException, ServletException, LifecycleException {
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(Integer.valueOf(webPort));
		File root = getRootFolder();
		File webContentFolder = new File(root.getAbsolutePath(), "src/main/webapp/");
		if (!webContentFolder.exists()) {
			webContentFolder = Files.createTempDirectory("default-doc-base").toFile();
		}
		StandardContext ctx = (StandardContext) tomcat.addWebapp("", webContentFolder.getAbsolutePath());
		// Set execution independent of current thread context classloader (compatibility with exec:java mojo)
		ctx.setParentClassLoader(Main.class.getClassLoader());

		log("Configuring app with basedir: " + webContentFolder.getAbsolutePath());

		// Declare an alternative location for your "WEB-INF/classes" dir
		// Servlet 3.0 annotation will work
		File additionWebInfClassesFolder = new File(root.getAbsolutePath(), "target/classes");
		WebResourceRoot resources = new StandardRoot(ctx);

		WebResourceSet resourceSet;
		if (additionWebInfClassesFolder.exists()) {
			resourceSet = new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClassesFolder.getAbsolutePath(), "/");
			log("Loading WEB-INF resources from as '" + additionWebInfClassesFolder.getAbsolutePath() + "'");
		} else {
			resourceSet = new EmptyResourceSet(resources);
		}
		resources.addPreResources(resourceSet);
		ctx.setResources(resources);

		tomcat.start();
		tomcat.getServer().await();
	}

	private static File getRootFolder() {
		try {
			File root;
			String runningJarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceAll("\\\\", "/");
			int lastIndexOf = runningJarPath.lastIndexOf("/target/");
			if (lastIndexOf < 0) {
				root = new File("");
			} else {
				root = new File(runningJarPath.substring(0, lastIndexOf));
			}
			log("Application resolved root folder: " + root.getAbsolutePath());
			return root;
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static void log(String message) {
		System.out.println("[MAESTRANO] " + message);
	}

	private static boolean hasEnvironments(String[] codes) {
		for (String code : codes) {
			if (Strings.isNullOrEmpty(System.getenv(code))) {
				return false;
			}
		}
		return true;
	}
}
