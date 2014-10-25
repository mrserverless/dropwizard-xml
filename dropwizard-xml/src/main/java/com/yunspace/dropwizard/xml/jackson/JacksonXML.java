package com.yunspace.dropwizard.xml.jackson;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import io.dropwizard.jackson.AnnotationSensitivePropertyNamingStrategy;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.FuzzyEnumModule;
import io.dropwizard.jackson.GuavaExtrasModule;
import io.dropwizard.jackson.LogbackModule;

public class JacksonXML {
    private JacksonXML() { /* singleton */ }

    public static XmlMapper newXMLMapper() {

        return newXMLMapper(new JacksonXmlModule());
    }

    /**
     * Creates a new {@link com.fasterxml.jackson.dataformat.xml.XmlMapper} using Woodstox
     * with Logback and Joda Time support.
     * Also includes all {@link io.dropwizard.jackson.Discoverable} interface implementations.
     *
     * @return XmlMapper
     */
    public static XmlMapper newXMLMapper(JacksonXmlModule jacksonXmlModule) {

        final XmlFactory woodstoxFactory = new XmlFactory(new WstxInputFactory(), new WstxOutputFactory());
        final XmlMapper mapper = new XmlMapper(woodstoxFactory, jacksonXmlModule);

        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new LogbackModule());
        mapper.registerModule(new GuavaExtrasModule());
        mapper.registerModule(new JodaModule());
        mapper.registerModule(new FuzzyEnumModule());
        mapper.setPropertyNamingStrategy(new AnnotationSensitivePropertyNamingStrategy());
        mapper.setSubtypeResolver(new DiscoverableSubtypeResolver());

        return mapper;
    }
}

