/*
 * #%L
 * anwiba commons advanced
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
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

import java.time.ZonedDateTime;
import java.util.Objects;

import net.anwiba.commons.utilities.time.ZonedDateTimeUtilities;

public class Version implements IVersion {

  public static final ZonedDateTime defaultDate = ZonedDateTime
      .of(0, 1, 1, 0, 0, 0, 0, ZonedDateTimeUtilities.getCoordinatedUniversalTimeZone());
  public static final IVersion DUMMY = new Version(
      0,
      0,
      ReleaseState.RELEASE,
      0,
      ProductState.EXPERIMENTAL,
      defaultDate,
      0);

  private final int major;
  private final int minor;
  private final int step;
  private final ReleaseState releaseState;
  private final ProductState productState;
  private final ZonedDateTime date;
  private final int buildCount;

  public Version(
      final int major,
      final int minor,
      final ReleaseState releaseState,
      final int step,
      final ProductState productState,
      final ZonedDateTime date,
      final int buildCount) {
    this.major = major;
    this.minor = minor;
    this.releaseState = releaseState;
    this.step = step;
    this.productState = productState;
    this.date = date;
    this.buildCount = buildCount;
  }

  public static IVersion of(final String string) {
    return new VersionParser().parse(string);
  }

  @Override
  public ZonedDateTime getDate() {
    return this.date;
  }

  @Override
  public int getMajor() {
    return this.major;
  }

  @Override
  public int getMinor() {
    return this.minor;
  }

  @Override
  public ReleaseState getReleaseState() {
    return this.releaseState;
  }

  @Override
  public int getStep() {
    return this.step;
  }

  @Override
  public ProductState getProductState() {
    return this.productState;
  }

  @Override
  public int getBuildCount() {
    return this.buildCount;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.buildCount;
    result = prime * result + ((this.date == null) ? 0 : this.date.hashCode());
    result = prime * result + this.major;
    result = prime * result + this.minor;
    result = prime * result + ((this.productState == null) ? 0 : this.productState.hashCode());
    result = prime * result + ((this.releaseState == null) ? 0 : this.releaseState.hashCode());
    result = prime * result + this.step;
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof IVersion)) {
      return false;
    }
    final IVersion other = (IVersion) obj;
    if (this.major != other.getMajor()) {
      return false;
    }
    if (this.minor != other.getMinor()) {
      return false;
    }
    if (this.step != other.getStep()) {
      return false;
    }
    if (this.buildCount != other.getBuildCount()) {
      return false;
    }
    return Objects.equals(this.date, other.getDate())
        && Objects.equals(this.productState, other.getProductState())
        && Objects.equals(this.releaseState, other.getReleaseState());
  }

}
