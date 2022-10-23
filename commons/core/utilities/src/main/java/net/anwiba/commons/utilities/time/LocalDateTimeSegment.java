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

import net.anwiba.commons.lang.optional.Optional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class LocalDateTimeSegment implements Serializable {

  private static final long serialVersionUID = 1L;
  private final LocalDateTime time;
  private final int resolution;
  private final TimeUnit unit;
  private final ILocalDateTimeRange timeRange;

  public LocalDateTimeSegment(final LocalDateTime time, final int resolution, final TimeUnit unit) {
    this.time = time;
    this.timeRange = null;
    this.resolution = resolution;
    this.unit = unit;
  }

  public LocalDateTimeSegment(final ILocalDateTimeRange timeRange,
      final int resolution,
      final TimeUnit unit) {
    this.time = null;
    this.timeRange = timeRange;
    this.resolution = resolution;
    this.unit = unit;
  }

  public LocalDateTime getTime() {
    return Optional.of(this.time)
        .getOr(() -> Optional.of(this.timeRange)
            .convert(r -> convert(r))
            .getOr(() -> UserDateTimeUtilities.now().toLocalDateTime()));
  }

  private LocalDateTime convert(final ILocalDateTimeRange range) {
    return range.getFrom();
  }

  public int getDuration() {
    return this.resolution;
  }

  public TimeUnit getTimeUnit() {
    return this.unit;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.time, this.timeRange, this.resolution, this.unit);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof LocalDateTimeSegment)) {
      return false;
    }
    LocalDateTimeSegment other = (LocalDateTimeSegment) obj;
    return Objects.equals(this.time, other.time)
        && Objects.equals(this.timeRange, other.timeRange)
        && this.resolution == other.resolution
        && this.unit == other.unit;
  }

  public ILocalDateTimeRange toLocalDateTimeRange() {
    return Optional.of(this.timeRange)
        .getOr(() -> Optional.of(this.time)
            .convert(t -> new LocalDateTimeRange(t, t.plus(this.resolution, this.unit.toTemporalUnit())))
            .get());
  }

  public boolean isRelative() {
    return this.timeRange instanceof RelativeLocalDateTimeRange;
  }

}
