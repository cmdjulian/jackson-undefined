package org.example;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;

import java.io.IOException;

public final class PropertyDeserializer<T> extends JsonDeserializer<Property<T>> implements ContextualDeserializer {

    private Class<T> cls;

    @Override
    @SuppressWarnings("unchecked")
    public JsonDeserializer<Property<T>> createContextual(DeserializationContext ctxt, BeanProperty property) {
        cls = (Class<T>) ctxt.getContextualType().getBindings().getBoundType(0).getRawClass();
        return this;
    }

    @Override
    public Property<T> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (cls == null) throw new IllegalStateException("Deserializer not initialized");
        T value = p.getCodec().readValue(p, cls);
        return Property.of(value);
    }

    @Override
    public Object getAbsentValue(DeserializationContext ctxt) {
        return Property.absent();
    }

    @Override
    public Property<T> getNullValue(DeserializationContext ctxt) {
        return Property.nullValue();
    }

    @Override
    @SuppressWarnings("RedundantMethodOverride")
    public AccessPattern getNullAccessPattern() {
        return AccessPattern.CONSTANT;
    }

    @Override
    public Object getEmptyValue(DeserializationContext ctxt) {
        return Property.nullValue();
    }

    @Override
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.CONSTANT;
    }
}
