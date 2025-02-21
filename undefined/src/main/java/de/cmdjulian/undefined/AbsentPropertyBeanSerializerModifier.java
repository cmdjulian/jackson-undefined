package de.cmdjulian.undefined;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.util.List;
import java.util.ListIterator;

final class AbsentPropertyBeanSerializerModifier extends BeanSerializerModifier {
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        ListIterator<BeanPropertyWriter> it = beanProperties.listIterator();
        while (it.hasNext()) {
            var writer = it.next();
            // Check if the property type is our Property type.
            if (Property.class.isAssignableFrom(writer.getType().getRawClass())) {
                it.set(new AbsentAwarePropertyBeanPropertyWriter(writer));
            }
        }
        return beanProperties;
    }
}
