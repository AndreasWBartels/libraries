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
import java.util.Objects;

public class RelativeLocalDateTimeRange implements ILocalDateTimeRange {

  private static final long serialVersionUID = 1L;
  private final TemporalAmount from;
  private final TemporalAmount until;
  private final TemporalUnit resolution;

  public static RelativeLocalDateTimeRange today() {
    return new RelativeLocalDateTimeRange(Period.ofDays(0), Period.ofDays(1), ChronoUnit.DAYS);
  }

  public static RelativeLocalDateTimeRange of(final TemporalAmount from, final int duration, final TemporalUnit unit) {
    return new RelativeLocalDateTimeRange(from, TemporalAmountUtilities.plus(from, duration, unit), unit);
  }

  public static RelativeLocalDateTimeRange minutes(final long minutes) {
    return new RelativeLocalDateTimeRange(Duration.ofMinutes(0), Duration.ofMinutes(minutes), ChronoUnit.MINUTES);
  }

  public static RelativeLocalDateTimeRange hours(final long hours) {
    return new RelativeLocalDateTimeRange(Duration.ofHours(0), Duration.ofHours(hours), ChronoUnit.HOURS);
  }

  public static RelativeLocalDateTimeRange days(final int days) {
    return new RelativeLocalDateTimeRange(Period.ofDays(0), Period.ofDays(days), ChronoUnit.DAYS);
  }

  public static RelativeLocalDateTimeRange months(final int months) {
    return new RelativeLocalDateTimeRange(Period.ofMonths(0), Period.ofMonths(months), ChronoUnit.MONTHS);
  }

  public static RelativeLocalDateTimeRange years(final int years) {
    return new RelativeLocalDateTimeRange(Period.ofYears(0), Period.ofYears(years), ChronoUnit.YEARS);
  }

  public static ILocalDateTimeRange of(final long value, final TemporalUnit temporalUnit) {
    if (!(temporalUnit instanceof ChronoUnit)) {
      throw new IllegalArgumentException();
    }
    ChronoUnit timeUnit = (ChronoUnit) temporalUnit;
    switch (timeUnit) {
      case YEARS: {
        return years((int) value);
      }
      case MONTHS: {
        return months((int) value);
      }
      case DAYS: {
        return days((int) value);
      }
      case HOURS: {
        return hours(value);
      }
      case MINUTES: {
        return minutes(value);
      }
      default:
        throw new IllegalArgumentException();
    }
  }

  public static RelativeLocalDateTimeRange of(final TemporalAmount from,
      final TemporalAmount until,
      final TemporalUnit unit) {
    final TemporalAmount _from = TemporalAmountUtilities.floor(from, unit);
    final TemporalAmount _until = TemporalAmountUtilities.floor(until, unit);
    if (Objects.equals(_from, _until)) {
      return new RelativeLocalDateTimeRange(_from, TemporalAmountUtilities.plus(_until, 1, unit), unit);
    }
    return new RelativeLocalDateTimeRange(_from, _until, unit);
  }

  private RelativeLocalDateTimeRange(final TemporalAmount from,
      final TemporalAmount until,
      final TemporalUnit resolution) {
    this.from = from;
    this.until = until;
    this.resolution = resolution;
  }

  final TemporalUnit getUnit() {
    return this.resolution;
  }

  @Override
  public LocalDateTime getFrom() {
    return now().plus(this.from);
  }

  @Override
  public LocalDateTime getUntil() {
    return now().plus(this.until);
  }

  @Override
  public LocalDateTime getCenter() {
    return getFrom().plus(Duration.between(getFrom(), getUntil()).dividedBy(2));
  }

  @Override
  public TemporalAmount getDuration() {
    if (this.resolution.isDateBased()) {
      return Period.between(getFrom().toLocalDate(), getUntil().toLocalDate());
    }
    return Duration.between(getFrom(), getUntil());
  }

  @Override
  public boolean interact(final LocalDateTime time) {
    if (time == null) {
      return false;
    }
    return (getFrom().isBefore(time) || getFrom().isEqual(time)) && getUntil().isAfter(time);
  }

  @Override
  public boolean interact(final ILocalDateTimeRange segment) {
    if (segment == null) {
      return false;
    }
    if (segment.getUntil().equals(getFrom()) || segment.getUntil().isBefore(getFrom())) {
      return false;
    }
    if (segment.getFrom().equals(getUntil()) || segment.getFrom().isAfter(getUntil())) {
      return false;
    }
    return true;
  }

