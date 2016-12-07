# Maestrano Java DemoApp

This application shows examples of how to use the Maestrano Java API. A live version of this application is available at this address:

http://java-demoapp.maestrano.io 

That may be tested from our sandbox environment:

https://sandbox.maestrano.com

## Running the application locally

Go to https://developer.maestrano.com

Create a new Application, and a new Environment

Retrieve your API Key and Secret

Set up the App endpoints:
- Host: http://localhost:1234
- IDM: http://localhost:1234
- SSO Init Path: /maestrano/auth/saml/init/%{marketplace}
- SSO Consume Path: /maestrano/auth/saml/init/%{marketplace}

Build with:
```
mvn package
```
Then run it with (application uses an embedded tomcat):

Linux
```
export ENVIRONMENT_NAME=<your environment name>
export ENVIRONMENT_KEY=<your environment key>
export ENVIRONMENT_SECRET=<your environment name>
export PORT=1238
sh target/bin/webapp
```

Windows
```
set ENVIRONMENT_NAME=<your environment name>
set ENVIRONMENT_KEY=<your environment key>
set ENVIRONMENT_SECRET=<your environment name>
set PORT=1238
target/bin/webapp.bat
```

Go to the platform sandbox:
https://sandbox.maestrano.com

Add your App from the marketplace.

Start the application

