# Jackson Undefined Property Module

## Overview

**Jackson Undefined Property Module** is a Java and Kotlin extension for the Jackson serialization framework that
enables clear differentiation between:

- **Undefined (absent) values**: Values that were never specified and should not be included in serialization.
- **Explicitly null values**: Values explicitly set to `null`, meaning they should be serialized as `null`.
- **Concrete values**: Actual values provided in the object.

This distinction is particularly useful in scenarios like **PATCH requests**, where the absence of a field should not
override existing values, but explicitly setting `null` should (Yes, I'm looking at you JavaScript!).

## Features

- **Automatic handling of undefined vs. null vs. concrete values**
- **Custom serialization and deserialization via Jackson modules, no black magic**
- **Seamless integration with Java and Kotlin**
- **Supports both immutable and mutable data models**
- **Works without modifying existing Jackson configurations at the ObjectMapper level**
- **Seamless convert between `Optional<T>` and `Property<T>`**
- **Uses [JSpecify](https://jspecify.dev/) for enhanced nullability annotations**

## Why This Matters

Standard Jackson behavior does not differentiate between missing and explicitly null values. This module enhances
Jackson’s ability to:

- **Omit undefined values** from serialization.
- **Retain explicit nulls** when necessary.
- **Enable fine-grained control over PATCH operations**, where omitting a value means "do not change" while setting it
  to `null` means "remove."

## Installation

<details>
  <summary>Maven</summary>

```xml

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.github.cmdjulian</groupId>
            <artifactId>mopy</artifactId>
            <version>Tag</version>
        </dependency>
    </dependencies>
</project>
```

</details>

<details>
  <summary>Gradle (Kotlin DSL)</summary>

`settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
```

`build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.cmdjulian:jackson-undefined:1.0.0")
}
```

</details>

<details>
  <summary>Gradle (Groovy DSL)</summary>

`settings.gradle`:

```groovy
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

`build.gradle`:

```groovy
dependencies {
    implementation 'com.github.cmdjulian:mopy:1.0.0'
}
```

</details>

## Usage

<details>
  <summary>Java</summary>

```java
void main() {
    Property<String> property = new Property.Absent<>();

    // Using Boolean Flags
    if (property.isAbsent()) {
        System.out.println("Property is absent");
    } else if (property.isNull()) {
        System.out.println("Property is explicitly set to null");
    } else {
        System.out.println("Property has a value: " + property.value());
    }

// Using switch (Java 17+)
    switch (property) {
        case Property.Absent<?> absent -> System.out.println("Property is absent");
        case Property.Null<?> nullValue -> System.out.println("Property is explicitly null");
        case Property.Value<?>(var val) -> System.out.println("Property has value: " + val);
    }
}
```

</details>

<details>
  <summary>Kotlin</summary>

```kotlin
// Using Boolean Flags
val property: Property<String> = Property.Absent<String>()

when {
    property.isAbsent() -> println("Property is absent")
    property.isNull() -> println("Property is explicitly null")
    else -> println("Property has value: ${property.value()}")
}

// Using when
when (property) {
    is Property.Absent<*> -> println("Property is absent")
    is Property.Null<*> -> println("Property is explicitly null")
    is Property.Value<String> -> println("Property has value: ${property.value}")
}
```

</details>

### Serialization

When serializing a class containing `Property<T>` fields, absent values are omitted entirely, null values are written as
`null`, and defined values are written as expected.

```java
public record Person(Property<String> name) {
}

ObjectMapper mapper = new ObjectMapper();

void serialize() throws JsonProcessingException {
    mapper.findAndRegisterModules();

    Person test = new Person(new Property.Absent<>());
    String json = mapper.writeValueAsString(test);
    System.out.println(json); // Output: {}
}
```

### Deserialization

When deserializing JSON, the module automatically maps missing properties to `Property.Absent`, `null` values to
`Property.Null`, and present values to `Property.Value`.

```java
public record Person(Property<String> name) {
}

ObjectMapper mapper = new ObjectMapper();

void deserialize() throws JsonProcessingException {
    Person person1 = mapper.readValue("{\"name\":\"John\"}", Person.class);
    assert person1.name().value().equals("John");

    Person person2 = mapper.readValue("{\"name\":null}", Person.class);
    assert person2.name().isNull();

    Person person3 = mapper.readValue("{}", Person.class);
    assert person3.name().isAbsent();
}
```

## Compatibility with `Optional`

The `Property<T>` type is designed to work seamlessly alongside `Optional<T>`:

- `Property.Value<T>` behaves similarly to `Optional.of(T)`
- `Property.Null<T>` behaves like `Optional.empty()`
- `Property.Absent<T>` is distinct, indicating the value was never specified and instead of returning an `Optional` it
  will return `null` to indicate that the value was not specified.

To convert between them:

```java
Optional<String> optional = new Property.Value<>("John").asOptional();
Property<String> propertyFromOptional = optional.<Property<String>>map(Property.Value::new)
        .orElseGet(Property.Null::new);
```

## Module Registration

The `JacksonPropertyModule` is automatically registered via Java's `ServiceLoader` mechanism. This means that if you
have the module on your classpath, Jackson will automatically discover and register it.

### Auto-Registration via ServiceLoader

Simply call `ObjectMapper.findAndRegisterModules()`.

### Manual Registration

If you prefer to register the module manually, you can do so by adding it to your `ObjectMapper` instance:

```java
void main() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JacksonPropertyModule());
}
```

### Complete Example

Consider a JSON payload and a class `UserProfile` with multiple attributes:

**JSON Payload:**

```json
{
  "username": "jdoe",
  "email": null,
  "age": 25,
  "address": {
    "street": "123 Main St",
    "city": null
  }
}
```

**Java Class:**

```java
public class UserProfile {
    public Property<String> username;
    public Property<String> email;
    public Property<Integer> age;
    public Property<Address> address;

    public record Address(String street, Property<String> city, Property<String> zip) {
    }
}
```

**Deserialization:**

```java
void main() throws JsonProcessingException {
  ObjectMapper mapper = new ObjectMapper();
  mapper.findAndRegisterModules();

  String jsonPayload = """
          {
            "username": "jdoe",
            "email": null,
            "age": 25,
            "address": {
              "street": "123 Main St",
              "city": null
            }
          }""";

  var userProfile = mapper.readValue(jsonPayload, UserProfile.class);

  // Accessing values
  System.out.println("Username: " + userProfile.username.value()); // Output: jdoe
  System.out.println("Email: " + (userProfile.email.asOptional().orElse("fallback"))); // Output: fallback
  System.out.println("Age: " + userProfile.age.value()); // Output: 25
  System.out.println("Street: " + userProfile.address.map(UserProfile.Address::street).value()); // Output: 123 Main St
  userProfile.address.visit(address ->
      address.city.visit(city -> 
          System.out.println("City: " + city)) // Output: City: null
  );
  switch (userProfile.address.fold(UserProfile.Address::zip)) {
    case Property.Value<String>(var value) -> System.out.println("Zip: " + value);
    case Property.Absent<?> _ -> System.out.println("Zip: absent");
    case Property.Null<?> _ -> System.out.println("Zip: null");
  } // Output: Zip: absent
}
```

## Contributing

We welcome contributions! If you’d like to contribute:

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Submit a pull request

## License

This project is licensed under the MIT License.
