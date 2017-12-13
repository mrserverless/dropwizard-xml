package com.yunspace.dropwizard.xml.jersey.jackson.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;

import org.hamcrest.CoreMatchers;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.yunspace.dropwizard.xml.jackson.JacksonXML;
import com.yunspace.dropwizard.xml.jersey.jackson.JacksonXMLMessageBodyProvider;

import io.dropwizard.validation.ConstraintViolations;
import io.dropwizard.validation.Validated;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

@SuppressWarnings("unchecked")
public class JacksonXMLMessageBodyProviderTest {
    private static final Annotation[] NONE = new Annotation[0];

    private static class Example {
        @Min(0)
        @JacksonXmlProperty
        int id;
    }

    interface Partial1 {}

    interface Partial2 {}

    private static class PartialExample {
        @Min(value = 0, groups = Partial1.class)
        @JacksonXmlProperty
        int id;

        @NotNull(groups = Partial2.class)
        @JacksonXmlProperty
        String text;
    }

    //tests wrapped and unwrapped lists, to verify jackson-dataformat-xml
    //<a href="https://github.com/FasterXML/jackson-dataformat-xml/issues/58">Issue 58</a>
    private static class ListExample {

        @JacksonXmlProperty(localName = "wrappedList")
        List<String> wrappedList;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty()
        List<String> unwrappedList;

        static final String sampleXml =
                "<ListExample>\n"
                + "  <unwrappedList>4</unwrappedList>\n"
                + "  <unwrappedList>5</unwrappedList>\n"
                + "  <unwrappedList>6</unwrappedList>\n"
                + "  <wrappedList>\n"
                + "    <wrappedList>1</wrappedList>\n"
                + "    <wrappedList>2</wrappedList>\n"
                + "    <wrappedList>3</wrappedList>\n"
                + "  </wrappedList>\n"
                + "</ListExample>\n";
    }

    private static class AttributeExample {

        @JacksonXmlProperty(isAttribute = true)
        String version;

    }

    @JacksonXmlRootElement(localName = "NamespaceAndText", namespace = "lostInSpace")
    private static class NamespaceLocalNameExample {

        @JacksonXmlText
        @JacksonXmlElementWrapper(localName = "TextValue")
        String text;
    }

    @JsonIgnoreType(false)
    interface NonIgnorable extends Ignorable {

    }

    @JsonIgnoreType
    interface Ignorable {

    }

    private final XmlMapper mapper = JacksonXML.newXMLMapper();
    private final JacksonXMLMessageBodyProvider provider =
            new JacksonXMLMessageBodyProvider(mapper,
                    Validation.buildDefaultValidatorFactory().getValidator());

    @Before
    public void setUp() throws Exception {
        mapper.enable(INDENT_OUTPUT);
        Assume.assumeThat(Locale.getDefault().getLanguage(), CoreMatchers.is("en"));
    }

    @Test
    public void readsDeserializableTypes() throws Exception {
        assertThat(provider.isReadable(Example.class, null, null, null))
                .isTrue();
    }

    @Test
    public void writesSerializableTypes() throws Exception {
        assertThat(provider.isWriteable(Example.class, null, null, null))
                .isTrue();
    }

    @Test
    public void doesNotWriteIgnoredTypes() throws Exception {
        assertThat(provider.isWriteable(Ignorable.class, null, null, null))
                .isFalse();
    }

    @Test
    public void writesUnIgnoredTypes() throws Exception {
        assertThat(provider.isWriteable(NonIgnorable.class, null, null, null))
                .isTrue();
    }

    @Test
    public void doesNotReadIgnoredTypes() throws Exception {
        assertThat(provider.isReadable(Ignorable.class, null, null, null))
                .isFalse();
    }

    @Test
    public void readsUnIgnoredTypes() throws Exception {
        assertThat(provider.isReadable(NonIgnorable.class, null, null, null))
                .isTrue();
    }

    @Test
    public void isChunked() throws Exception {
        assertThat(provider.getSize(null, null, null, null, null))
                .isEqualTo(-1);
    }

