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
package net.anwiba.spatial.coordinatereferencesystem.coordinatesystem;

import java.util.List;

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.spatial.coordinatereferencesystem.Authority;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.unit.Unit;

public class VerticalCoordinateSystem extends AbstractCoordinateSystem {

  private static final long serialVersionUID = 1L;
  private VerticalDatum datum;

  public VerticalCoordinateSystem(
      final Authority authority,
      final String name,
      final VerticalDatum datum,
      final Area area,
      final Unit unit,
      final List<Extension> extensions,
      final Axis[] axises) {
    super(authority, name, area, unit, extensions, axises);
    this.datum = datum;
  }

  public VerticalDatum getDatum() {
    return datum;
  }

  @Override
  public int hashCode() {
    return ObjectUtilities.hashCode(1, 31, getUnit());
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof VerticalCoordinateSystem)) {
      return false;
    }
    final VerticalCoordinateSystem other = (VerticalCoordinateSystem) obj;
    return super.equals(other);
  }

  @Override
  public ICoordinateSystem adapt(ToWgs84 towgs84) {
    return this;
  }

  @Override
  public ICoordinateSystemType getCoordinateSystemType() {
    return CoordinateSystemType.VERTICAL;
  }
}
