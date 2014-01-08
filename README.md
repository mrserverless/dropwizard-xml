#Dropwizard + XML
[Dropwizard](https://github.com/dropwizard/dropwizard) extension for XML input and ouputs.

Uses:
* [Jackson XML Provider](https://github.com/FasterXML/jackson-jaxrs-xml-provider) for JAX-RS MessageBodyReader and MessageBodyWriter implementation
* [Jackson DataFormat XML](https://github.com/FasterXML/jackson-dataformat-xml) for XMLMapper, equivalent to ObjectMapper in JSON
* [Woodstox](http://wiki.fasterxml.com/WoodstoxHome) for high performance XML processing under the hood

##Why
JSON is awesome. But understandably, XML files are deeply rooted into the IT ecosystem of many organisations. It would be
great if we can easily leverage the power and simplicity of Dropwizard to build XML RESTful web services, instead of
relying on bloated frameworks and chunky application servers. That's what this project aims to do.

##Noteworthy
This project is compatible with Dropwizard 0.7.0 and above only, which supports Jackson 2.2.3. The previous version of
Dropwizard uses Jackson 2.1.4 which contains a show stopper bug with XML unwrapped lists. See jackson-dataformat-xml
[issue-58](https://github.com/FasterXML/jackson-dataformat-xml/issues/58)

Certain configurations such as Indentation are switched on by default. Will attempt to make these configurable in the
future.

##Usage
Unfortunately there is no maven repository yet. For now, simply download the project, run gradle or gradle wrapper to compile and install
to your local maven repository:

    gradlew install

To use the XML Provider, simply register it in your Application.Run() method:

    env.jersey().register(new JacksonXMLMessageBodyProvider(JacksonXML.newXMLMapper(), env.getValidator()));

Don't forget to annotate your Resources accordingly with XML mediatype:

    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)

Sample project coming soon.