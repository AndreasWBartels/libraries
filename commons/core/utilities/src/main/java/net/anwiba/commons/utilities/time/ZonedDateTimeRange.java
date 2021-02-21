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
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.Objects;

public class ZonedDateTimeRange implements IZonedDateTimeRange {

  private static final long serialVersionUID = 1L;
  private final ZonedDateTime from;
  private final ZonedDateTime until;

  public static IZonedDateTimeRange of(final ZonedDateTime from, final Duration duration) {
    return new ZonedDateTimeRange(from, from.plus(duration));
  }

  public static IZonedDateTimeRange of(final ZonedDateTime from, final Period period) {
    return new ZonedDateTimeRange(from, from.plus(period));
  }

  public ZonedDateTimeRange(final ZonedDateTime from, final ZonedDateTime until) {
    this.from = from;
    this.until = until;
  }

  @Override
  public ZonedDateTime getFrom() {
    return this.from;
  }

  @Override
  public ZonedDateTime getUntil() {
    return this.until;
  }

  @Override
  public ZonedDateTime getCenter() {
    return this.from.plus(getDuration().dividedBy(2));
  }

  @Override
  public Duration getDuration() {
    return Duration.between(this.from, this.until);
  }

  @Override
  public boolean interact(final ZonedDateTime time) {
    if (time == null) {
      return false;
    }
    return (this.from.isBefore(time) || this.from.isEqual(time)) && getUntil().isAfter(time);
  }

  @Override
  public boolean interact(final IZonedDateTimeRange segment) {
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
  public IZonedDateTimeRange intersection(final IZonedDateTimeRange segment) {
    if (!interact(segment)) {
      return null;
    }
    ZonedDateTime from = segment.getFrom().isAfter(getFrom())
        ? segment.getFrom()
        : getFrom();
    ZonedDateTime until = segment.getUntil().isBefore(getUntil())
        ? segment.getUntil()
        : getUntil();
    return new ZonedDateTimeRange(from, until);
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
    if (!(obj instanceof ZonedDateTimeRange)) {
      return false;
    }
    ZonedDateTimeRange other = (ZonedDateTimeRange) obj;
    return Objects.equals(this.from, other.from) && Objects.equals(this.until, other.until);
  }

}
