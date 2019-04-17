# COMP1206: Sushi Coursework 2

## Your submission:
- Your submission zip file structure must match the provided sushi.zip file
- There must be a pom.xml in the root of the zip
- There must be a progress.txt in the root of the zip, updated to reflect the progress you have made
- The main source tree must continue to exist in src/main/java

## Main source tree
The main source tree can be found in src/main/java

## Your application:
- You must keep the packages as provided. You may add new packages or add to them, but not change the names of the provided packages.
- You must not change the names of the provided classes
- You must not change the names of the provided methods
- You must not modify the Launcher
- You must not modify the interfaces
- You must not modify the ServerApplication, ClientApplication, ServerWindow or ClientWindow
- It must be possible to build a jar file using Maven from your source as below
- It must be possible to launch your application as below

## Dependencies:
- You must have a valid pom.xml file which contains all needed dependencies
- Executing mvn install will build /target/sushi-2.jar in the target folder correctly.
- It is possible to run just the sushi-2.jar directly with no parameters from the target folder.
0 Any and all dependencies used must be specified in Maven and installed in the lib folder using the maven-dependency-plugin plugin, as provided in the default pom.xml

## Execution: 
- Executing java -jar target/sushi-2.jar server will start the server
- Executing java -jar sushi-2.jar client will start the client.

## Creating the jar file
    mvn install

## Running
    java -jar target/sushi-2.jar
    java -jar target/sushi-2.jar server
    java -jar target/sushi-2.jar client

