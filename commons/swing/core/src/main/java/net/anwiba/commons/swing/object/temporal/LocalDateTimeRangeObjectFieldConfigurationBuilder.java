/*
 * #%L
 * anwiba commons swing
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
package net.anwiba.commons.swing.object.temporal;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

import net.anwiba.commons.swing.dialog.DialogMessages;
import net.anwiba.commons.swing.object.AbstractObjectFieldConfigurationBuilder;
import net.anwiba.commons.utilities.time.ILocalDateTimeRange;
import net.anwiba.commons.utilities.time.LocalDateTimeRangeStringValidator;
import net.anwiba.commons.utilities.time.LocalDateTimeRangeToStringConverter;
import net.anwiba.commons.utilities.time.StringToLocalDateTimeRangeConverter;

public class LocalDateTimeRangeObjectFieldConfigurationBuilder extends
    AbstractObjectFieldConfigurationBuilder<ILocalDateTimeRange, LocalDateTimeRangeObjectFieldConfigurationBuilder> {

  private static SimpleDateFormat format = new SimpleDateFormat(DialogMessages.FORMAT_DATE);
  private static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
      .parseCaseInsensitive()
      .append(DateTimeFormatter.ISO_LOCAL_DATE)
      .appendLiteral(' ')
      .append(
          new DateTimeFormatterBuilder()
              .appendValue(HOUR_OF_DAY, 2)
              .appendLiteral(':')
              .appendValue(MINUTE_OF_HOUR, 2)
              .toFormatter(Locale.getDefault()))
      .toFormatter(Locale.getDefault());

  public LocalDateTimeRangeObjectFieldConfigurationBuilder() {
    super(
        new LocalDateTimeRangeStringValidator(formatter),
        new StringToLocalDateTimeRangeConverter(formatter),
        new LocalDateTimeRangeToStringConverter(formatter));
  }
}
