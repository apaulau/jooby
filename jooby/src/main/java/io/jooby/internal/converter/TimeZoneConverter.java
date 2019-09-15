/**
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.internal.converter;

import io.jooby.ValueNode;
import io.jooby.ValueConverter;

import java.util.TimeZone;

public class TimeZoneConverter implements ValueConverter {
  @Override public boolean supports(Class type) {
    return type == TimeZone.class;
  }

  @Override public Object convert(ValueNode value, Class type) {
    return TimeZone.getTimeZone(value.value());
  }
}
