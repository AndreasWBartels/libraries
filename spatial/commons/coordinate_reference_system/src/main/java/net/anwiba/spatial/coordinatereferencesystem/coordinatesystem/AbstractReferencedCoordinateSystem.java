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
// Copyright (c) 2009 by Andreas W. Bartels
package net.anwiba.spatial.coordinatereferencesystem.coordinatesystem;

import java.util.List;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.spatial.coordinatereferencesystem.Authority;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.unit.Unit;

public abstract class AbstractReferencedCoordinateSystem extends AbstractCoordinateSystem {

  private static final long serialVersionUID = 1815527065894920786L;

  private final Datum datum;
  private final PrimeMeridian primeMeridian;

  AbstractReferencedCoordinateSystem(
      final Authority authority,
      final String name,
      final Datum datum,
      final Area area,
      final PrimeMeridian primeMeridian,
      final Unit unit,
      final List<Extension> extensions,
      final Axis... axises) {
    super(authority, name, area, unit, extensions, axises);
    Ensure.ensureArgumentNotNull(datum);
    Ensure.ensureArgumentNotNull(primeMeridian);
    this.datum = datum;
    this.primeMeridian = primeMeridian;
  }

  public Datum getDatum() {
    return this.datum;
  }

  public PrimeMeridian getPrimeMeridian() {
    return this.primeMeridian;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = ObjectUtilities.hashCode(1, prime, this.datum);
    result = ObjectUtilities.hashCode(1, prime, this.primeMeridian);
    result = ObjectUtilities.hashCode(result, prime, getUnit());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null || !(obj instanceof AbstractReferencedCoordinateSystem)) {
      return false;
    }
    final AbstractReferencedCoordinateSystem other = (AbstractReferencedCoordinateSystem) obj;
    return super.equals(other)
        && ObjectUtilities.equals(this.datum, other.datum)
        && ObjectUtilities.equals(this.primeMeridian, other.primeMeridian);
  }
}
