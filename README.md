# Maestrano Java DemoApp

This application shows examples of how to use the Maestrano Java API. A live version of this application is available at this address:

http://java-demoapp.maestrano.io/

## Running the application locally

First build with:

    $mvn clean install

Then run it with (application uses embedded jetty):

    $java -cp target/classes:target/dependency/* com.example.Main

