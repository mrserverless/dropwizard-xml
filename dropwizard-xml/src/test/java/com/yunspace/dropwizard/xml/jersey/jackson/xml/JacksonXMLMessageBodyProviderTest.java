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

import org.fest.assertions.api.Assertions;
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
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.core.util.StringKeyObjectValueIgnoreCaseMultivaluedMap;
import com.yunspace.dropwizard.xml.jackson.JacksonXML;
import com.yunspace.dropwizard.xml.jersey.jackson.JacksonXMLMessageBodyProvider;

import io.dropwizard.validation.ConstraintViolations;
import io.dropwizard.validation.Validated;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

@SuppressWarnings("unchecked")
public class JacksonXMLMessageBodyProviderTest {
    private static final Annotation[] NONE = new Annotation[0];

    public static class Example {
        @Min(0)
        @JacksonXmlProperty
        int id;
    }

    public interface Partial1{}
    public interface Partial2{}

    public static class PartialExample {
        @Min(value = 0, groups = Partial1.class)
        @JacksonXmlProperty
        int id;

        @NotNull(groups = Partial2.class)
        @JacksonXmlProperty
        String text;
    }

    //tests wrapped and unwrapped lists, to verify jackson-dataformat-xml
    //<a href="https://github.com/FasterXML/jackson-dataformat-xml/issues/58">Issue 58</a>
    public static class ListExample {

        @JacksonXmlElementWrapper(useWrapping = true)
        @JacksonXmlProperty(localName = "wrappedList")
        List<String>wrappedList;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty()
        List<String>unwrappedList;

        static final String sampleXml =
                "<ListExample>\n" +
                "  <unwrappedList>4</unwrappedList>\n" +
                "  <unwrappedList>5</unwrappedList>\n" +
                "  <unwrappedList>6</unwrappedList>\n" +
                "  <wrappedList>\n" +
                "    <wrappedList>1</wrappedList>\n" +
                "    <wrappedList>2</wrappedList>\n" +
                "    <wrappedList>3</wrappedList>\n" +
                "  </wrappedList>\n" +
                "</ListExample>";
    }


    public static class AttributeExample {

        @JacksonXmlProperty(isAttribute = true)
        String version;

    }

    @JacksonXmlRootElement(localName = "NamespaceAndText", namespace = "lostInSpace")
    public static class NamespaceLocalNameExample {

        @JacksonXmlText
        @JacksonXmlElementWrapper(localName = "TextValue")
        String text;
    }

    @JsonIgnoreType(false)
    public static interface NonIgnorable extends Ignorable {

    }

    @JsonIgnoreType
    public static interface Ignorable {

    }

    private final XmlMapper mapper = JacksonXML.newXMLMapper();
    private final JacksonXMLMessageBodyProvider provider =
            new JacksonXMLMessageBodyProvider(mapper,
                    Validation.buildDefaultValidatorFactory().getValidator());

    @Before
    public void setUp() throws Exception {
        mapper.enable( INDENT_OUTPUT );
        Assume.assumeThat( Locale.getDefault().getLanguage(), CoreMatchers.is( "en" ) );
    }

    @Test
    public void readsDeserializableTypes() throws Exception {
        Assertions.assertThat(provider.isReadable(Example.class, null, null, null))
                .isTrue();
    }

    @Test
    public void writesSerializableTypes() throws Exception {
        Assertions.assertThat(provider.isWriteable(Example.class, null, null, null))
                .isTrue();
    }

    @Test
    public void doesNotWriteIgnoredTypes() throws Exception {
        Assertions.assertThat(provider.isWriteable(Ignorable.class, null, null, null))
                .isFalse();
    }

    @Test
    public void writesUnIgnoredTypes() throws Exception {
        Assertions.assertThat(provider.isWriteable(NonIgnorable.class, null, null, null))
                .isTrue();
    }

    @Test
    public void doesNotReadIgnoredTypes() throws Exception {
        Assertions.assertThat(provider.isReadable(Ignorable.class, null, null, null))
                .isFalse();
    }

    @Test
    public void readsUnIgnoredTypes() throws Exception {
        Assertions.assertThat(provider.isReadable(NonIgnorable.class, null, null, null))
                .isTrue();
    }

    @Test
    public void isChunked() throws Exception {
        Assertions.assertThat(provider.getSize(null, null, null, null, null))
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
                new MultivaluedMapImpl(),
                entity);

        Assertions.assertThat(obj)
                .isInstanceOf(Example.class);

