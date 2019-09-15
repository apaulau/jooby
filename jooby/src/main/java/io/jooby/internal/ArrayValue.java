/**
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.internal;

import io.jooby.Context;
import io.jooby.MissingValueException;
import io.jooby.TypeMismatchException;
import io.jooby.ValueNode;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ArrayValue implements ValueNode {
  private final Context ctx;

  private final String name;

  private final List<ValueNode> value = new ArrayList<>(5);

  public ArrayValue(Context ctx, String name) {
    this.ctx = ctx;
    this.name = name;
  }

  @Override public String name() {
    return name;
  }

  public ArrayValue add(ValueNode value) {
    this.value.add(value);
    return this;
  }

  public ArrayValue add(List<String> values) {
    for (String value : values) {
      add(value);
    }
    return this;
  }

  public ArrayValue add(String value) {
    return this.add(new SingleValue(ctx, name, value));
  }

  @Override public ValueNode get(@Nonnull int index) {
    try {
      return value.get(index);
    } catch (IndexOutOfBoundsException x) {
      return new MissingValue(name + "[" + index + "]");
    }
  }

  @Override public ValueNode get(@Nonnull String name) {
    return new MissingValue(this.name + "." + name);
  }

  @Override public int size() {
    return value.size();
  }

  @Override public String value() {
    String name = name();

    throw new TypeMismatchException(name == null ? getClass().getSimpleName() : name, String.class);
  }

  @Override public String toString() {
    return value.toString();
  }

  @Override public Iterator<ValueNode> iterator() {
    return value.iterator();
  }

  @Nonnull @Override public <T> T to(@Nonnull Class<T> type) {
    return ctx.convert(value.get(0), type);
  }

  @Nonnull @Override public <T> List<T> toList(@Nonnull Class<T> type) {
    List<T> list = new ArrayList<>(value.size());
    for (ValueNode it : value) {
      list.add(it.to(type));
    }
    return list;
  }

  @Nonnull @Override public <T> Optional<T> toOptional(@Nonnull Class<T> type) {
    try {
      return Optional.ofNullable(to(type));
    } catch (MissingValueException x) {
      return Optional.empty();
    }
  }

  @Nonnull @Override public <T> Set<T> toSet(@Nonnull Class<T> type) {
    Set<T> list = new LinkedHashSet<>(value.size());
    for (ValueNode it : value) {
      list.add(it.to(type));
    }
    return list;
  }

  @Override public Map<String, List<String>> toMultimap() {
    List<String> values = new ArrayList<>();
    value.stream().forEach(it -> it.toMultimap().values().forEach(values::addAll));
    return Collections.singletonMap(name, values);
  }

  @Override public List<String> toList() {
    return fill(new ArrayList<>());
  }

  @Override public Set<String> toSet() {
    return fill(new LinkedHashSet<>());
  }

  private <C extends Collection<String>> C fill(C values) {
    value.forEach(v -> values.addAll(v.toList()));
    return values;
  }
}
