package org.example;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;

public class JacksonPropertyModule extends SimpleModule {

    public JacksonPropertyModule() {
        super(new Version(1, 0, 0, null, "org.example", "jackson-undefined"));
    }

    @Override
    public void setupModule(SetupContext context) {
        SimpleSerializers serializers = new SimpleSerializers();
        SimpleDeserializers deserializers = new SimpleDeserializers();

        serializers.addSerializer(Property.class, new PropertySerializer());
        deserializers.addDeserializer(Property.class, new PropertyDeserializer());

        context.addSerializers(serializers);
        context.addDeserializers(deserializers);

        // Register the BeanSerializerModifier so Jackson will omit absent properties.
        context.addBeanSerializerModifier(new AbsentPropertyBeanSerializerModifier());
    }
}
