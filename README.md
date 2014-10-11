# Dropwizard XML Provider
[Dropwizard](https://github.com/dropwizard/dropwizard) extension for high performance processing and validation of XML

Uses:
* [Jackson XML Provider](https://github.com/FasterXML/jackson-jaxrs-xml-provider) Jackson XML Provider with woodstox under the hood.
* [Hibernate Validator](http://hibernate.org/validator/) same as JSON validation for Dropwizard 

## Dependencies
This project is pegged against Dropwizard's release number and try to use the same Jackson dependency to avoid conflicts.

| Dropwizard-XML   | Dropwizard     | Jackson   |
| ---------------- | -------------- | --------- |
| 0.7.0.1          | 0.7.0          | 2.3.2     |
| 0.7.1.1          | 0.7.1          | 2.3.3     |
| 0.8.0.1-SNAPSHOT | 0.8.8-SNAPSHOT | 2.4.1     |

## Usage
Download the project, run gradlew or gradlew.bat to compile and install to your local maven repository:

    gradlew install

Add the dependency to your project by Maven:

    <dependency>
        <groupId>com.yunspace</groupId>
        <artifactId>dropwizard-jackson-xml</artifactId>
        <version>0.7.1.1</version>
        <scope>compile</scope>
    </dependency>

Or Gradle:

    compile 'com.yunspace.dropwizard:dropwizard-jackson-xml:0.7.1.1

Register the XML Provider in your Application.Run() method:

    env.jersey().register(new JacksonXMLMessageBodyProvider(JacksonXML.newXMLMapper(), env.getValidator()));

Annotate your Resources with application/xml mediatype:

    @Produces(MediaType.APPLICATION_XML) @Consumes(MediaType.APPLICATION_XML)

Annotate your model POJO with Jackson XML bindings (See jackson-dataformat-xml project):

    @JacksonXmlRootElement @JacksonXmlProperty @JacksonXmlElementWrapper @JacksonXmlText

Use validation/ignore annotations as you would normally in Dropwizard:

    @NotNull @Min @JsonIgnore

##Sample project
See dropwizard-example-xml subproject.
