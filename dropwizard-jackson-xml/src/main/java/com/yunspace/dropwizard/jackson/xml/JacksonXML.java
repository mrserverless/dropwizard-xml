package com.yunspace.dropwizard.jackson.xml;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.FuzzyEnumModule;
import io.dropwizard.jackson.LogbackModule;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;

/**
 * Created by Yun Zhi Lin on 8/01/14.
 */
public class JacksonXML {
    private JacksonXML() { /* singleton */ }


    /**
     * Creates a new {@link com.fasterxml.jackson.dataformat.xml.XmlMapper} using Woodstox
     * with Logback and Joda Time support. Indentation is on by default
     * Also includes all {@link io.dropwizard.jackson.Discoverable} interface implementations.
     */
    public static XmlMapper newXMLMapper() {

        final XmlFactory woodstoxFactory = new XmlFactory(new WstxInputFactory(), new WstxOutputFactory());
        final XmlMapper mapper = new XmlMapper (woodstoxFactory, new JacksonXmlModule());

        mapper.enable(SerializationFeature.INDENT_OUTPUT); //TODO make indentation configurable
        mapper.registerModule(new LogbackModule()); //logging
        mapper.registerModule(new JodaModule()); //joda-teim
        mapper.registerModule(new FuzzyEnumModule()); // deserializing enums
        mapper.setSubtypeResolver(new DiscoverableSubtypeResolver());

        return mapper;
    }
}