  @Override
  public ILocalDateTimeRange intersection(final ILocalDateTimeRange segment) {
    if (!interact(segment)) {
      return null;
    }
    LocalDateTime _from = segment.getFrom().isAfter(getFrom())
        ? segment.getFrom()
        : getFrom();
    LocalDateTime _until = segment.getUntil().isBefore(getUntil())
        ? segment.getUntil()
        : getUntil();
    return new LocalDateTimeRange(_from, _until);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.from, this.until, this.resolution);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof RelativeLocalDateTimeRange)) {
      return false;
    }
    RelativeLocalDateTimeRange other = (RelativeLocalDateTimeRange) obj;
    return Objects.equals(this.from, other.from)
        && Objects.equals(this.until, other.until)
        && Objects.equals(this.resolution, other.resolution);
  }

  @Override
  public TemporalAmount getDurationFromUntilToNow() {
    final LocalDateTime now = now();
    final LocalDateTime plus = now.plus(this.until);
    if (now.isBefore(plus)) {
      return Duration.between(now, plus);
    }
    return this.until;
  }

  @Override
  public TemporalAmount getDurationFromFromToNow() {
    return this.from;
  }

  private LocalDateTime now() {
    LocalDateTime now = UserDateTimeUtilities.now();
    return TemporalAmountUtilities.floor(now, this.resolution);
  }

  @Override
  public ILocalDateTimeRange duration(final long value, final TemporalUnit unit) {
    final TemporalAmount _from = TemporalAmountUtilities.floor(this.from, unit);
    final TemporalAmount _until = TemporalAmountUtilities.plus(this.from, value, unit);
    if (TemporalAmountUtilities.toDateTime(_until).isBefore(TemporalAmountUtilities.toDateTime(_from))) {
      return new RelativeLocalDateTimeRange(this.from, TemporalAmountUtilities.plus(this.until, value, unit), unit);
    }
    return new RelativeLocalDateTimeRange(this.from, _until, unit);
  }

  @Override
  public ILocalDateTimeRange shift(final long value, final TemporalUnit unit) {
    return new RelativeLocalDateTimeRange(TemporalAmountUtilities.plus(this.from, value, unit),
        TemporalAmountUtilities.plus(this.until, value, unit),
        unit);
  }

  @Override
  public ILocalDateTimeRange plus(final long value, final TemporalUnit unit) {
    final TemporalAmount _from = TemporalAmountUtilities.floor(this.from, unit);
    final TemporalAmount _until = TemporalAmountUtilities.plus(this.until, value, unit);
    if (TemporalAmountUtilities.toDateTime(_until).isBefore(TemporalAmountUtilities.toDateTime(_from))) {
      return new RelativeLocalDateTimeRange(this.from, TemporalAmountUtilities.plus(this.until, value, unit), unit);
    }
    return new RelativeLocalDateTimeRange(this.from, _until, unit);
  }

  @Override
  public ILocalDateTimeRange minus(final long value, final TemporalUnit unit) {
    final TemporalAmount _from = TemporalAmountUtilities.floor(this.from, unit);
    final TemporalAmount _until = TemporalAmountUtilities.minus(this.until, value, unit);
    if (TemporalAmountUtilities.toDateTime(_until).isBefore(TemporalAmountUtilities.toDateTime(_from))) {
      return new RelativeLocalDateTimeRange(this.from, TemporalAmountUtilities.plus(this.until, value, unit), unit);
    }
    return new RelativeLocalDateTimeRange(this.from, _until, unit);
  }

  @Override
  public ILocalDateTimeRange toRelative() {
    return new RelativeLocalDateTimeRange(this.from, this.until, this.resolution);
  }

  @Override
  public ILocalDateTimeRange toAbsolute() {
    return new LocalDateTimeRange(getFrom(), getUntil());
  }

  @Override
  public ILocalDateTimeRange toRelative(final TemporalUnit unit) {
    if (Objects.equals(unit, this.resolution)) {
      return toRelative();
    }
    final TemporalAmount _from = TemporalAmountUtilities.floor(this.from, unit);
    final TemporalAmount _until = TemporalAmountUtilities.floor(this.until, unit);
    if (Objects.equals(_from, _until)) {
      return new RelativeLocalDateTimeRange(_from, TemporalAmountUtilities.plus(_until, 1, unit), unit);
    }
    return new RelativeLocalDateTimeRange(_from, _until, unit);
  }
}
