package com.yunspace.dropwizard.xml.jersey.jackson.xml;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

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

        @JacksonXmlProperty(localName = "unwrapped")
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<UnwrappedElement> unwrapped;

        @JacksonXmlProperty(localName = "unwrapped")
        public String name2;

    }

    @JacksonXmlRootElement(localName = "unwrapped")
    public static class UnwrappedElement {

        public UnwrappedElement() {
            // for Jackson deserialization
        }

        public UnwrappedElement (String id, String name) {
            this.id = id;
            this.name = name;
        }

        @JacksonXmlProperty(isAttribute = true, localName = "id")
        public String id;

        @JacksonXmlProperty(isAttribute = true, localName = "type")
        public String name;
    }

    protected final XmlMapper xmlMapper = new XmlMapper();
    protected final String rootXml =
            "<root>"  + System.lineSeparator() +
                    "  <unwrapped id=\"1\" name=\"string\"/>"  + System.lineSeparator() +
                    "  <name2>text</name2>"  + System.lineSeparator() +
            "</root>";

    protected final Root rootObject = new Root();

    @Before
    public void setUp() {
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT).registerModule(new JaxbAnnotationModule());

        rootObject.unwrapped = Arrays.asList(
                new UnwrappedElement("1", "string")
        );
        rootObject.name2 = "text";
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

