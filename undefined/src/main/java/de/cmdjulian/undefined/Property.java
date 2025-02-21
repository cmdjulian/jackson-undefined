package de.cmdjulian.undefined;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The `Property` interface represents a container that may or may not hold a value of type `T`.
 * It provides methods to check the presence, absence, and nullity of the value, as well as
 * methods to transform and visit the contained value.
 *
 * <p>Absence indicates that the property does not hold any value, which is different from
 * holding a `null` value. A property can be absent, hold a `null` value, or hold a non-null value.
 *
 * @param <T> the type of the value contained in the `Property`.
 */
public sealed interface Property<T> permits Property.Absent, Property.Null, Property.Value {
    /**
     * Returns the value contained in this `Property`, if present.
     *
     * @return the value, or `null` if the value is absent or null.
     * @throws IllegalStateException if the property is absent.
     */
    @Nullable
    T value();

    /**
     * Returns an `Optional` containing the value, if present.
     *
     * @return an `Optional` containing the value, or an empty `Optional` if the value is null, returns null for
     * absent value.
     */
    @Nullable
    default Optional<T> asOptional() {
        return isAbsent() ? null : Optional.ofNullable(value());
    }

    /**
     * Returns `true` if the value is absent.
     *
     * <p>Absence means that the property does not hold any value at all.
     * This is distinct from the property holding a `null` value.
     *
     * @return `true` if the value is absent, `false` otherwise.
     */
    boolean isAbsent();

    /**
     * Returns `true` if the value is present.
     *
     * <p>A value is considered present if it is neither absent nor `null`.
     *
     * @return `true` if the value is present, `false` otherwise.
     */
    default boolean isPresent() {
        return !isAbsent();
    }

    /**
     * Returns `true` if the value is null.
     *
     * <p>A property can explicitly hold a `null` value, which is different from being absent.
     * A `null` value indicates that the property has been set, but the value itself is `null`.
     *
     * @return `true` if the value is null, `false` otherwise.
     */
    boolean isNull();

    /**
     * Applies a function to the contained value, if present, and returns the result as a new `Property`.
     *
     * <p>If the property is absent, the function is not applied, and an absent property is returned.
     * If the property holds a `null` value, the function is applied with `null` as the argument.
     * Fold always returns Properties based on the initial json which was used to create the Property.
     * That said, having the following json:
     * <pre>
     * {
     *   "address": null
     * }
     * </pre>
     * <p>
     * implies all properties beneath address, are absent, not null. This is due to the fact that the null value
     * prevents the property from being present.
     *
     * @param <R> the type of the result.
     * @param f   the function to apply to the value.
     * @return a new `Property` containing the result of the function application, or an absent `Property` if the value is absent.
     */
    default <R> Property<R> fold(Function<T, Property<R>> f) {
        if (isAbsent()) return new Property.Absent<>();
        if (isNull()) return new Property.Absent<>();
        return f.apply(value());
    }

    /**
     * Applies a function to the contained value, if present, and returns the result as a new `Property`.
     *
     * <p>If the property is absent, the function is not applied, and an absent property is returned.
     * If the property holds a `null` value, the function is applied with `null` as the argument.
     *
     * @param <R> the type of the result.
     * @param f   the function to apply to the value.
     * @return a new `Property` containing the result of the function application, or an absent `Property` if the value is absent.
     */
    default <R> Property<R> map(Function<@Nullable T, R> f) {
        if (isAbsent()) {
            return new Absent<>();
        } else {
            var result = f.apply(value());
            return result == null ? new Null<>() : new Value<>(result);
        }
    }

    /**
     * Executes the given consumer if the value is present.
     *
     * <p>If the property is absent, the consumer is not called.
     * If the property holds a `null` value, the consumer is called with `null` as the argument.
     *
     * @param f the consumer to accept the value.
     */
    default void onPresence(Consumer<@Nullable T> f) {
        if (isPresent()) {
            f.accept(value());
        }
    }

    /**
     * Executes the given runnable if the value is absent.
     *
     * <p>If the property is present (either non-null or `null`), the runnable is not called.
     * This method is useful for handling cases where the absence of a value requires specific actions.
     *
     * @param r the runnable to execute when the value is absent.
     */
    default void onAbsence(Runnable r) {
        if (isAbsent()) {
            r.run();
        }
    }

    /**
     * Visits the contained value with the given consumers, depending on its presence or absence.
     *
     * <p>If the property is absent, the `absent` runnable is executed.
     * If the property is present (either non-null or `null`), the `present` consumer is called with the value.
     *
     * @param present the consumer to accept the value if present.
     * @param absent  the runnable to execute if the value is absent.
     */
    default void visit(Consumer<@Nullable T> present, Runnable absent) {
        onPresence(present);
        onAbsence(absent);
    }

    /**
     * A record representing an absent `Property`.
     *
     * <p>An absent property does not hold any value. This is distinct from holding a `null` value.
     *
     * @param <T> the type of the value.
     */
    record Absent<T>() implements Property<T> {
        @NonNull
        @Override
        public T value() {
            throw new IllegalStateException("Property is absent");
        }

        @Override
        public boolean isAbsent() {
            return true;
        }

        @Override
        public boolean isNull() {
            return false;
        }
    }

    /**
     * A record representing a null `Property`.
     *
     * <p>A null property explicitly holds a `null` value. This is distinct from being absent.
     *
     * @param <T> the type of the value.
     */
    record Null<T>() implements Property<T> {
        @Nullable
        @Override
        public T value() {
            return null;
        }

        @Override
        public boolean isAbsent() {
            return false;
        }

        @Override
        public boolean isNull() {
            return true;
        }
    }

    /**
     * A record representing a present `Property` with a value.
     *
     * <p>A present property holds a non-null value.
     *
     * @param <T> the type of the value.
     */
    record Value<T>(T value) implements Property<T> {
        @NonNull
        @Override
        public T value() {
            return value;
        }

        @Override
        public boolean isAbsent() {
            return false;
        }

        @Override
        public boolean isNull() {
            return false;
        }
    }
}
