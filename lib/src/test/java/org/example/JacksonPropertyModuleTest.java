package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonPropertyModuleTest {

    public static final class TestClass {

        private final Property<String> name;

        public TestClass(Property<String> name) {
            this.name = name;
        }

        public Property<String> getName() {
            return name;
        }
    }

    private final ObjectMapper mapper = new ObjectMapper();

    public JacksonPropertyModuleTest() {
        mapper.findAndRegisterModules();
    }

    @Test
    void moduleGetsAutoRegistered() {
        assertThat(mapper.getRegisteredModuleIds()).contains("jackson-undefined");
    }

    @Nested
    class Serialize {
        @Test
        void value() throws JsonProcessingException {
            TestClass testClass = new TestClass(Property.of("test"));
            String json = mapper.writeValueAsString(testClass);
            assertThat(json).isEqualTo("{\"name\":\"test\"}");
        }

        @Test
        void nullValue() throws JsonProcessingException {
            TestClass testClass = new TestClass(Property.nullValue());
            String json = mapper.writeValueAsString(testClass);
            assertThat(json).isEqualTo("{\"name\":null}");
        }

        @Test
        void absent() throws JsonProcessingException {
            TestClass testClass = new TestClass(Property.absent());
            String json = mapper.writeValueAsString(testClass);
            assertThat(json).isEqualTo("{}");
        }
    }

    @Nested
    class Deserialize {
        @Test
        void value() throws JsonProcessingException {
            TestClass testClass = mapper.readValue("{\"name\":\"test\"}", TestClass.class);
            assertThat(testClass.getName().getValue()).isEqualTo("test");
        }

        @Test
        void nullValue() throws JsonProcessingException {
            TestClass testClass = mapper.readValue("{\"name\":null}", TestClass.class);
            assertThat(testClass.getName().isNull()).isTrue();
        }

        @Test
        void absent() throws JsonProcessingException {
            TestClass testClass = mapper.readValue("{}", TestClass.class);
            assertThat(testClass.getName().isAbsent()).isTrue();
        }
    }
}