    @Test
    public void deserializesRequestEntities() throws Exception {
        final ByteArrayInputStream entity = new ByteArrayInputStream("<Example xmlns=\"\"><id>1</id></Example>".getBytes());
        final Class<?> klass = Example.class;

        final Object obj = provider.readFrom((Class<Object>) klass,
                Example.class,
                NONE,
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedHashMap<>(),
                entity);

        assertThat(obj)
                .isInstanceOf(Example.class);

        assertThat(((Example) obj).id)
                .isEqualTo(1);
    }

    @Test
    public void deserializesRequestList() throws Exception {

        final ByteArrayInputStream requestList = new ByteArrayInputStream((
                ListExample.sampleXml).getBytes());
        final Class<?> klass = ListExample.class;

        final Object obj = provider.readFrom((Class<Object>) klass,
                ListExample.class,
                NONE,
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedHashMap<>(),
                requestList);

        assertThat(obj)
                .isInstanceOf(ListExample.class);

        assertThat(((ListExample) obj).unwrappedList)
                .isEqualTo(Arrays.asList("4", "5", "6"));

        assertThat(((ListExample) obj).wrappedList)
                .isEqualTo(Arrays.asList("1", "2", "3"));
    }

    @Test
    public void derializeEntityWithAttribute() throws Exception {

        final ByteArrayInputStream entity = new ByteArrayInputStream(
                "<AttributeAndTextExample xmlns=\"\" version=\"1.0\"/>".getBytes());
        final Class<?> klass = AttributeExample.class;

        final Object obj = provider.readFrom((Class<Object>) klass,
                AttributeExample.class,
                NONE,
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedHashMap<>(),
                entity);

        assertThat(obj)
                .isInstanceOf(AttributeExample.class);

        assertThat(((AttributeExample) obj).version)
                .isEqualTo("1.0");
    }

    @Test
    public void returnsPartialValidatedRequestEntities() throws Exception {
        final Validated valid = Mockito.mock(Validated.class);
        Mockito.doReturn(Validated.class).when(valid).annotationType();
        Mockito.when(valid.value()).thenReturn(new Class[] {Partial1.class, Partial2.class});

        final ByteArrayInputStream entity =
                new ByteArrayInputStream("<PartialExample xmlns=\"\"><id>1</id><text>hello Cemo</text></PartialExample>".getBytes());
        final Class<?> klass = PartialExample.class;

        final Object obj = provider.readFrom((Class<Object>) klass,
                PartialExample.class,
                new Annotation[] {valid},
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedHashMap<>(),
                entity);

        assertThat(obj)
                .isInstanceOf(PartialExample.class);

        assertThat(((PartialExample) obj).id)
                .isEqualTo(1);
    }

    @Test
    public void returnsPartialValidatedByGroupRequestEntities() throws Exception {
        final Validated valid = Mockito.mock(Validated.class);
        Mockito.doReturn(Validated.class).when(valid).annotationType();
        Mockito.when(valid.value()).thenReturn(new Class[] {Partial1.class});

        final ByteArrayInputStream entity = new ByteArrayInputStream("<Example xmlns=\"\"><id>1</id></Example>".getBytes());
        final Class<?> klass = PartialExample.class;

        final Object obj = provider.readFrom((Class<Object>) klass,
                PartialExample.class,
                new Annotation[] {valid},
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedHashMap<>(),
                entity);

        assertThat(obj)
                .isInstanceOf(PartialExample.class);

        assertThat(((PartialExample) obj).id)
                .isEqualTo(1);
    }

    @Test
    public void throwsAnInvalidEntityExceptionForPartialValidatedRequestEntities() throws Exception {
        final Validated valid = Mockito.mock(Validated.class);
        Mockito.doReturn(Validated.class).when(valid).annotationType();
        Mockito.when(valid.value()).thenReturn(new Class[] {Partial1.class, Partial2.class});

        final ByteArrayInputStream entity = new ByteArrayInputStream("<Example xmlns=\"\"><id>1</id></Example>".getBytes());

        try {
            final Class<?> klass = PartialExample.class;
            provider.readFrom((Class<Object>) klass,
                    PartialExample.class,
                    new Annotation[] {valid},
                    MediaType.APPLICATION_XML_TYPE,
                    new MultivaluedHashMap<>(),
                    entity);
            failBecauseExceptionWasNotThrown(ConstraintViolationException.class);
        } catch (ConstraintViolationException e) {
            assertThat(ConstraintViolations.formatUntyped(e.getConstraintViolations()))
                    .containsOnly("text may not be null");
        }
    }

