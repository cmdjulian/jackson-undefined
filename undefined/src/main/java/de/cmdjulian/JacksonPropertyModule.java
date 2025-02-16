package de.cmdjulian;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;

public final class JacksonPropertyModule extends SimpleModule {

    public JacksonPropertyModule() {
        super(new Version(1, 0, 0, null, "de.cmdjulian", "jackson-undefined"));
    }

    @Override
    public void setupModule(SetupContext context) {
        // deserializer
        SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(Property.class, new PropertyDeserializer());
        context.addDeserializers(deserializers);

        // serializer
        SimpleSerializers serializers = new SimpleSerializers();
        serializers.addSerializer(new PropertySerializer());
        context.addSerializers(serializers);
        // Register the BeanSerializerModifier so Jackson will omit absent properties.
        context.addBeanSerializerModifier(new AbsentPropertyBeanSerializerModifier());
    }
}
