package de.cmdjulian.undefined;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

@SuppressWarnings("rawtypes")
final class PropertySerializer extends JsonSerializer<Property> {
    @Override
    public void serialize(Property value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // value can't be absent here, as [AbsentAwarePropertyBeanPropertyWriter] filters out absent properties
        if (value.isNull()) {
            gen.writeNull();
        } else {
            gen.writeObject(value.value());
        }
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, Property value) {
        return value.isAbsent() || value.isNull();
    }

    @Override
    public Class<Property> handledType() {
        return Property.class;
    }
}
