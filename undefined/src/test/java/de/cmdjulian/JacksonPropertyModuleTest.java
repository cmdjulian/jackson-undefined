package de.cmdjulian;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.cmdjulian.undefined.Property;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class JacksonPropertyModuleTest {

    private final ObjectMapper mapper = new ObjectMapper();

    public JacksonPropertyModuleTest() {
        mapper.findAndRegisterModules();
    }

    public record TestClass(Property<String> name) {
    }

    @Test
    void moduleGetsAutoRegistered() {
        assertThat(mapper.getRegisteredModuleIds()).contains("jackson-undefined");
    }

    @Nested
    class Optional {
        @Test
        void absentPropertyToOptionalReturnsNull() {
            // given
            var testClass = new TestClass(new Property.Absent<>());

            // when
            var optional = testClass.name().asOptional();

            // then
            assertThat(optional).isNull();
        }

        @Test
        void nullPropertyToOptionalReturnsNull() {
            // given
            var testClass = new TestClass(new Property.Null<>());

            // when
            var optional = testClass.name().asOptional();

            // then
            assertThat(optional).isEmpty();
        }

        @Test
        void valuePropertyToOptionalReturnsValue() {
            // given
            var testClass = new TestClass(new Property.Value<>("test"));

            // when
            var optional = testClass.name().asOptional();

            // then
            assertThat(optional).isEqualTo(java.util.Optional.of("test"));
        }
    }

    @Nested
    class Serialize {
        @Test
        void value() throws JsonProcessingException {
            // given
            var testClass = new TestClass(new Property.Value<>("test"));

            // when
            var json = mapper.writeValueAsString(testClass);

            // then
            assertThat(json).isEqualTo("{\"name\":\"test\"}");
        }

        @Test
        void nullValue() throws JsonProcessingException {
            // given
            var testClass = new TestClass(new Property.Null<>());

            // when
            var json = mapper.writeValueAsString(testClass);

            // then
            assertThat(json).isEqualTo("{\"name\":null}");
        }

        @Test
        void absent() throws JsonProcessingException {
            // given
            var testClass = new TestClass(new Property.Absent<>());

            // when
            var json = mapper.writeValueAsString(testClass);

            // then
            assertThat(json).isEqualTo("{}");
        }
    }

    @Nested
    class Deserialize {
        @Test
        void value() throws JsonProcessingException {
            // given
            //language=JSON
            var json = """
                    {
                        "name": "test"
                    }""";

            // when
            var testClass = mapper.readValue(json, TestClass.class);

            // then
            assertThat(testClass.name()).isInstanceOf(Property.Value.class);
            assertThat(testClass.name().value()).isEqualTo("test");
        }

        @Test
        void nullValue() throws JsonProcessingException {
            // given
            //language=JSON
            var json = """
                    {
                        "name": null
                    }""";

            // when
            var testClass = mapper.readValue(json, TestClass.class);

            // then
            assertThat(testClass.name()).isInstanceOf(Property.Null.class);
            assertThat(testClass.name().value()).isNull();
        }

        @Test
        void absent() throws JsonProcessingException {
            // given
            //language=JSON
            var json = "{}";

            // when
            var testClass = mapper.readValue(json, TestClass.class);

            // then
            assertThat(testClass.name()).isInstanceOf(Property.Absent.class);
            assertThatCode(() -> testClass.name().value())
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