        Assertions.assertThat(((Example) obj).id)
                .isEqualTo(1);
    }


    @Test
    public void deserializesRequestList()  throws Exception {

        final ByteArrayInputStream requestList = new ByteArrayInputStream((
                ListExample.sampleXml).getBytes());
        final Class<?> klass = ListExample.class;

        final Object obj = provider.readFrom((Class<Object>) klass,
                ListExample.class,
                NONE,
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedMapImpl(),
                requestList);

        Assertions.assertThat(obj)
                .isInstanceOf(ListExample.class);

        Assertions.assertThat(((ListExample) obj).unwrappedList)
                .isEqualTo(Arrays.asList("4", "5", "6"));

        Assertions.assertThat(((ListExample) obj).wrappedList)
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
                new MultivaluedMapImpl(),
                entity);

        Assertions.assertThat(obj)
                .isInstanceOf(AttributeExample.class);

        Assertions.assertThat(((AttributeExample) obj).version)
                .isEqualTo("1.0");
        //        assertThat(((AttributeAndTextExample) obj).value).isEqualTo("text");
    }

    @Test
    public void returnsPartialValidatedRequestEntities() throws Exception {
        final Validated valid = Mockito.mock(Validated.class);
        Mockito.doReturn(Validated.class).when(valid).annotationType();
        Mockito.when(valid.value()).thenReturn(new Class<?>[]{Partial1.class, Partial2.class});


        final ByteArrayInputStream entity = new ByteArrayInputStream("<PartialExample xmlns=\"\"><id>1</id><text>hello Cemo</text></PartialExample>".getBytes());
        final Class<?> klass = PartialExample.class;

        final Object obj = provider.readFrom((Class<Object>) klass,
                PartialExample.class,
                new Annotation[]{valid},
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedMapImpl(),
                entity);

        Assertions.assertThat(obj)
                .isInstanceOf(PartialExample.class);

        Assertions.assertThat(((PartialExample) obj).id)
                .isEqualTo(1);
    }

    @Test
    public void returnsPartialValidatedByGroupRequestEntities() throws Exception {
        final Validated valid = Mockito.mock(Validated.class);
        Mockito.doReturn(Validated.class).when(valid).annotationType();
        Mockito.when(valid.value()).thenReturn(new Class<?>[]{Partial1.class});

        final ByteArrayInputStream entity = new ByteArrayInputStream("<Example xmlns=\"\"><id>1</id></Example>".getBytes());
        final Class<?> klass = PartialExample.class;

        final Object obj = provider.readFrom((Class<Object>) klass,
                PartialExample.class,
                new Annotation[]{valid},
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedMapImpl(),
                entity);

        Assertions.assertThat(obj)
                .isInstanceOf(PartialExample.class);

        Assertions.assertThat(((PartialExample) obj).id)
                .isEqualTo(1);
    }

    @Test
    public void throwsAnInvalidEntityExceptionForPartialValidatedRequestEntities() throws Exception {
        final Validated valid = Mockito.mock(Validated.class);
        Mockito.doReturn(Validated.class).when(valid).annotationType();
        Mockito.when(valid.value()).thenReturn(new Class<?>[]{Partial1.class, Partial2.class});

        final ByteArrayInputStream entity = new ByteArrayInputStream("<Example xmlns=\"\"><id>1</id></Example>".getBytes());

        try {
            final Class<?> klass = PartialExample.class;
            provider.readFrom((Class<Object>) klass,
                    PartialExample.class,
                    new Annotation[]{ valid },
                    MediaType.APPLICATION_XML_TYPE,
                    new MultivaluedMapImpl(),
                    entity);
            Assertions.failBecauseExceptionWasNotThrown(ConstraintViolationException.class);
        } catch(ConstraintViolationException e) {
            Assertions.assertThat(ConstraintViolations.formatUntyped(e.getConstraintViolations()))
                    .containsOnly("text may not be null (was null)");
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
                new Annotation[]{ valid },
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedMapImpl(),
                entity);

        Assertions.assertThat(obj)
                .isInstanceOf(Example.class);

        Assertions.assertThat(((Example) obj).id)
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
                    new Annotation[]{ valid },
                    MediaType.APPLICATION_XML_TYPE,
                    new MultivaluedMapImpl(),
                    entity);
            Assertions.failBecauseExceptionWasNotThrown(ConstraintViolationException.class);
        } catch (ConstraintViolationException e) {
            Assertions.assertThat(ConstraintViolations.formatUntyped(e.getConstraintViolations()))
                    .containsOnly("id must be greater than or equal to 0 (was -1)");
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
                    new MultivaluedMapImpl(),
                    entity);
            Assertions.failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (JsonProcessingException e) {
            Assertions.assertThat(e.getMessage())
                    .startsWith("Can not construct instance of java.lang.Integer from String value '-1d': " +
                            "not a valid Integer value");

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
                new StringKeyObjectValueIgnoreCaseMultivaluedMap(),
                output);

        Assertions.assertThat(output.toString())
                .isEqualTo(
                        "<Example>" + System.lineSeparator() +
                        "  <id>500</id>" + System.lineSeparator() +
                        "</Example>");
    }

    @Test
    public void serializesResponseList()  throws Exception {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        final ListExample listExample = new ListExample();
        listExample.wrappedList = Arrays.asList("1", "2", "3");
        listExample.unwrappedList = Arrays.asList("4", "5", "6");

        provider.writeTo(listExample,
                ListExample.class,
                ListExample.class,
                NONE,
                MediaType.APPLICATION_XML_TYPE,
                new StringKeyObjectValueIgnoreCaseMultivaluedMap(),
                output);

        Assertions.assertThat(output.toString())
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
                new StringKeyObjectValueIgnoreCaseMultivaluedMap(),
                output);

        Assertions.assertThat(output.toString())
                .isEqualTo("<AttributeExample version=\"2.0\"/>");
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
                new StringKeyObjectValueIgnoreCaseMultivaluedMap(),
                output);

        Assertions.assertThat(output.toString())
                .isEqualTo("<NamespaceAndText xmlns=\"lostInSpace\">hello world</NamespaceAndText>");
    }

}
