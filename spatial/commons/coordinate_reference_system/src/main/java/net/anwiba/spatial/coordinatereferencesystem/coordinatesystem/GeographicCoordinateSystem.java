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

import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.utilities.collection.IterableUtilities;
import net.anwiba.spatial.coordinatereferencesystem.Authority;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.unit.Unit;

public class GeographicCoordinateSystem extends AbstractReferencedCoordinateSystem {

  private static final long serialVersionUID = 5933195127383688673L;

  public GeographicCoordinateSystem(
      final Authority authority,
      final String wgs84,
      final Datum datum,
      final PrimeMeridian gREENWICH,
      final Unit degree,
      final Axis... axises) {
    this(authority, wgs84, datum, gREENWICH, degree, new ArrayList<>(), axises);
  }

  public GeographicCoordinateSystem(
      final Authority authority,
      final String name,
      final Datum datum,
      final PrimeMeridian primeMeridian,
      final Unit unit,
      final List<Extension> extensions,
      final Axis... axises) {
    this(authority, name, datum, null, primeMeridian, unit, extensions, axises);
  }

  public GeographicCoordinateSystem(
      final Authority authority,
      final String name,
      final Datum datum,
      final Area area,
      final PrimeMeridian primeMeridian,
      final Unit unit,
      final Axis... axises) {
    super(authority, name, datum, area, primeMeridian, unit, new ArrayList<>(), axises);
  }

  public GeographicCoordinateSystem(
      final Authority authority,
      final String name,
      final Datum datum,
      final Area area,
      final PrimeMeridian primeMeridian,
      final Unit unit,
      final List<Extension> extensions,
      final Axis... axises) {
    super(authority, name, datum, area, primeMeridian, unit, extensions, axises);
  }

  @Override
  public ICoordinateSystemType getCoordinateSystemType() {
    return CoordinateSystemType.GEOGRAPHIC;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof GeographicCoordinateSystem)) {
      return false;
    }
    return super.equals(obj);
  }

  @Override
  public GeographicCoordinateSystem adapt(ToWgs84 towgs84) {
    return new GeographicCoordinateSystem(
        getAuthority(),
        getName(),
        getDatum().adapt(towgs84),
        getArea(),
        getPrimeMeridian(),
        getUnit(),
        IterableUtilities.asList(getExtensions()),
        getAxises());
  }
}
