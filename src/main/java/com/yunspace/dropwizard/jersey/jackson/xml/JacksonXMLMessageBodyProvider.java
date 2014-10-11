package com.yunspace.dropwizard.jersey.jackson.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;
import io.dropwizard.validation.ConstraintViolations;
import io.dropwizard.validation.Validated;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.groups.Default;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;


/**
 * (Essentially the same as {@link io.dropwizard.jersey.jackson.JacksonMessageBodyProvider} except it extends
 * {@link com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider} and uses the
 * {@link com.fasterxml.jackson.dataformat.xml.XmlMapper}
 */
public class JacksonXMLMessageBodyProvider extends JacksonJaxbXMLProvider {

    /**
     * The default group array used in case any of the validate methods is called without a group.
     */
    private static final Class<?>[] DEFAULT_GROUP_ARRAY = new Class<?>[]{ Default.class };
    private final XmlMapper mapper;
    private final Validator validator;

    public JacksonXMLMessageBodyProvider(XmlMapper mapper, Validator validator) {
        this.validator = validator;
        this.mapper = mapper;
        setMapper(mapper);
    }

    @Override
    public boolean isReadable(Class<?> type,
                              Type genericType,
                              Annotation[] annotations,
                              MediaType mediaType) {
        return isProvidable(type) && super.isReadable(type, genericType, annotations, mediaType);
    }

    @Override
    public Object readFrom(Class<Object> type,
                           Type genericType,
                           Annotation[] annotations,
                           MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders,
                           InputStream entityStream) throws IOException {
        return validate(annotations, super.readFrom(type,
                genericType,
                annotations,
                mediaType,
                httpHeaders,
                entityStream));
    }

    @Override
    public boolean isWriteable(Class<?> type,
                               Type genericType,
                               Annotation[] annotations,
                               MediaType mediaType) {
        return isProvidable(type) && super.isWriteable(type, genericType, annotations, mediaType);
    }

    private Object validate(Annotation[] annotations, Object value) {
        final Class<?>[] classes = findValidationGroups(annotations);

        if (classes != null) {
            final Set<ConstraintViolation<Object>> violations = validator.validate(value, classes);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException("The request entity had the following errors:",
                        ConstraintViolations.copyOf(violations));
            }
        }

        return value;
    }

    private Class<?>[] findValidationGroups(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Valid.class) {
                return DEFAULT_GROUP_ARRAY;
            } else if (annotation.annotationType() == Validated.class) {
                return  ((Validated) annotation).value();
            }
        }
        return null;
    }

    private boolean isProvidable(Class<?> type) {
        final JsonIgnoreType ignore = type.getAnnotation(JsonIgnoreType.class);
        return (ignore == null) || !ignore.value();
    }

    public XmlMapper getXmlMapper() {
        return mapper;
    }
}
