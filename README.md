# Dropwizard XML Bundle

[![Build Status](https://snap-ci.com/yunspace/dropwizard-xml/branch/master/build_image)](https://snap-ci.com/yunspace/dropwizard-xml/branch/master)
[![Download](https://api.bintray.com/packages/yunspace/dropwizard/dropwizard-xml/images/download.svg)](https://bintray.com/yunspace/dropwizard/dropwizard-xml/_latestVersion)
<a href='https://bintray.com/yunspace/dropwizard/dropwizard-xml/view?source=watch' alt='Get automatic notifications about new "dropwizard-xml" versions'><img src='https://www.bintray.com/docs/images/bintray_badge_color.png'></a>

[Dropwizard](https://github.com/dropwizard/dropwizard) extension for high performance processing and validation of XML.

Uses:
* [Jackson XML Provider](https://github.com/FasterXML/jackson-jaxrs-xml-provider) Jackson XML Provider with woodstox under the hood.
* [Hibernate Validator](http://hibernate.org/validator/) same Dropwizard validation behvaiour for XML 

## Status
This project is built using [Snap-CI](https://www.snap-ci.com/) to enable [Continuous Delivery](http://www.thoughtworks.com/continuous-delivery).
There are no mysterious snapshots, every time the tests pass a new release tagged and uploaded to bintray, so you know exactly what you are pulling down. 

Until 0.9.0, all versions are are pegged against Dropwizard's release number and try to use the same Jackson dependency to avoid conflicts. 
See above badges for latest version to use. The table below give an indication of dependencies:

| Dropwizard-XML   | Dropwizard     | Jackson   | Woodstox | Stax  |
| ---------------- | -------------- | --------- | -------- |------ |
| 0.7.1-X          | 0.7.1          | 2.3.3     | 4.1.4    | 3.1.1 |
| 0.8.0-X          | 0.8.0          | 2.5.1     |    transitive    |
| 0.8.1-X          | 0.8.1          | 2.5.2     |    transitive    |
| 0.9.0-X          | 0.9.0          | 2.6.3     |    transitive    |

After 0.9.0, the Dropwizard and Jackson dependencies are no longer bundled with this library. They are specified as 
gradle `compileOnly` scope (`provided` scope in Maven). It is up to you to provide the correct Dropwizard version 
and Jackson version combination in your classpath:

| Dropwizard-XML   | Provide Dropwizard | Provide Jackson  | 
| ---------------- | ------------------ | ---------------- |
| 39               | 0.9.1, 0.9.2       | 2.6.3            | 
| 39               | 0.9.3              | 2.6.7            |
| 40+              | 1.0.0+             | 2.7.5+           |

See [dependencies](#Dependencies) below fore more details.

## Dependencies
Dropwizard XML Provider is hosted by [Bintray JCenter](https://bintray.com/bintray/jcenter).

You can add the dependency to your project by Maven:

    <repositories>
        <repository>
            <id>jcenter</id>
            <url>http://jcenter.bintray.com</url>
        </repository>
    </repositories>
    <dependency>
        <groupId>com.yunspace.dropwizard</groupId>
        <artifactId>dropwizard-xml</artifactId>
        <version>${dropwizardXmlVersion}</version>
        <scope>compile</scope>
    </dependency>
    
    <!-- provided dependencies -->
    <dependency>
        <groupId>io.dropwizard</groupId>
        <artifactId>dropwizard-core</artifactId>
        <version>1.0.0-rc4</version>
        <scope>compile</scope>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.jaxrs</groupId>
        <artifactId>jackson-jaxrs-xml-provider</artifactId>
        <version>2.7.5</version>
        <scope>compile</scope>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.dataformat</groupId>
        <artifactId>jackson-dataformat-xml</artifactId>
        <version>2.7.5</version>
        <scope>compile</scope>
    </dependency>
    
Or Gradle:

    repositories {
        jcenter()
        mavenCentral()
    }
    dependencies {
        compile "com.yunspace.dropwizard:dropwizard-xml:${dropwizardXmlVersion}"
        compile "io.dropwizard:dropwizard-core:1.0.0-rc4"
        compile "com.fasterxml.jackson.jaxrs:jackson-jaxrs-xml-provider:2.7.5"
        compile "com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.7.5"
    }
    
If you are using less than Dropwizard `1.0.0` and Jackson `2.7.5` you need to add [jackson-datatype-jdk7](https://github.com/FasterXML/jackson-datatype-jdk7)

Maven: 

    <dependency>
        <groupId>com.fasterxml.jackson.datatype</groupId>
        <artifactId>jackson-datatype-jdk7</artifactId>
        <version>2.6.7</version>
        <scope>compile</scope>
    </dependency>

Gradle:

    compile "com.fasterxml.jackson.datatype:jackson-datatype-jdk7:2.6.7"

## Usage 

Add the XMLBundle

    bootstrap.addBundle(new XmlBundle());

Annotate your Resources:

    @Produces(MediaType.APPLICATION_XML) @Consumes(MediaType.APPLICATION_XML)

Annotate your model POJO with jackson-dataformat-xml bindings:

    @JacksonXmlRootElement @JacksonXmlProperty @JacksonXmlElementWrapper @JacksonXmlText

Use validation/ignore annotations as you would normally:

    @NotNull @Min @JsonIgnore

## Advanced Usage

You can further custom the behaviour of your XML Mapper by passing in a JacksonXmlModule:

    bootstrap.addBundle(new XmlBundle(jacksonXmlModule));

Or enable various serialisation/deserialisation features

    XmlBundle indentXmlBundle = new XmlBundle();
    indentXmlBundle.getXmlMapper().enable(SerializationFeature.INDENT_OUTPUT);
    bootstrap.addBundle(indentXmlBundle);

##Sample project
See [dropwizard-xml-example](https://github.com/yunspace/dropwizard-xml/tree/master/dropwizard-xml-example) subproject.

## TODO
Add support for [aalto](https://github.com/FasterXML/aalto-xml)