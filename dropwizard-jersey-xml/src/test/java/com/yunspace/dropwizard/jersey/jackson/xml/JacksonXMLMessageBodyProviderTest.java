package com.yunspace.dropwizard.jersey.jackson.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.core.util.StringKeyObjectValueIgnoreCaseMultivaluedMap;
import com.yunspace.dropwizard.jackson.xml.JacksonXML;
import io.dropwizard.validation.ConstraintViolations;
import io.dropwizard.validation.Validated;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeThat;
import static org.mockito.Mockito.*;

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
                "<ListExample><wrappedList>\n" +
                "    <wrappedList>1</wrappedList>\n" +
                "    <wrappedList>2</wrappedList>\n" +
                "    <wrappedList>3</wrappedList>\n" +
                "  </wrappedList>\n" +
                "  <unwrappedList>4</unwrappedList>\n" +
                "  <unwrappedList>5</unwrappedList>\n" +
                "  <unwrappedList>6</unwrappedList>\n" +
                "</ListExample>";
    }

    public static class AttributeAndTextExample {

        @JacksonXmlProperty(isAttribute = true)
        String version;

    }

    @JsonIgnoreType
    public static interface Ignorable {

    }

    @JsonIgnoreType(false)
    public static interface NonIgnorable extends Ignorable {

    }

    private final XmlMapper mapper = spy(JacksonXML.newXMLMapper());
    //private JacksonJaxbXMLProvider provider = new JacksonJaxbXMLProvider();

    private final JacksonXMLMessageBodyProvider provider =
            new JacksonXMLMessageBodyProvider(mapper,
                    Validation.buildDefaultValidatorFactory().getValidator());

    @Before
    public void setUp() throws Exception {
        assumeThat(Locale.getDefault().getLanguage(), is("en"));
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
                new MultivaluedMapImpl(),
                entity);

        assertThat(obj)
                .isInstanceOf(Example.class);

        assertThat(((Example) obj).id)
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
        final Class<?> klass = AttributeAndTextExample.class;

        final Object obj = provider.readFrom((Class<Object>) klass,
                AttributeAndTextExample.class,
                NONE,
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedMapImpl(),
                entity);

        assertThat(obj)
                .isInstanceOf(AttributeAndTextExample.class);

        assertThat(((AttributeAndTextExample) obj).version)
                .isEqualTo("1.0");
        //        assertThat(((AttributeAndTextExample) obj).value).isEqualTo("text");
    }

    @Test
    public void returnsPartialValidatedRequestEntities() throws Exception {
        final Validated valid = mock(Validated.class);
        doReturn(Validated.class).when(valid).annotationType();
        when(valid.value()).thenReturn(new Class<?>[]{Partial1.class, Partial2.class});


        final ByteArrayInputStream entity = new ByteArrayInputStream("<PartialExample xmlns=\"\"><id>1</id><text>hello Cemo</text></PartialExample>".getBytes());
        final Class<?> klass = PartialExample.class;

        final Object obj = provider.readFrom((Class<Object>) klass,
                PartialExample.class,
                new Annotation[]{valid},
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedMapImpl(),
                entity);

        assertThat(obj)
                .isInstanceOf(PartialExample.class);

        assertThat(((PartialExample) obj).id)
                .isEqualTo(1);
    }

    @Test
    public void returnsPartialValidatedByGroupRequestEntities() throws Exception {
        final Validated valid = mock(Validated.class);
        doReturn(Validated.class).when(valid).annotationType();
        when(valid.value()).thenReturn(new Class<?>[]{Partial1.class});

        final ByteArrayInputStream entity = new ByteArrayInputStream("<Example xmlns=\"\"><id>1</id></Example>".getBytes());
        final Class<?> klass = PartialExample.class;

        final Object obj = provider.readFrom((Class<Object>) klass,
                PartialExample.class,
                new Annotation[]{valid},
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedMapImpl(),
                entity);

        assertThat(obj)
                .isInstanceOf(PartialExample.class);

        assertThat(((PartialExample) obj).id)
                .isEqualTo(1);
    }

    @Test
    public void throwsAnInvalidEntityExceptionForPartialValidatedRequestEntities() throws Exception {
        final Validated valid = mock(Validated.class);
        doReturn(Validated.class).when(valid).annotationType();
        when(valid.value()).thenReturn(new Class<?>[]{Partial1.class, Partial2.class});

        final ByteArrayInputStream entity = new ByteArrayInputStream("<Example xmlns=\"\"><id>1</id></Example>".getBytes());

        try {
            final Class<?> klass = PartialExample.class;
            provider.readFrom((Class<Object>) klass,
                    PartialExample.class,
                    new Annotation[]{ valid },
                    MediaType.APPLICATION_XML_TYPE,
                    new MultivaluedMapImpl(),
                    entity);
            failBecauseExceptionWasNotThrown(ConstraintViolationException.class);
        } catch(ConstraintViolationException e) {
            assertThat(ConstraintViolations.formatUntyped(e.getConstraintViolations()))
                    .containsOnly("text may not be null (was null)");
        }
    }

    @Test
    public void returnsValidatedRequestEntities() throws Exception {
        final Annotation valid = mock(Annotation.class);
        doReturn(Valid.class).when(valid).annotationType();

        final ByteArrayInputStream entity = new ByteArrayInputStream("<Example xmlns=\"\"><id>1</id></Example>".getBytes());
        final Class<?> klass = Example.class;

        final Object obj = provider.readFrom((Class<Object>) klass,
                Example.class,
                new Annotation[]{ valid },
                MediaType.APPLICATION_XML_TYPE,
                new MultivaluedMapImpl(),
                entity);

        assertThat(obj)
                .isInstanceOf(Example.class);

        assertThat(((Example) obj).id)
                .isEqualTo(1);
    }

    @Test
    public void throwsAnInvalidEntityExceptionForInvalidRequestEntities() throws Exception {
        final Annotation valid = mock(Annotation.class);
        doReturn(Valid.class).when(valid).annotationType();

        final ByteArrayInputStream entity = new ByteArrayInputStream("<Example xmlns=\"\"><id>-1</id></Example>".getBytes());

        try {
            final Class<?> klass = Example.class;
            provider.readFrom((Class<Object>) klass,
                    Example.class,
                    new Annotation[]{ valid },
                    MediaType.APPLICATION_XML_TYPE,
                    new MultivaluedMapImpl(),
                    entity);
            failBecauseExceptionWasNotThrown(ConstraintViolationException.class);
        } catch (ConstraintViolationException e) {
            assertThat(ConstraintViolations.formatUntyped(e.getConstraintViolations()))
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
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (JsonProcessingException e) {
            assertThat(e.getMessage())
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

        assertThat(output.toString())
                .isEqualTo(
                        "<Example>\n" +
                                "  <id>500</id>\n" +
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

        assertThat(output.toString())
                .isEqualTo(ListExample.sampleXml);

    }

    @Test
    public void serializeResponseWithAttribute() throws Exception {

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final AttributeAndTextExample attributeAndTextExample = new AttributeAndTextExample();
        attributeAndTextExample.version = "2.0";

        provider.writeTo(attributeAndTextExample,
                AttributeAndTextExample.class,
                AttributeAndTextExample.class,
                NONE,
                MediaType.APPLICATION_XML_TYPE,
                new StringKeyObjectValueIgnoreCaseMultivaluedMap(),
                output);

        assertThat(output.toString())
                .isEqualTo("<AttributeAndTextExample version=\"2.0\"/>");
    }


}
