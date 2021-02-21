/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2021 Andreas Bartels
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
package net.anwiba.spatial.coordinatereferencesystem;

import java.util.Objects;

import net.anwiba.commons.lang.functional.IEqualComperator;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.GeocentricCoordinateSystem;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.GeographicCoordinateSystem;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ICoordinateSystem;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.ProjectedCoordinateSystem;

public final class CoordinateReferenceSystemEqualComperator implements
    IEqualComperator<ICoordinateReferenceSystem> {
  @Override
  public boolean equals(final ICoordinateReferenceSystem value, final ICoordinateReferenceSystem other) {
    return isEquals(value, other);
  }

  public static boolean isEquals(final ICoordinateReferenceSystem value, final ICoordinateReferenceSystem other) {
    return Objects.equals(value, other)
        && Objects.equals(getAuthority(value), getAuthority(other))
        && Objects.equals(getToWgs84(value), getToWgs84(other));
  }

  private static Object getAuthority(final ICoordinateReferenceSystem value) {
    return value == null ? null : value.getAuthority();
  }

  private static Object getToWgs84(final ICoordinateReferenceSystem value) {
    if (value == null) {
      return null;
    }
    ICoordinateSystem coordinateSystem = value.getCoordinateSystem();
    return getToWgs84(coordinateSystem);
  }

  private static Object getToWgs84(final ICoordinateSystem coordinateSystem) {
    if (coordinateSystem instanceof GeographicCoordinateSystem) {
      GeographicCoordinateSystem geographicCoordinateSystem = (GeographicCoordinateSystem) coordinateSystem;
      return geographicCoordinateSystem.getDatum().getToWgs84();
    }
    if (coordinateSystem instanceof ProjectedCoordinateSystem) {
      ProjectedCoordinateSystem projectedCoordinateSystem = (ProjectedCoordinateSystem) coordinateSystem;
      return getToWgs84(projectedCoordinateSystem.getGeographicCoordinateSystem());
    }
    if (coordinateSystem instanceof GeocentricCoordinateSystem) {
      GeocentricCoordinateSystem geocentricCoordinateSystem = (GeocentricCoordinateSystem) coordinateSystem;
      return geocentricCoordinateSystem.getDatum().getToWgs84();
    }
    return null;
  }
}
