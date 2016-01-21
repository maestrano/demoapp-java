# Maestrano Java DemoApp

This application shows examples of how to use the Maestrano Java API. A live version of this application is available at this address:

http://java-demoapp.maestrano.io

## Running the application locally

First build with:

    $mvn clean install

Then run it with (application uses embedded jetty):

    $java -jar target/demoapp-java-1.0-SNAPSHOT-jar-with-dependencies.jar

If you need to use a special port

    $DEMO_PORT=1234 java -jar target/demoapp-java-1.0-SNAPSHOT-jar-with-dependencies.jar