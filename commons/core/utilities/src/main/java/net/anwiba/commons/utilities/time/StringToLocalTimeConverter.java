/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.utilities.time;

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.utilities.string.StringUtilities;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class StringToLocalTimeConverter implements IConverter<String, LocalTime, RuntimeException> {

  private final DateTimeFormatter formatter;

  public StringToLocalTimeConverter(final DateTimeFormatter formatter) {
    this.formatter = formatter;
  }

  @Override
  public LocalTime convert(final String value) {
    if (StringUtilities.isNullOrTrimmedEmpty(value)) {
      return null;
    }
    try {
      return LocalTime.from(this.formatter.parse(value));
    } catch (final DateTimeParseException exception) {
      return null;
    }
  }
}