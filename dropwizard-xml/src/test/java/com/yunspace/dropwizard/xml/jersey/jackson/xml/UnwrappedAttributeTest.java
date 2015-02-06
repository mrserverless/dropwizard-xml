package com.yunspace.dropwizard.xml.jersey.jackson.xml;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * this test is created for Jackson Dataformat XML
 * <a href="https://github.com/FasterXML/jackson-dataformat-xml/issues/101">Issue 101</a>
 *
 */

public class UnwrappedAttributeTest {

    @JacksonXmlRootElement(localName = "root")
    public static class Root {

        public Root() {}

        @JacksonXmlProperty
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<UnwrappedElement> unwrapped;

        public String name;

    }

    @JacksonXmlRootElement(localName = "unwrapped")
    public static class UnwrappedElement {

        @JacksonXmlProperty(isAttribute = true)
        public String id;

        @JacksonXmlProperty(isAttribute = true)
        public String type;

    }

    protected final XmlMapper xmlMapper = new XmlMapper();
    protected final String rootXml =
            "<root>"  + System.lineSeparator() +
                    "  <unwrapped id=\"1\" type=\"string\"/>"  + System.lineSeparator() +
                    "  <name>text</name>"  + System.lineSeparator() +
            "</root>";

    protected final Root rootObject = new Root();

    @Before
    public void setUp() {
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT).registerModule(new JaxbAnnotationModule());

        UnwrappedElement unwrapped = new UnwrappedElement();
        unwrapped.id="1";
        unwrapped.type="string";
        rootObject.unwrapped = Arrays.asList(unwrapped);
        rootObject.name = "text";
    }

    @Test
    public void serializeTest () throws Exception {
        assertThat(xmlMapper.writeValueAsString(rootObject)).isEqualTo(rootXml);
    }

    @Test
    public void derializeTest () throws Exception {

        Root rootResult =  xmlMapper.readValue(rootXml, Root.class);

        assertThat(rootResult).isEqualsToByComparingFields(rootObject);
    }
}

