/**
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.internal.converter;

import io.jooby.ValueNode;
import io.jooby.ValueConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter implements ValueConverter {
  @Override public boolean supports(Class type) {
    return type == LocalDate.class;
  }

  @Override public Object convert(ValueNode value, Class type) {
    try {
      // must be millis
      Instant instant = Instant.ofEpochMilli(Long.parseLong(value.value()));
      return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    } catch (NumberFormatException x) {
      // must be YYYY-MM-dd
      return LocalDate.parse(value.value(), DateTimeFormatter.ISO_LOCAL_DATE);
    }
  }
}
