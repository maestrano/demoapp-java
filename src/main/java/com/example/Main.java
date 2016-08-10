package com.example;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Properties;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.EmptyResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import com.maestrano.Maestrano;
import com.maestrano.exception.MnoConfigurationException;

/**
 * 
 * This class launches the web application in an embedded Jetty container. This is the entry point to your application. The Java command that is used for launching should fire this main method.
 * 
 */
public class Main {

	public static void main(String[] args) throws Exception {
		String webPort = System.getenv("PORT");
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
		configureMaestrano(appHost);
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

		System.out.println("configuring app with basedir: " + webContentFolder.getAbsolutePath());

		// Declare an alternative location for your "WEB-INF/classes" dir
		// Servlet 3.0 annotation will work
		File additionWebInfClassesFolder = new File(root.getAbsolutePath(), "target/classes");
		WebResourceRoot resources = new StandardRoot(ctx);

		WebResourceSet resourceSet;
		if (additionWebInfClassesFolder.exists()) {
			resourceSet = new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClassesFolder.getAbsolutePath(), "/");
			System.out.println("loading WEB-INF resources from as '" + additionWebInfClassesFolder.getAbsolutePath() + "'");
		} else {
			resourceSet = new EmptyResourceSet(resources);
		}
		resources.addPreResources(resourceSet);
		ctx.setResources(resources);

		tomcat.start();
		tomcat.getServer().await();
	}

	private static void configureMaestrano(String appHost) throws MnoConfigurationException {
		// Configure Maestrano API
		Properties props = new Properties();

		// Environment configuration (Maestrano UAT environment)
		props.setProperty("environment", "test");
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
		// Configure maestrano for the default preset
		Maestrano.configure(props);

		// For multi-tenant sintegration, define different presets using a properties file
		Maestrano.configure("other-tenant", "other-tenant-config.properties");
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
			System.out.println("application resolved root folder: " + root.getAbsolutePath());
			return root;
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
	}
}
