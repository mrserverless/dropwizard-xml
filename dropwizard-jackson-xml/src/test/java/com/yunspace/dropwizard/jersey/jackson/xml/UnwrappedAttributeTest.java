package com.yunspace.dropwizard.jersey.jackson.xml;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.core.util.StringKeyObjectValueIgnoreCaseMultivaluedMap;
import com.yunspace.dropwizard.jackson.xml.JacksonXML;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
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
    private static final Annotation[] NONE = new Annotation[0];

    @XmlRootElement(name = "root")
    static class Root {

        @XmlElement(name = "unwrapped")
        public List<UnwrappedElement> unwrapped;

        @XmlElement(name = "name")
        public String name;

        public static class UnwrappedElement {
            public UnwrappedElement () {}

            public UnwrappedElement (String id, String type) {
                this.id = id;
                this.type = type;
            }

            @XmlAttribute(name = "id")
            public String id;

            @XmlAttribute(name = "type")
            public String type;
        }
    }

    protected final static XmlMapper xmlMapper = JacksonXML.newXMLMapper();
    protected final static JacksonJaxbXMLProvider provider = new JacksonJaxbXMLProvider(xmlMapper, JacksonJaxbXMLProvider.DEFAULT_ANNOTATIONS);
    protected final String rootXml = "<root>"  + System.lineSeparator() +
            "  <unwrapped id=\"1\" type=\"string\"/>"  + System.lineSeparator() +
            "  <unwrapped id=\"2\" type=\"string\"/>"  + System.lineSeparator() +
            "  <name>text</name>"  + System.lineSeparator() +
            "</root>";
    protected final Root rootObject = new Root();

    @Before
    public void setUp() {
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        rootObject.unwrapped = Arrays.asList(
                new Root.UnwrappedElement("1", "string"),
                new Root.UnwrappedElement("2", "string")
        );
        rootObject.name = "text";
    }


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

        assertThat(obj).isInstanceOf(Root.class);
        Root root = (Root) obj;
        assertThat(root).isEqualsToByComparingFields(rootObject);
    }

    @Test
    public void serializeTest () throws Exception {

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final Class<?> klass = Root.class;

        provider.writeTo(rootObject,
                klass,
                klass,
                NONE,
                MediaType.APPLICATION_XML_TYPE,
                new StringKeyObjectValueIgnoreCaseMultivaluedMap(),
                output);

        assertThat(output.toString()).isEqualTo(rootXml);
    }

}