    @Test
    public void returnsValidatedRequestEntities() throws Exception {
        final Annotation valid = Mockito.mock(Annotation.class);
        Mockito.doReturn(Valid.class).when(valid).annotationType();

        final ByteArrayInputStream entity = new ByteArrayInputStream("<Example xmlns=\"\"><id>1</id></Example>".getBytes());
        final Class<?> klass = Example.class;

        final Object obj = provider.readFrom((Class<Object>) klass,
                Example.class,
                new Annotation[] {valid},
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedHashMap<>(),
                entity);

        assertThat(obj)
                .isInstanceOf(Example.class);

        assertThat(((Example) obj).id)
                .isEqualTo(1);
    }

    @Test
    public void throwsAnInvalidEntityExceptionForInvalidRequestEntities() throws Exception {
        final Annotation valid = Mockito.mock(Annotation.class);
        Mockito.doReturn(Valid.class).when(valid).annotationType();

        final ByteArrayInputStream entity = new ByteArrayInputStream("<Example xmlns=\"\"><id>-1</id></Example>".getBytes());

        try {
            final Class<?> klass = Example.class;
            provider.readFrom((Class<Object>) klass,
                    Example.class,
                    new Annotation[] {valid},
                    MediaType.APPLICATION_XML_TYPE,
                    new MultivaluedHashMap<>(),
                    entity);
            failBecauseExceptionWasNotThrown(ConstraintViolationException.class);
        } catch (ConstraintViolationException e) {
            assertThat(ConstraintViolations.formatUntyped(e.getConstraintViolations()))
                    .containsOnly("id must be greater than or equal to 0");
        }
    }

    @Test
    public void throwsAJsonProcessingExceptionForMalformedRequestEntities() throws Exception {
        final ByteArrayInputStream entity = new ByteArrayInputStream("<Example xmlns=\"\"><id>-1d</id></Example>".getBytes());

        try {
            final Class<?> klass = Example.class;
            provider.readFrom((Class<Object>) klass,
                    Example.class,
                    NONE,
                    MediaType.APPLICATION_XML_TYPE,
                    new MultivaluedHashMap<>(),
                    entity);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (JsonProcessingException e) {
            assertThat(e.getMessage())
                    .contains("Can", "not", "int", "String");

        }
    }

    @Test
    public void serializesResponseEntities() throws Exception {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        final Example example = new Example();
        example.id = 500;

        provider.writeTo(example,
                Example.class,
                Example.class,
                NONE,
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedHashMap<>(),
                output);

        assertThat(output.toString())
                .isEqualTo(
                        "<Example>" + System.lineSeparator() +
                                "  <id>500</id>" + System.lineSeparator() +
                                "</Example>" + System.lineSeparator());
    }

    @Test
    public void serializesResponseList() throws Exception {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        final ListExample listExample = new ListExample();
        listExample.wrappedList = Arrays.asList("1", "2", "3");
        listExample.unwrappedList = Arrays.asList("4", "5", "6");

        provider.writeTo(listExample,
                ListExample.class,
                ListExample.class,
                NONE,
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedHashMap<>(),
                output);

        assertThat(output.toString())
                .isEqualTo(ListExample.sampleXml);

    }

    @Test
    public void serializeResponseWithAttribute() throws Exception {

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final AttributeExample attributeAndTextExample = new AttributeExample();
        attributeAndTextExample.version = "2.0";

        provider.writeTo(attributeAndTextExample,
                AttributeExample.class,
                AttributeExample.class,
                NONE,
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedHashMap<>(),
                output);

        assertThat(output.toString())
                .isEqualTo("<AttributeExample version=\"2.0\"/>" + System.lineSeparator());
    }

    @Test
    public void serializeResponseNamespace() throws Exception {

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final NamespaceLocalNameExample namespaceLocalNameExample = new NamespaceLocalNameExample();
        namespaceLocalNameExample.text = "hello world";

        provider.writeTo(namespaceLocalNameExample,
                NamespaceLocalNameExample.class,
                NamespaceLocalNameExample.class,
                NONE,
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedHashMap<>(),
                output);

        assertThat(output.toString())
                .isEqualTo("<NamespaceAndText xmlns=\"lostInSpace\">hello world</NamespaceAndText>" + System.lineSeparator());
    }

}
