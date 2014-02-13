package com.yunspace.dropwizard.jersey.jackson.xml;

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
 * this test is created to show that it is easier to use JAXB to annotate
 * unwrapped lists instead of Jackson due to issue:
 * https://github.com/FasterXML/jackson-dataformat-xml/issues/101
 *
 */

public class UnwrappedAttributeTest {

    @JacksonXmlRootElement(localName = "root")
    public class Root {

        public Root() {}

        @JacksonXmlProperty(localName = "unwrapped")
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<UnwrappedElement> unwrapped;

        @JacksonXmlProperty(localName = "name")
        public String name;

    }

    @JacksonXmlRootElement(localName = "unwrapped")
    public class UnwrappedElement {

        public UnwrappedElement() {

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
            "  <unwrapped id=\"2\" name=\"string\"/>"  + System.lineSeparator() +
            "  <name>text</name>"  + System.lineSeparator() +
            "</root>";

    protected final Root rootObject = new Root();

    @Before
    public void setUp() {
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT).registerModule(new JaxbAnnotationModule());

        rootObject.unwrapped = Arrays.asList(
                new UnwrappedElement("1", "string"),
                new UnwrappedElement("2", "string")
        );
        rootObject.name = "text";
    }

    @Test
    public void serializeTest () throws Exception {
        assertThat(xmlMapper.writeValueAsString(rootObject)).isEqualTo(rootXml);
    }

    @Test
    public void derializeTest () throws Exception {

        Root rootResult = (Root) xmlMapper.readValue(rootXml, Root.class);

        assertThat(rootResult).isEqualsToByComparingFields(rootObject);
    }
}

