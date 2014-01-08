#Dropwizard + XML
[Dropwizard](https://github.com/dropwizard/dropwizard) extension for high performance processing of XML input and output

Uses:
* [Jackson XML Provider](https://github.com/FasterXML/jackson-jaxrs-xml-provider) for JAX-RS MessageBodyReader and MessageBodyWriter implementation
* [Jackson DataFormat XML](https://github.com/FasterXML/jackson-dataformat-xml) for XMLMapper
* [Woodstox](http://wiki.fasterxml.com/WoodstoxHome) for high performance XML processing under the hood
* [Gradle](http://www.gradle.org/) for the best of Ant + Maven, minus all the crap.

##Why
JSON is awesome. But understandably, XML files are deeply rooted into the IT ecosystem of many organisations. It would be
great if we can easily leverage the power and simplicity of Dropwizard to b
