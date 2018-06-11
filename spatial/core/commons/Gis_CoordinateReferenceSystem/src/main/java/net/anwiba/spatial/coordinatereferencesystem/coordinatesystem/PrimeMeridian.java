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
 
package net.anwiba.spatial.coordinatereferencesystem.coordinatesystem;

import java.io.Serializable;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.spatial.coordinatereferencesystem.Authority;

public class PrimeMeridian implements Serializable {

  private static final long serialVersionUID = 1689378822975425529L;

  public static PrimeMeridian GREENWICH = new PrimeMeridian("Greenwich", 0); //$NON-NLS-1$

  private final String name;
  private final double longitude;

  private final Authority authority;

  public PrimeMeridian(final String name, final double longitude) {
    this(name, longitude, null);
  }

  public PrimeMeridian(final String name, final double longitude, final Authority authority) {
    this.authority = authority;
    Ensure.ensureArgumentNotNull(name);
    this.name = name.trim().toLowerCase();
    this.longitude = longitude;
  }

  public String getName() {
    return this.name;
  }

  public double getLongitude() {
    return this.longitude;
  }

  public Authority getAuthority() {
    return this.authority;
  }

  @Override
  public int hashCode() {
    return ObjectUtilities.hashCode(1, 31, this.longitude);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null || !(obj instanceof PrimeMeridian)) {
      return false;
    }
    final PrimeMeridian other = (PrimeMeridian) obj;
    return this.longitude == other.longitude;
  }
}
