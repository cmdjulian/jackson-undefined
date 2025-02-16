package org.example;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public class AbsentAwarePropertyBeanPropertyWriter extends BeanPropertyWriter {

    private final BeanPropertyWriter delegate;

    public AbsentAwarePropertyBeanPropertyWriter(BeanPropertyWriter delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
        Object value = delegate.get(bean);
        if (value instanceof Property) {
            Property<?> property = (Property<?>) value;
            // If the property is absent, skip writing the field.
            if (property.isAbsent()) {
                return;
            }
        }
        delegate.serializeAsField(bean, gen, prov);
    }
}
