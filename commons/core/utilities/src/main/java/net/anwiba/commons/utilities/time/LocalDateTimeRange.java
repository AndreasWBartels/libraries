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
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

public class LocalDateTimeRange implements ILocalDateTimeRange {

  private static final long serialVersionUID = 1L;
  private final LocalDateTime from;
  private final LocalDateTime until;

  public LocalDateTimeRange(final LocalDateTime from, final LocalDateTime until) {
    this.from = from;
    this.until = until;
  }

  @Override
  public LocalDateTime getFrom() {
    return this.from;
  }

  @Override
  public LocalDateTime getUntil() {
    return this.until;
  }

  @Override
  public LocalDateTime getCenter() {
    return this.from.plus(getDuration().dividedBy(2));
  }

  @Override
  public Duration getDuration() {
    return Duration.between(this.from, this.until);
  }

  @Override
  public boolean interact(final LocalDateTime time) {
    if (time == null) {
      return false;
    }
    return (this.from.isBefore(time) || this.from.isEqual(time)) && getUntil().isAfter(time);
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
    LocalDateTime from = segment.getFrom().isAfter(getFrom())
        ? segment.getFrom()
        : getFrom();
    LocalDateTime until = segment.getUntil().isBefore(getUntil())
        ? segment.getUntil()
        : getUntil();
    return new LocalDateTimeRange(from, until);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.from, this.until);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof LocalDateTimeRange)) {
      return false;
    }
    LocalDateTimeRange other = (LocalDateTimeRange) obj;
    return Objects.equals(this.from, other.from) && Objects.equals(this.until, other.until);
  }

  @Override
  public Duration getDurationFromUntilToNow() {
    final LocalDateTime now = now();
    if (now.isBefore(this.until)) {
      return Duration.between(now, this.until);
    }
    return Duration.between(this.until, now);
  }

  private LocalDateTime now() {
    return UserDateTimeUtilities.now();
  }

  @Override
  public Duration getDurationFromFromToNow() {
    final LocalDateTime now = now();
    if (now.isBefore(this.from)) {
      return Duration.between(now, this.from);
    }
    return Duration.between(this.from, now);
  }

  @Override
  public ILocalDateTimeRange shift(final long value, final TemporalUnit unit) {
    return new LocalDateTimeRange(this.from.plus(value, unit), this.until.plus(value, unit));
  }

  @Override
  public ILocalDateTimeRange duration(final long value, final TemporalUnit unit) {
    final LocalDateTime result = this.from.plus(value, unit);
    if (result.isBefore(this.from)) {
      return new LocalDateTimeRange(this.from, this.from.plus(1, ChronoUnit.SECONDS));
    }
    return new LocalDateTimeRange(this.from, result);
  }

  @Override
  public ILocalDateTimeRange plus(final long value, final TemporalUnit unit) {
    return new LocalDateTimeRange(this.from, this.until.plus(value, unit));
  }

  @Override
  public ILocalDateTimeRange minus(final long value, final TemporalUnit unit) {
    final LocalDateTime result = this.until.minus(value, unit);
    if (result.isBefore(this.from)) {
      return new LocalDateTimeRange(this.from, this.from.plus(1, ChronoUnit.SECONDS));
    }
    return new LocalDateTimeRange(this.from, result);
  }

  @Override
  public ILocalDateTimeRange toRelative() {
    return RelativeLocalDateTimeRange.of(getDurationFromFromToNow(),
        getDurationFromUntilToNow(),
        ChronoUnit.SECONDS);
  }

  @Override
  public ILocalDateTimeRange toAbsolute() {
    return new LocalDateTimeRange(this.from, this.until);
  }

  @Override
  public ILocalDateTimeRange toRelative(final TemporalUnit unit) {
    return toRelative().toRelative(unit);
  }

}
