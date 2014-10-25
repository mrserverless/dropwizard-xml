package com.yunspace.dropwizard.xml;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.yunspace.dropwizard.xml.jackson.JacksonXML;
import com.yunspace.dropwizard.xml.jersey.jackson.JacksonXMLMessageBodyProvider;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class XmlBundle implements Bundle {

    private final XmlMapper xmlMapper;

    public XmlBundle() {
        this.xmlMapper = JacksonXML.newXMLMapper();
    }

    public XmlBundle(final JacksonXmlModule jacksonXmlModule) {
        this.xmlMapper = JacksonXML.newXMLMapper( jacksonXmlModule );
    }

    @Override
    public void initialize(final Bootstrap<?> bootstrap) {

    }

    @Override
    public void run(final Environment environment) {
        environment.jersey().register(
                new JacksonXMLMessageBodyProvider( xmlMapper, environment.getValidator()));
    }

    public XmlMapper getXmlMapper () {
        return xmlMapper;
    }
}
