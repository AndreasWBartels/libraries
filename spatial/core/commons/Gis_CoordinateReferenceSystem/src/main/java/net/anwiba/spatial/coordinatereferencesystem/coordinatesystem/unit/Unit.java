/*
 * #%L
 * anwiba commons core
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

package net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.unit;

import java.io.Serializable;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.spatial.coordinatereferencesystem.Authority;

public class Unit implements Serializable {

  private static final long serialVersionUID = -8388918067249437514L;

  public static final Unit METER = new Unit("Meter", UnitType.DISTANCE, 1); //$NON-NLS-1$
  public static final Unit DEGREE = new Unit("Degree", UnitType.ANGLE, 0.0174532925199433); //$NON-NLS-1$

  private final String name;
  private final double conversionFactor;
  private final UnitType type;

  private final Authority authority;

  public Unit(final String name, final UnitType type, final double conversionFactor) {
    this(name, type, conversionFactor, null);
  }

  public Unit(final String name, final UnitType type, final double conversionFactor, final Authority authority) {
    this.authority = authority;
    Ensure.ensureArgumentNotNull(name);
    Ensure.ensureArgumentNotNull(type);
    this.type = type;
    this.name = name.trim().toLowerCase();
    this.conversionFactor = conversionFactor;
  }

  public String getName() {
    return this.name;
  }

  public double getConversionFactor() {
    return this.conversionFactor;
  }

  public Authority getAuthority() {
    return this.authority;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null || !(obj instanceof Unit)) {
      return false;
    }
    final Unit other = (Unit) obj;
    if (!ObjectUtilities.equals(this.type, other.getType())) {
      return false;
    }
    if (UnitType.ANGLE.equals(other.getType())) {
      return 0.0000000000000001 > Math.abs(this.conversionFactor - other.getConversionFactor());
    }
    return this.conversionFactor == other.getConversionFactor();
  }

  @Override
  public int hashCode() {
    final long bits = Double.doubleToLongBits(this.conversionFactor);
    return ((int) (bits ^ (bits >>> 32))) * this.type.hashCode();
  }

  public UnitType getType() {
    return this.type;
  }
}
