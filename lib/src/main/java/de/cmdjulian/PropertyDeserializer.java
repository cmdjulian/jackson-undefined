package de.cmdjulian;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

@SuppressWarnings("rawtypes")
final class PropertyDeserializer extends JsonDeserializer<Property> implements ContextualDeserializer {

    @Nullable
    private Class<?> cls;

    @Override
    public JsonDeserializer<Property> createContextual(DeserializationContext ctxt, BeanProperty property) {
        cls = ctxt.getContextualType().getBindings().getBoundType(0).getRawClass();
        return this;
    }

    @Override
    public Property deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (cls == null) throw new IllegalStateException("Deserializer not initialized");
        Object value = p.getCodec().readValue(p, cls);
        return new Property.Value<>(value);
    }

    @Override
    public Object getAbsentValue(DeserializationContext ctxt) {
        return new Property.Absent<>();
    }

    @Override
    public Property getNullValue(DeserializationContext ctxt) {
        return new Property.Null<>();
    }

    @Override
    @SuppressWarnings("RedundantMethodOverride")
    public AccessPattern getNullAccessPattern() {
        return AccessPattern.CONSTANT;
    }

    @Override
    public Object getEmptyValue(DeserializationContext ctxt) {
        return new Property.Null<>();
    }

    @Override
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.CONSTANT;
    }
}
