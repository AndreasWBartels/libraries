/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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

import static java.time.temporal.ChronoField.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class LocalDateTimeUtilities {

  public static LocalDateTime atCoordinatedUniversalTimeZone(final ZonedDateTime dateTime) {
    return atZone(dateTime, ZonedDateTimeUtilities.getCoordinatedUniversalTimeZone());
  }

  public static LocalDateTime atZone(final ZonedDateTime dateTime, final ZoneId targetZone) {
    return dateTime.toInstant().atZone(targetZone).toLocalDateTime();
  }

  public static LocalDateTime atZone(final java.sql.Date date, final ZoneId zone) {
    return atZone(new Date(date.getTime()), zone);
  }

  public static LocalDateTime atZone(final Date date, final ZoneId zone) {
    return LocalDateTime.ofInstant(date.toInstant(), zone);
  }

  public static LocalDateTime atZone(
      final LocalDateTime sourceDateTime,
      final ZoneId sourceZone,
      final ZoneId targetZone) {
    if (Objects.equals(sourceZone, targetZone)) {
      return sourceDateTime;
    }
    final ZonedDateTime zonedSourceDateTime = sourceDateTime.atZone(sourceZone);
    final ZonedDateTime zonedTargetDateTime = zonedSourceDateTime.toInstant().atZone(targetZone);
    return zonedTargetDateTime.toLocalDateTime();
  }

  public static String toString(final LocalDateTime dateTime) {
    return dateTime.format(
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(
                new DateTimeFormatterBuilder()
                    .appendValue(HOUR_OF_DAY, 2)
                    .appendLiteral(':')
                    .appendValue(MINUTE_OF_HOUR, 2)
                    .toFormatter(Locale.getDefault()))
            .toFormatter(Locale.getDefault()));
  }

  public static LocalDateTime atCoordinatedUniversalTimeZone(final java.sql.Date date) {
    return atCoordinatedUniversalTimeZone(new Date(date.getTime()));
  }

  private static LocalDateTime atCoordinatedUniversalTimeZone(final Date date) {
    return LocalDateTime.ofInstant(date.toInstant(), ZonedDateTimeUtilities.getCoordinatedUniversalTimeZone());
  }

}
