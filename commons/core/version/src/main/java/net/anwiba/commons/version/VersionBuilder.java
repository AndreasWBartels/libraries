/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.commons.version;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

public class VersionBuilder {

  private int major = 0;
  private int minor = 0;
  private ReleaseState releaseState = ReleaseState.RELEASE;
  private int step = 0;
  private ProductState productState = ProductState.STABLE;
  private int year = 0;
  private int month = 1;
  private int day = 1;
  private int hour = 0;
  private int minute = 0;
  private int count = 0;
  private final ZoneId timeZone = Clock.systemUTC().getZone();

  public VersionBuilder setMajor(final int value) {
    if (value == Integer.MIN_VALUE) {
      this.major = 0;
    }
    this.major = value;
    return this;
  }

  public VersionBuilder setMinor(final int value) {
    if (value == Integer.MIN_VALUE) {
      this.minor = 0;
    }
    this.minor = value;
    return this;
  }

  public VersionBuilder setStep(final int value) {
    if (value == Integer.MIN_VALUE) {
      this.step = 0;
    }
    this.step = value;
    return this;
  }

  public VersionBuilder setReleaseState(final ReleaseState releaseState) {
    this.releaseState = releaseState;
    return this;
  }

  public VersionBuilder setProductState(final ProductState productState) {
    this.productState = productState;
    return this;
  }

  public VersionBuilder setYear(final int value) {
    if (value == Integer.MIN_VALUE) {
      this.year = 0;
    }
    this.year = value;
    return this;
  }

  public VersionBuilder setMonth(final int value) {
    if (value == Integer.MIN_VALUE) {
      this.month = 0;
    }
    this.month = value;
    return this;
  }

  public VersionBuilder setDay(final int value) {
    if (value == Integer.MIN_VALUE) {
      this.day = 0;
    }
    this.day = value;
    return this;
  }

  public VersionBuilder setHour(final int value) {
    if (value == Integer.MIN_VALUE) {
      this.hour = 0;
    }
    this.hour = value;
    return this;
  }

  public VersionBuilder setMinute(final int value) {
    if (value == Integer.MIN_VALUE) {
      this.minute = 0;
    }
    this.minute = value;
    return this;
  }

  public VersionBuilder setCount(final int value) {
    if (value == Integer.MIN_VALUE) {
      this.count = 0;
    }
    this.count = value;
    return this;
  }

  public IVersion build() {
    return new Version(
        this.major,
        this.minor,
        this.releaseState,
        this.step,
        this.productState,
        ZonedDateTime.of(this.year, this.month, this.day, this.hour, this.minute, 0, 0, this.timeZone),
        // new GregorianCalendar(this.year, this.month - 1, this.day, this.hour, this.minute, 0).getTime(),
        this.count);
  }

  public VersionBuilder setMajor(final String value) {
    setMajor(convertToInt(value));
    return this;
  }

  public VersionBuilder setMinor(final String value) {
    setMinor(convertToInt(value));
    return this;
  }

  public VersionBuilder setStep(final String value) {
    setStep(convertToInt(value));
    return this;
  }

  public VersionBuilder setYear(final String value) {
    setYear(convertToInt(value));
    return this;
  }

  public VersionBuilder setMonth(final String value) {
    setMonth(convertToInt(value));
    return this;
  }

  public VersionBuilder setDay(final String value) {
    setDay(convertToInt(value));
    return this;
  }

  public VersionBuilder setHour(final String value) {
    setHour(convertToInt(value));
    return this;
  }

  public VersionBuilder setMinute(final String value) {
    setMinute(convertToInt(value));
    return this;
  }

  public VersionBuilder setCount(final String value) {
    setCount(convertToInt(value));
    return this;
  }

  private int convertToInt(final String value) {
    if (isBlank(value)) {
      return Integer.MIN_VALUE;
    }
    return Integer.valueOf(value.trim()).intValue();
  }

  public VersionBuilder setProductState(final String string) {
    if (isBlank(string)) {
      this.productState = ProductState.STABLE;
      return this;
    }
    final String upperCase = string.toUpperCase();
    for (final ProductState state : ProductState.values()) {
      if (Objects.equals(upperCase, state.getAcronym()) || Objects.equals(upperCase, state.name())) {
        this.productState = state;
        return this;
      }
    }
    this.productState = ProductState.STABLE;
    return this;

  }

  public VersionBuilder setReleaseState(final String string) {
    if (isBlank(string)) {
      this.releaseState = ReleaseState.RELEASE;
      return this;
    }
    final String upperCase = string.toUpperCase();
    for (final ReleaseState state : ReleaseState.values()) {
      if (Objects.equals(upperCase, state.getAcronym()) || Objects.equals(upperCase, state.name())) {
        this.releaseState = state;
        return this;
      }
    }
    this.releaseState = ReleaseState.RELEASE;
    return this;
  }

  protected boolean isBlank(final String string) {
    return string == null || string.isBlank();
  }
}
