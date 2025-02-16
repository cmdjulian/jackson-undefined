package de.cmdjulian;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

final class AbsentAwarePropertyBeanPropertyWriter extends BeanPropertyWriter {

    private final BeanPropertyWriter delegate;

    public AbsentAwarePropertyBeanPropertyWriter(BeanPropertyWriter delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
        if (delegate.get(bean) instanceof Property<?> property && property.isPresent()) {
            delegate.serializeAsField(bean, gen, prov);
        }
    }
}
