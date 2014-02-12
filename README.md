# Dropwizard XML Provider
[Dropwizard](https://github.com/dropwizard/dropwizard) extension for high performance processing of XML inputs and outputs

Uses:
* [Jackson XML Provider](https://github.com/FasterXML/jackson-jaxrs-xml-provider) for JAX-RS MessageBodyReader and MessageBodyWriter implementations
* [Jackson DataFormat XML](https://github.com/FasterXML/jackson-dataformat-xml) for XMLMapper, which inherits from the Jackson ObjectMapper
* [Woodstox](http://wiki.fasterxml.com/WoodstoxHome) for high performance XML processing under the hood
* [Gradle](http://www.gradle.org/) for the best of Ant plus Maven IMHO.

## Why
Dropwizard is fast, container-less and JSON is awesome. But understandably, XML files are deeply rooted into the IT ecosystem of many organisations. It would be great if the power and simplicity of Dropwizard to build XML RESTful web services, instead of relying on bloated frameworks and chunky application servers. That's what this project aims to do.

## Noteworthy
Dropwizard-xml is compatible with Dropwizard 0.7.0-rc1 and above only. Dropwizard 0.7 uses Jackson 2.3.0. The previous Dropwizard release uses Jackson 2.1.4 which contains a show stopper bug with XML unwrapped lists. See jackson-dataformat-xml
[ISSUE-58](https://github.com/FasterXML/jackson-dataformat-xml/issues/58)

For unwrapped lists containing elements with multiple attributes, it's easier to use JAXB annotation.
[ISSUE-101](https://github.com/FasterXML/jackson-dataformat-xml/issues/101)

Certain configurations such as Indentation are switched on by default. Will attempt to make these configurable in the
future.

## Usage
Unfortunately there is no maven repository yet. For now, simply download the project, run gradlew or gradlew.bat to compile and install to your local maven repository:

    gradlew install

To use the XML Provider, simply register it in your Application.Run() method:

    env.jersey().register(new JacksonXMLMessageBodyProvider(JacksonXML.newXMLMapper(), env.getValidator()));

Annotate your Resources accordingly with XML mediatype:

    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)

Annotate your model POJO with either Jackson bindings:

    @JacksonXmlRootElement
    @JacksonXmlProperty
    @JacksonXmlElementWrapper
    @JacksonXmlText

or JAXB:

    @XmlType
    @XmlAttribute
    etc

And you can use the same validator annotations as you would with a JSON POJO when working with Dropwizard:

    @NotNull
    @Min
    etc

Sample project coming soon.