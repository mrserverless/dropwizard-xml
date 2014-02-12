package com.yunspace.dropwizard.jersey.jackson.xml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.core.util.StringKeyObjectValueIgnoreCaseMultivaluedMap;
import com.yunspace.dropwizard.jackson.xml.JacksonXML;
import org.junit.Test;

import javax.validation.Validation;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * this test is created to show that it is easier to use JAXB to annotate
 * unwrapped lists instead of Jackson due to issue:
 * https://github.com/FasterXML/jackson-dataformat-xml/issues/101
 *
 */

public class UnwrappedAttributeTest {
    private static final Annotation[] NONE = new Annotation[0];

    //@XmlAccessorType(XmlAccessType.FIELD)
    @JacksonXmlRootElement(localName = "root")
    //@XmlRootElement(name = "root")
    static class Root {

//        @JacksonXmlProperty(localName = "unwrapped")
//        @JacksonXmlElementWrapper(useWrapping = false)
        @XmlElement(name = "unwrapped")
        public List<UnwrappedElement> unwrapped;

        @JacksonXmlProperty(localName = "name")
        public String name;

        public static class UnwrappedElement {
            @JacksonXmlProperty(isAttribute = true)
            public String id;

            @JacksonXmlProperty(isAttribute = true)
            public String type;
        }
    }

    protected final static XmlMapper xmlMapper = JacksonXML.newXMLMapper();
    protected final static JacksonXMLMessageBodyProvider provider = new JacksonXMLMessageBodyProvider(xmlMapper, Validation.buildDefaultValidatorFactory().getValidator());
    protected final String rootXml = "<root><unwrapped id=\"1\" type=\"string\"/><unwrapped id=\"2\" type=\"string\"/><name>text</name></root>";
    protected final Root rootObject = new Root();


    @Test
    public void deserializeTest () throws Exception {
        final ByteArrayInputStream requestList = new ByteArrayInputStream(rootXml.getBytes());
        final Class<?> klass = Root.class;
        final Object obj = provider.readFrom((Class<Object>) klass,
                Root.class,
                NONE,
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedMapImpl(),
                requestList);

    }

    @Test
    public void serializeTest () throws Exception {
        Root root = xmlMapper.readValue(rootXml, Root.class);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final Class<?> klass = Root.class;

        provider.writeTo(rootObject,
                klass,
                klass,
                NONE,
                MediaType.APPLICATION_XML_TYPE,
                new StringKeyObjectValueIgnoreCaseMultivaluedMap(),
                output);

        assertThat(root).isEqualsToByComparingFields(rootObject);
    }

}