/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2019 Andreas Bartels
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
// Copyright (c) 2006 by Andreas W. Bartels
package net.anwiba.spatial.coordinatereferencesystem;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ICoordinateSystem;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ToWgs84;

public class CoordinateReferenceSystem implements ICoordinateReferenceSystem {

  private static final long serialVersionUID = 8081333636994621858L;

  private final Authority authority;
  private final int srid;
  private final ICoordinateSystem coordinateSystem;

  public CoordinateReferenceSystem(final Authority authority,
      final int srid,
      final ICoordinateSystem coordinateSystem) {
    Ensure.ensureArgumentNotNull(authority);
    Ensure.ensureArgumentIsInside(srid, -1, Integer.MAX_VALUE);
    Ensure.ensureArgumentNotNull(coordinateSystem);
    this.authority = authority;
    this.srid = srid;
    this.coordinateSystem = coordinateSystem;
  }

  @Override
  public Authority getAuthority() {
    return this.authority;
  }

  @Override
  public int getSrid() {
    return this.srid;
  }

  @Override
  public CoordinateReferenceSystem adapt(final ToWgs84 towgs84) {
    return new CoordinateReferenceSystem(this.authority, this.srid, this.coordinateSystem.adapt(towgs84));
  }

  @Override
  public ICoordinateReferenceSystem adapt(final int srid) {
    return new CoordinateReferenceSystem(this.authority, srid, this.coordinateSystem);
  }

  @Override
  public ICoordinateSystem getCoordinateSystem() {
    return this.coordinateSystem;
  }

  @Override
  public int hashCode() {
    return ObjectUtilities.hashCode(1, 31, this.coordinateSystem);
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof ICoordinateReferenceSystem other)) {
      return super.equals(obj);
    }
    return ObjectUtilities.equals(this.coordinateSystem, other.getCoordinateSystem());
  }
}
