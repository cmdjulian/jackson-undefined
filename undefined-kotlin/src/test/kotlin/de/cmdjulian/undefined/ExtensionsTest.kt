package de.cmdjulian.undefined

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class ExtensionsTest {
    @Test
    fun `absent properties don't invoke on absence`() {
        val property = Property.Absent<String>()

        property { -> fail("should not be called") }
        property { fail("should not be called") }
    }

    @Test
    fun `null properties don't invoke on absence`() {
        val property = Property.Absent<String>()

        property { -> fail("should not be called") }
        property { fail("should not be called") }
    }

    @Test
    fun `null properties invoke on null`() {
        val property = Property.Null<String>()

        property { -> fail("should not be called") }
        property { value -> return Assertions.assertNull(value) }

        fail("should not have been reached, except second property method was not called")
    }

    @Test
    fun `value returns value`() {
        val property = Property.Value("test")

        Assertions.assertEquals("test", property.value)
    }

    @Test
    fun `invoke returns value`() {
        val property = Property.Value("test")

        Assertions.assertEquals("test", property())
    }

    @Test
    fun `invoke calls on value`() {
        val property = Property.Value("test")

        property { value -> return Assertions.assertEquals("test", value) }

        fail("should not have been reached, except property method was not called")
    }

    @Test
    fun `invoke calls on this value`() {
        val property = Property.Value("test")

        property { -> return Assertions.assertEquals("test", this) }

        fail("should not have been reached, except property method was not called")
    }
}
