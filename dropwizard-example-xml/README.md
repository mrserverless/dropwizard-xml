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

* To hit the Hello World example (hit refresh a few times).

	http://localhost:8080/hello-world

* To post data into the application.

	curl -H "Content-Type: application/json" -X POST -d '{"fullName":"Other Person","emailAddress":"Other Title"}' http://localhost:8080/people
	
	open http://localhost:8080/people
