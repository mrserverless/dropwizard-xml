# Introduction

This is a simplified version of the main dropwizard-example project. The main purposes is to
demonstrate the usage of the Jackson Xml Message Provider.

See [Dropwziard Example][https://github.com/dropwizard/dropwizard/tree/master/dropwizard-example]
for more detailed overview of the other sample features.

# Running The Application

To test the example application run the following commands.

* To package the example run.

        gradlew shadowJar

* To setup the h2 database run.

        java -jar build/distributions/dropwizard-example-xml-0.5.0.jar db migrate helloxml.yml

* To run the server run.

        java -jar build/distributions/dropwizard-example-xml-0.5.0.jar server example.yml

* To hit the Hello XML Pirates example (hit refresh a few times).

	    http://localhost:8080/pirates

* To post data into the application. Use [PostMan][http://www.getpostman.com/] REST Client would be the easiest way.

        load the HellXMLPirates.json collection using PostMan

* To reset the database:

        java -jar build/distributions/dropwizard-example-xml-0.5.0.jar db drop-all helloxml.yml --confirm-delete-everything

