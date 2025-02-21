package de.cmdjulian.undefined

inline operator fun <T : Any> Property<T>.invoke(block: (T?) -> Unit) {
    if (isPresent) block(value)
}

@JvmName("invokeThis")
inline operator fun <T : Any> Property<T>.invoke(block: T.() -> Unit) {
    if (isPresent) value?.block()
}

operator fun <T : Any> Property<T>.invoke(): T? = value()

val <T : Any> Property<T>.value: T? get() = this.invoke()
