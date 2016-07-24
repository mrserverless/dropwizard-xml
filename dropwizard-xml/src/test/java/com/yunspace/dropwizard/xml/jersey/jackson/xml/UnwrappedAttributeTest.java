package com.yunspace.dropwizard.xml.jersey.jackson.xml;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * this test is created for Jackson Dataformat XML
 * <a href="https://github.com/FasterXML/jackson-dataformat-xml/issues/101">Issue 101</a>
 *
 */

public class UnwrappedAttributeTest {

    @JacksonXmlRootElement(localName = "root")
    private static class Root {

        Root() {}

        @JacksonXmlProperty
        @JacksonXmlElementWrapper(useWrapping = false)
        List<UnwrappedElement> unwrapped;

        @JacksonXmlProperty
        String name;

    }

    @JacksonXmlRootElement(localName = "unwrapped")
    private static class UnwrappedElement {

        @JacksonXmlProperty(isAttribute = true)
        String id;

        @JacksonXmlProperty(isAttribute = true)
        String type;

    }

    private final XmlMapper xmlMapper = new XmlMapper();
    private final String rootXml =
            "<root>"  + System.lineSeparator() +
                    "  <unwrapped id=\"1\" type=\"string\"/>"  + System.lineSeparator() +
                    "  <unwrapped id=\"2\" type=\"string\"/>"  + System.lineSeparator() +
                    "  <name>text</name>"  + System.lineSeparator() +
            "</root>" + System.lineSeparator();

    private Root givenRootObject() {
        Root rootObject = new Root();

        UnwrappedElement unwrapped1 = new UnwrappedElement();
        unwrapped1.id="1";
        unwrapped1.type="string";

        UnwrappedElement unwrapped2 = new UnwrappedElement();
        unwrapped2.id="2";
        unwrapped2.type="string";

        rootObject.unwrapped = Arrays.asList(unwrapped1, unwrapped2);
        rootObject.name = "text";

        return rootObject;
    }

    @Before
    public void setUp() {
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT).registerModule(new JaxbAnnotationModule());
    }

//    @Ignore
    @Test
    public void serializeUnwrappedList () throws Exception {
        // then
        assertThat(xmlMapper.writeValueAsString(givenRootObject())).isEqualTo(rootXml);
    }

//    @Ignore
    @Test
    public void deserializeUnwrappedList () throws Exception {
        // given
        Root rootObject = givenRootObject();

        // when
        Root rootResult =  xmlMapper.readValue(rootXml, Root.class);

        // then
        assertThat(rootResult.name).isEqualTo(rootObject.name);
        assertThat(rootResult.unwrapped).hasSize(2);
        assertThat(rootResult.unwrapped.get(0)).isEqualToComparingFieldByField(rootObject.unwrapped.get(0));

    }
}

