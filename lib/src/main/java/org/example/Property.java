package org.example;

public final class Property<T> {

    private final boolean isAbsent;
    private final boolean isNull;
    private final T value;

    private Property(final boolean isAbsent, final boolean isNull, T value) {
        this.isAbsent = isAbsent;
        this.isNull = isNull;
        this.value = value;
    }

    public static <T> Property<T> absent() {
        return new Property<>(true, false, null);
    }

    public static <T> Property<T> nullValue() {
        return new Property<>(false, true, null);
    }

    public static <T> Property<T> of(T value) {
        return new Property<>(false, false, value);
    }

    public boolean isAbsent() {
        return isAbsent;
    }

    public boolean isNull() {
        return isNull;
    }

    public T getValue() {
        if (isAbsent) {
            throw new IllegalStateException("Property is absent");
        } else {
            return value;
        }
    }
}
