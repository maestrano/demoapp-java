# Maestrano Java DemoApp

This application shows examples of how to use the Maestrano Java API. A live version of this application is available at this address:

http://java-demoapp.maestrano.io 

That may be tested from our sandbox environment:

https://sandbox.maestrano.com

Please follow the documentation described here:

https://maestrano.atlassian.net/wiki/display/DEV/Integrate+your+app+on+partner%27s+marketplaces

## Running the application locally

Go to https://developer.maestrano.com

Create a new Application, and a new Environment. Make sure your environment is associated to the Marketplace: Maestrano Singapore.

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
export MNO_DEVPL_ENV_NAME=<your environment name>
export MNO_DEVPL_ENV_KEY=<your environment key>
export MNO_DEVPL_ENV_SECRET=<your environment name>
export PORT=1234
sh target/bin/webapp
```

Windows
```
set MNO_DEVPL_ENV_NAME=<your environment name>
set MNO_DEVPL_ENV_KEY=<your environment key>
set MNO_DEVPL_ENV_SECRET=<your environment name>
set PORT=1234
target/bin/webapp.bat
```

Go to the platform sandbox:
https://sandbox.maestrano.com

Add your App from the marketplace.

Start the application

