package io.jooby;

import io.jooby.internal.ArrayValue;
import io.jooby.internal.HashValue;
import io.jooby.internal.MissingValue;
import io.jooby.internal.SingleValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public interface Value {
  /**
   * Convert this value to long (if possible).
   *
   * @return Long value.
   */
  default long longValue() {
    try {
      return Long.parseLong(value());
    } catch (NumberFormatException x) {
      try {
        LocalDateTime date = LocalDateTime.parse(value(), Context.RFC1123);
        Instant instant = date.toInstant(ZoneOffset.UTC);
        return instant.toEpochMilli();
      } catch (DateTimeParseException expected) {
      }
      throw new TypeMismatchException(name(), long.class, x);
    }
  }

  /**
   * Convert this value to long (if possible) or fallback to given value when missing.
   *
   * @param defaultValue Default value.
   * @return Convert this value to long (if possible) or fallback to given value when missing.
   */
  default long longValue(long defaultValue) {
    try {
      return longValue();
    } catch (MissingValueException x) {
      return defaultValue;
    }
  }

  /**
   * Convert this value to int (if possible).
   *
   * @return Int value.
   */
  default int intValue() {
    try {
      return Integer.parseInt(value());
    } catch (NumberFormatException x) {
      throw new TypeMismatchException(name(), int.class, x);
    }
  }

  /**
   * Convert this value to int (if possible) or fallback to given value when missing.
   *
   * @param defaultValue Default value.
   * @return Convert this value to int (if possible) or fallback to given value when missing.
   */
  default int intValue(int defaultValue) {
    try {
      return intValue();
    } catch (MissingValueException x) {
      return defaultValue;
    }
  }

  /**
   * Convert this value to byte (if possible).
   *
   * @return Convert this value to byte (if possible).
   */
  default byte byteValue() {
    try {
      return Byte.parseByte(value());
    } catch (NumberFormatException x) {
      throw new TypeMismatchException(name(), byte.class, x);
    }
  }

  /**
   * Convert this value to byte (if possible) or fallback to given value when missing.
   *
   * @param defaultValue Default value.
   * @return Convert this value to byte (if possible) or fallback to given value when missing.
   */
  default byte byteValue(byte defaultValue) {
    try {
      return byteValue();
    } catch (MissingValueException x) {
      return defaultValue;
    }
  }

  /**
   * Convert this value to float (if possible).
   *
   * @return Convert this value to float (if possible).
   */
  default float floatValue() {
    try {
      return Float.parseFloat(value());
    } catch (NumberFormatException x) {
      throw new TypeMismatchException(name(), float.class, x);
    }
  }

  /**
   * Convert this value to float (if possible) or fallback to given value when missing.
   *
   * @param defaultValue Default value.
   * @return Convert this value to float (if possible) or fallback to given value when missing.
   */
  default float floatValue(float defaultValue) {
    try {
      return floatValue();
    } catch (MissingValueException x) {
      return defaultValue;
    }
  }

  /**
   * Convert this value to double (if possible).
   *
   * @return Convert this value to double (if possible).
   */
  default double doubleValue() {
    try {
      return Double.parseDouble(value());
    } catch (NumberFormatException x) {
      throw new TypeMismatchException(name(), double.class, x);
    }
  }

  /**
   * Convert this value to double (if possible) or fallback to given value when missing.
   *
   * @param defaultValue Default value.
   * @return Convert this value to double (if possible) or fallback to given value when missing.
   */
  default double doubleValue(double defaultValue) {
    try {
      return doubleValue();
    } catch (MissingValueException x) {
      return defaultValue;
    }
  }

  /**
   * Convert this value to boolean (if possible).
   *
   * @return Convert this value to boolean (if possible).
   */
  default boolean booleanValue() {
    return Boolean.parseBoolean(value());
  }

  /**
   * Convert this value to boolean (if possible) or fallback to given value when missing.
   *
   * @param defaultValue Default value.
   * @return Convert this value to boolean (if possible) or fallback to given value when missing.
   */
  default boolean booleanValue(boolean defaultValue) {
    try {
      return booleanValue();
    } catch (MissingValueException x) {
      return defaultValue;
    }
  }

  /**
   * Convert this value to String (if possible) or fallback to given value when missing.
   *
   * @param defaultValue Default value.
   * @return Convert this value to String (if possible) or fallback to given value when missing.
   */
  @Nonnull default String value(@Nonnull String defaultValue) {
    try {
      return value();
    } catch (MissingValueException x) {
      return defaultValue;
    }
  }

  /**
   * Convert this value to String (if possible) or <code>null</code> when missing.
   *
   * @return Convert this value to String (if possible) or <code>null</code> when missing.
   */
  @Nullable default String valueOrNull() {
    return value((String) null);
  }

  /**
   * Convert value using the given function.
   *
   * @param fn Function.
   * @param <T> Target type.
   * @return Converted value.
   */
  @Nonnull default <T> T value(@Nonnull SneakyThrows.Function<String, T> fn) {
    return fn.apply(value());
  }

  /**
   * Get string value.
   *
   * @return String value.
   */
  @Nonnull String value();

  /**
   * Get list of values.
   *
   * @return List of values.
   */
  @Nonnull default List<String> toList() {
    return Collections.emptyList();
  }

  /**
   * Get set of values.
   *
   * @return set of values.
   */
  @Nonnull default Set<String> toSet() {
    return Collections.emptySet();
  }

  /**
   * Convert this value to an Enum.
   *
   * @param fn Mapping function.
   * @param <T> Enum type.
   * @return Enum.
   */
  @Nonnull default <T extends Enum<T>> T toEnum(@Nonnull SneakyThrows.Function<String, T> fn) {
    return toEnum(fn, String::toUpperCase);
  }

  /**
   * Convert this value to an Enum.
   *
   * @param fn Mapping function.
   * @param nameProvider Enum name provider.
   * @param <T> Enum type.
   * @return Enum.
   */
  @Nonnull default <T extends Enum<T>> T toEnum(@Nonnull SneakyThrows.Function<String, T> fn,
      @Nonnull Function<String, String> nameProvider) {
    return fn.apply(nameProvider.apply(value()));
  }

  /**
   * Get a value or empty optional.
   *
   * @return Value or empty optional.
   */
  @Nonnull default Optional<String> toOptional() {
    try {
      return Optional.of(value());
    } catch (MissingValueException x) {
      return Optional.empty();
    }
  }

  /**
   * True if this is a single value (not a hash or array).
   *
   * @return True if this is a single value (not a hash or array).
   */
  default boolean isSingle() {
    return this instanceof SingleValue;
  }

  /**
   * True for missing values.
   *
   * @return True for missing values.
   */
  default boolean isMissing() {
    return this instanceof MissingValue;
  }

  /**
   * Name of this value or <code>null</code>.
   *
   * @return Name of this value or <code>null</code>.
   */
  @Nullable String name();

  /**
   * Value as multi-value map.
   *
   * @return Value as multi-value map.
   */
  @Nullable Map<String, List<String>> toMultimap();

  /**
   * Value as single-value map.
   *
   * @return Value as single-value map.
   */
  default @Nonnull Map<String, String> toMap() {
    Map<String, String> map = new LinkedHashMap<>();
    toMultimap().forEach((k, v) -> map.put(k, v.get(0)));
    return map;
  }

  /* ***********************************************************************************************
   * Factory methods
   * ***********************************************************************************************
   */

  /**
   * Creates a missing value.
   *
   * @param name Name of missing value.
   * @return Missing value.
   */
  static @Nonnull ValueNode missing(@Nonnull String name) {
    return new MissingValue(name);
  }

  /**
   * Creates a single value.
   *
   * @param ctx Current context.
   * @param name Name of value.
   * @param value Value.
   * @return Single value.
   */
  static @Nonnull ValueNode value(@Nonnull Context ctx, @Nonnull String name, @Nonnull String value) {
    return new SingleValue(ctx, name, value);
  }

  /**
   * Creates a sequence/array of values.
   *
   * @param ctx Current context.
   * @param name Name of array.
   * @param values Field values.
   * @return Array value.
   */
  static @Nonnull ValueNode array(@Nonnull Context ctx, @Nonnull String name,
      @Nonnull List<String> values) {
    return new ArrayValue(ctx, name)
        .add(values);
  }

  /**
   * Creates a value that fits better with the given values.
   *
   * - For null/empty values. It produces a missing value.
   * - For single element (size==1). It produces a single value
   * - For multi-value elements (size&gt;1). It produces an array value.
   *
   * @param ctx Current context.
   * @param name Field name.
   * @param values Field values.
   * @return A value.
   */
  static @Nonnull ValueNode create(Context ctx, @Nonnull String name, @Nullable List<String> values) {
    if (values == null || values.size() == 0) {
      return missing(name);
    }
    if (values.size() == 1) {
      return value(ctx, name, values.get(0));
    }
    return new ArrayValue(ctx, name)
        .add(values);
  }

  /**
   * Creates a value that fits better with the given values.
   *
   * - For null/empty values. It produces a missing value.
   * - For single element (size==1). It produces a single value
   *
   * @param ctx Current context.
   * @param name Field name.
   * @param value Field values.
   * @return A value.
   */
  static @Nonnull ValueNode create(Context ctx, @Nonnull String name, @Nullable String value) {
    if (value == null) {
      return missing(name);
    }
    return value(ctx, name, value);
  }

  /**
   * Create a hash/object value using the map values.
   *
   * @param ctx Current context.
   * @param values Map values.
   * @return A hash/object value.
   */
  static @Nonnull ValueNode hash(Context ctx, @Nonnull Map<String, Collection<String>> values) {
    return new HashValue(ctx, null).put(values);
  }

}
