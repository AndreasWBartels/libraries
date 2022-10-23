/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2019 Andreas W. Bartels
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

public class TemporalAmountUtilities {

  final public static LocalDateTime ZERO_DATE_TIME = LocalDateTimeUtilities.ZERO_DATE_TIME;

  public static Duration duration(final int year,
      final int month,
      final int dayOfMonth,
      final int hour,
      final int minute,
      final int second) {
    final LocalDateTime time = time(year, month, dayOfMonth, hour, minute, second);
    return Duration.between(ZERO_DATE_TIME, time);
  }

  public static Duration duration(final TemporalAmount value) {
    final LocalDateTime time = toDateTime(value);
    return Duration.between(ZERO_DATE_TIME, time);
  }

  public static Period period(final TemporalAmount value) {
    final LocalDateTime time = toDateTime(value);
    return Period.between(ZERO_DATE_TIME.toLocalDate(), time.toLocalDate());
  }

  public static TemporalAmount temporalAmount(final Duration duration, final TemporalUnit temporalUnit) {
    if (temporalUnit.isDateBased()) {
      return period(duration);
    }
    return duration;
  }

  public static Duration mod(final TemporalAmount value) {
    final LocalDateTime time = toDateTime(value);
    return Duration.between(ZERO_DATE_TIME.toLocalTime(), time.toLocalTime());
  }

  public static Period period(final int year,
      final int month,
      final int dayOfMonth,
      final int hour,
      final int minute,
      final int second) {
    final LocalDateTime time = time(year, month, dayOfMonth, hour, minute, second);
    return Period.between(ZERO_DATE_TIME.toLocalDate(), time.toLocalDate());
  }

  private static LocalDateTime time(final int year,
      final int month,
      final int dayOfMonth,
      final int hour,
      final int minute,
      final int second) {
    return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
  }

  public static TemporalAmount plus(final TemporalAmount value, final long count, final TemporalUnit unit) {
    final LocalDateTime dateTime = toDateTime(value).plus(count, unit);
    if (unit.isDateBased()) {
      return toPeriod(dateTime);
    }
    return toDuration(dateTime);
  }

  public static TemporalAmount minus(final TemporalAmount value, final long count, final TemporalUnit unit) {
    final LocalDateTime dateTime = toDateTime(value).minus(count, unit);
    if (unit.isDateBased()) {
      return toPeriod(dateTime);
    }
    return toDuration(dateTime);
  }

  public static TemporalAmount round(final TemporalAmount value, final TemporalUnit unit) {
    TemporalAmount floor = floor(value, unit);
    TemporalAmount ceil = plus(floor, 1, unit);
    if (duration(value).minus(duration(floor)).compareTo(duration(ceil).minus(duration(value))) > 0) {
      return ceil;
    }
    return floor;
  }

  public static TemporalAmount ceil(final TemporalAmount value, final TemporalUnit unit) {
    return plus(floor(value, unit), 1, unit);
  }

  public static TemporalAmount floor(final TemporalAmount value, final TemporalUnit unit) {
    LocalDateTime time = floor(toDateTime(value), unit);
    if (ChronoUnit.SECONDS.getDuration().compareTo(unit.getDuration()) >= 0) {
      return Duration.between(ZERO_DATE_TIME, time);
    }
    if (ChronoUnit.MINUTES.getDuration().compareTo(unit.getDuration()) >= 0) {
      return Duration.between(ZERO_DATE_TIME, time);
    }
    if (ChronoUnit.HOURS.getDuration().compareTo(unit.getDuration()) >= 0) {
      return Duration.between(ZERO_DATE_TIME, time);
    }
    if (ChronoUnit.DAYS.getDuration().compareTo(unit.getDuration()) >= 0) {
      return Period.between(ZERO_DATE_TIME.toLocalDate(), time.toLocalDate());
    }
    if (ChronoUnit.MONTHS.getDuration().compareTo(unit.getDuration()) >= 0) {
      return Period.between(ZERO_DATE_TIME.toLocalDate(), time.toLocalDate());
    }
    if (ChronoUnit.YEARS.getDuration().compareTo(unit.getDuration()) >= 0) {
      return Period.between(ZERO_DATE_TIME.toLocalDate(), time.toLocalDate());
    }
    return Period.between(ZERO_DATE_TIME.toLocalDate(), time.toLocalDate());
  }

  public static LocalDateTime floor(final LocalDateTime time, final TemporalUnit unit) {
    LocalDateTime value = time.withNano(0);
    if (ChronoUnit.SECONDS.getDuration().compareTo(unit.getDuration()) >= 0) {
      return value;
    }
    value = value.withSecond(0);
    if (ChronoUnit.MINUTES.getDuration().compareTo(unit.getDuration()) >= 0) {
      return value;
    }
    value = value.withMinute(0);
    if (ChronoUnit.HOURS.getDuration().compareTo(unit.getDuration()) >= 0) {
      return value;
    }
    value = value.withHour(0);
    if (ChronoUnit.DAYS.getDuration().compareTo(unit.getDuration()) >= 0) {
      return value;
    }
    value = value.withDayOfMonth(1);
    if (ChronoUnit.MONTHS.getDuration().compareTo(unit.getDuration()) >= 0) {
      return value;
    }
    value = value.withMonth(1);
    if (ChronoUnit.YEARS.getDuration().compareTo(unit.getDuration()) >= 0) {
      return value;
    }
    value = value.withYear(0);
    return value;
  }

  public static LocalDateTime toDateTime(final TemporalAmount value) {
    return ZERO_DATE_TIME.plus(value);
  }

  public static Duration toDuration(final LocalDateTime value) {
    return Duration.between(ZERO_DATE_TIME, value.withNano(0));
  }

  public static Period toPeriod(final LocalDateTime value) {
    return Period.between(ZERO_DATE_TIME.toLocalDate(), value.toLocalDate());
  }

  public static TemporalAmount betweenNow(final LocalDateTime value, final TemporalUnit unit) {
    return between(UserDateTimeUtilities.now().toLocalDateTime(), value, unit);
  }

  public static TemporalAmount between(final LocalDateTime from, final LocalDateTime until, final TemporalUnit unit) {
    if (unit.isDateBased()) {
      //      return Period.between(from.toLocalDate(), until.toLocalDate());
      return Period.between(floor(from, unit).toLocalDate(), floor(until, unit).toLocalDate());
    }
    //    return Duration.between(from, until);
    return Duration.between(floor(from, unit), floor(until, unit));
  }
}
