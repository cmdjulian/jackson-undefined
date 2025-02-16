package org.example;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public final class PropertySerializer<T> extends JsonSerializer<Property<T>> {
    @Override
    public void serialize(Property<T> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value.isAbsent()) {
            return;
        }

        if (value.isNull()) {
            gen.writeNull();
        } else {
            gen.writeObject(value.getValue());
        }
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, Property<T> value) {
        return value.isAbsent() || value.isNull();
    }
}
