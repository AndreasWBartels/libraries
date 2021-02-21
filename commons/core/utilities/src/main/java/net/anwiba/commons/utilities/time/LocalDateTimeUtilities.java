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

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class LocalDateTimeUtilities {

  final public static LocalDateTime ZERO_DATE_TIME = LocalDateTime.of(0, 1, 1, 0, 0, 0);

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
    if (sourceDateTime == null) {
      return null;
    }
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

  public static LocalDateTime
      truncat(final LocalDateTime dateTime, final TemporalAmount duration, final TemporalUnit unit) {
    if (ChronoUnit.SECONDS.getDuration().compareTo(unit.getDuration()) >= 0) {
      final LocalDateTime truncated = dateTime.truncatedTo(unit);
      long mod = truncated.getSecond() % duration.get(ChronoUnit.SECONDS);
      return truncated.minus(mod, unit);
    }
    if (ChronoUnit.MINUTES.getDuration().compareTo(unit.getDuration()) >= 0) {
      final LocalDateTime truncated = dateTime.truncatedTo(unit);
      long mod = truncated.getMinute() % (duration.get(ChronoUnit.SECONDS) / 60);
      return truncated.minus(mod, unit);
    }
    if (ChronoUnit.HOURS.getDuration().compareTo(unit.getDuration()) >= 0) {
      final LocalDateTime truncated = dateTime.truncatedTo(unit);
      long mod = truncated.getHour() % (duration.get(ChronoUnit.SECONDS) / 3600);
      return truncated.minus(mod, unit);
    }
    if (ChronoUnit.DAYS.getDuration().compareTo(unit.getDuration()) >= 0) {
      final LocalDate truncated = dateTime.toLocalDate();
      final int days = (int) duration.get(ChronoUnit.DAYS);
      if (days == 0) {
        return LocalDateTime.of(truncated.minusDays(truncated.getDayOfMonth() - 1), LocalTime.ofSecondOfDay(0));
      }
      int mod = (truncated.getDayOfMonth() - 1) % days;
      return LocalDateTime.of(truncated.minusDays(mod), LocalTime.ofSecondOfDay(0));
    }
    if (ChronoUnit.MONTHS.getDuration().compareTo(unit.getDuration()) >= 0) {
      final LocalDate truncated = dateTime.toLocalDate().withDayOfMonth(1);
      int mod = (truncated.getMonthValue() - 1) % ((int) duration.get(ChronoUnit.MONTHS));
      return LocalDateTime.of(truncated.minusMonths(mod), LocalTime.ofSecondOfDay(0));
    }
    final LocalDate truncated = dateTime.toLocalDate().withDayOfMonth(1).withDayOfMonth(1);
    int mod = (truncated.getYear()) % ((int) duration.get(ChronoUnit.YEARS));
    return LocalDateTime.of(truncated.minusYears(mod), LocalTime.ofSecondOfDay(0));
  }

}
