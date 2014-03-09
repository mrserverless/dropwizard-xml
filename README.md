# Dropwizard XML Provider
[Dropwizard](https://github.com/dropwizard/dropwizard) extension for high performance processing of XML inputs and outputs

Uses:
* [Jackson XML Provider](https://github.com/FasterXML/jackson-jaxrs-xml-provider) for JAX-RS MessageBodyReader and MessageBodyWriter implementations
* [Jackson DataFormat XML](https://github.com/FasterXML/jackson-dataformat-xml) for XMLMapper, which inherits from the Jackson ObjectMapper
* [Woodstox](http://wiki.fasterxml.com/WoodstoxHome) for high performance XML processing under the hood
* [Gradle](http://www.gradle.org/) for the best of Ant plus Maven IMHO.

## Why
Dropwizard is fast, war-less and JSON with Jackson is awesome. But understandably, XML files are deeply rooted into the IT ecosystem of many organisations. It would be great to leverage the power and simplicity of Dropwizard to build XML RESTful web services, instead of relying on bloated frameworks and chunky application servers. That's what this project aims to do.

## Dependencies
This project is pegged against Dropwizard's minior release number and the same Jackson dependency as Dropwizard to avoid conflicts.

| Dropwizard-XML  | Dropwizard   | Jackson   |
| --------------- | ------------ | --------- |
| v0.5.0          | v0.7.0-rc1   | 2.3.0     |
| v0.7.0          | v0.7.0-rc2   | 2.3.0     |
| v0.7.1          | v0.7.0-rc2   | 2.3.1     |

## Usage
Unfortunately there is no maven repository yet. For now, simply download the project, run gradlew or gradlew.bat to compile and install to your local maven repository:

    gradlew install

To use the XML Provider, simply register it in your Application.Run() method:

    env.jersey().register(new JacksonXMLMessageBodyProvider(JacksonXML.newXMLMapper(), env.getValidator()));

Annotate your Resources accordingly with XML mediatype:

    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)

Annotate your model POJO with either Jackson XML bindings (See jackson-dataformat-xml project):

    @JacksonXmlRootElement
    @JacksonXmlProperty
    @JacksonXmlElementWrapper
    @JacksonXmlText

or JAXB (need to enable annotation introspector, see Jackson manual for more details):

    @XmlType
    @XmlAttribute

And you can use the same validator/ignore annotations as you would with a JSON POJO when working with Dropwizard:

    @NotNull
    @Min
    @JsonIgnore

##Sample project
See dropwizard-example-xml subproject.

## Known Issues
Dropwizard-xml is compatible with Dropwizard 0.7.X and above only. Dropwizard 0.7 uses Jackson 2.3.0. The previous Dropwizard 0.6.2 stable release uses Jackson 2.1.4 which contains a show stopper bug with XML unwrapped lists. See jackson-dataformat-xml
[ISSUE-58](https://github.com/FasterXML/jackson-dataformat-xml/issues/58)

Also for unwrapped lists containing elements with attributes, there is an oustanding bug where lists must be placed after non-list elements
[ISSUE-101](https://github.com/FasterXML/jackson-dataformat-xml/issues/101) You can try and work around by placing your lists after elements and attributes in your POJO.

