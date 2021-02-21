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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ZonedDateTimeUtilities {

  public static ZonedDateTime atTimeZone(final java.sql.Date date, final ZoneId zone) {
    return atTimeZone(new Date(date.getTime()), zone);
  }

  public static ZonedDateTime atTimeZone(final Date date, final ZoneId zone) {
    if (date instanceof java.sql.Date) {
      final Instant instant = new Date(date.getTime()).toInstant();
      return instant.atZone(zone);
    }
    final Instant instant = date.toInstant();
    return instant.atZone(zone);
  }

  public static ZonedDateTime atTimeZone(final ZonedDateTime zonedDateTime, final ZoneId zoneId) {
    return zonedDateTime.toInstant().atZone(zoneId);
  }

  public static ZonedDateTime atCoordinatedUniversalTimeZone(final Date value) {
    return atTimeZone(value, getCoordinatedUniversalTimeZone());
  }

  public static ZonedDateTime atCoordinatedUniversalTimeZone(final LocalDateTime localDateTime, final ZoneId zoneId) {
    return localDateTime.atZone(zoneId).toInstant().atZone(getCoordinatedUniversalTimeZone());
  }

  public static ZonedDateTime atCoordinatedUniversalTimeZone(final ZonedDateTime zonedDateTime) {
    return zonedDateTime.toInstant().atZone(getCoordinatedUniversalTimeZone());
  }

  public static ZonedDateTime atSystemTimeZone(final ZonedDateTime zonedDateTime) {
    return zonedDateTime.toInstant().atZone(getSystemZone());
  }

  public static ZonedDateTime atUserTimeZone(final ZonedDateTime zonedDateTime) {
    return zonedDateTime.toInstant().atZone(getUserZone());
  }

  public static ZonedDateTime atUserTimeZone(final LocalDateTime dateTime) {
    return dateTime.atZone(ZonedDateTimeUtilities.getUserZone());
  }

  public static ZonedDateTime atCoordinatedUniversalTimeZone(final LocalDateTime dateTime) {
    return dateTime.atZone(ZonedDateTimeUtilities.getCoordinatedUniversalTimeZone());
  }

  public static ZonedDateTime atSystemTimeZone(final LocalDateTime dateTime) {
    return dateTime.atZone(ZonedDateTimeUtilities.getSystemZone());
  }

  public static ZoneId getUserZone() {
    return TimeZoneUtilities.getUserTimeZone().toZoneId();
  }

  public static ZoneId getSystemZone() {
    return TimeZoneUtilities.getSystemTimeZone().toZoneId();
  }

  public static ZoneId getCoordinatedUniversalTimeZone() {
    return ZoneId.of("UTC"); //$NON-NLS-1$
  }

  public static Date toDate(final ZonedDateTime dateTime) {
    return Date.from(dateTime.toInstant());
  }

  public static String toString(final ZonedDateTime dateTime) {
    return dateTime.format(DateTimeFormatter.ISO_INSTANT);
  }

  public static ZonedDateTime valueOf(final String date) {
    return ZonedDateTime.parse(date);
  }

}
