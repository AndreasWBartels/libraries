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

public class UserDateTimeUtilities {

  public static ZonedDateTime asZoneDateTime(final LocalDateTime userDateTime) {
    return userDateTime.atZone(ZonedDateTimeUtilities.getUserZone());
  }

  public static LocalDateTime now() {
    return LocalDateTimeUtilities.atZone(ZonedDateTime.now(), ZonedDateTimeUtilities.getUserZone());
  }

  public static LocalDateTime atUserZone(final ZonedDateTime dateTime) {
    return LocalDateTimeUtilities.atZone(dateTime, ZonedDateTimeUtilities.getUserZone());
  }

  public static LocalDateTime atUserZone(final LocalDateTime localSystemDateTime) {
    return LocalDateTimeUtilities
        .atZone(localSystemDateTime, ZonedDateTimeUtilities.getSystemZone(), ZonedDateTimeUtilities.getUserZone());
  }

  public static LocalDateTime atSystemZone(final LocalDateTime userDateTime) {
    return LocalDateTimeUtilities
        .atZone(userDateTime, ZonedDateTimeUtilities.getUserZone(), ZonedDateTimeUtilities.getSystemZone());
  }

  public static LocalDateTime atCoordinatedUniversalTimeZone(final LocalDateTime userDateTime) {
    return LocalDateTimeUtilities.atZone(
        userDateTime,
        ZonedDateTimeUtilities.getUserZone(),
        ZonedDateTimeUtilities.getCoordinatedUniversalTimeZone());
  }

  public static LocalDateTime atUserZone(final LocalDateTime localSystemDateTime, final ZoneId sourceZone) {
    return LocalDateTimeUtilities.atZone(localSystemDateTime, sourceZone, ZonedDateTimeUtilities.getUserZone());
  }

  public static LocalDateTime atUserZone(final java.sql.Date date) {
    return atUserZone(new Date(date.getTime()));
  }

  public static LocalDateTime atUserZone(final Date date) {
    return LocalDateTime.ofInstant(date.toInstant(), ZonedDateTimeUtilities.getUserZone());
  }

  public static String toString(final ZonedDateTime dateTime) {
    final LocalDateTime userDateTime = atUserZone(dateTime);
    return userDateTime.atZone(ZonedDateTimeUtilities.getSystemZone()).format(
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

  public static String toStringAtSystemZone(final LocalDateTime userDateTime) {
    final LocalDateTime systemDateTime = atSystemZone(userDateTime);
    return systemDateTime.format(
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

}
