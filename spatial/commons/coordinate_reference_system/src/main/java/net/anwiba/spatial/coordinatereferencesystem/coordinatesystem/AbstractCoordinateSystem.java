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

public abstract class AbstractCoordinateSystem implements ICoordinateSystem {

  private static final long serialVersionUID = -1626737173413842942L;

  private final String name;
  private final Unit unit;
  private final Axis[] axises;
  private final List<Extension> extensions;
  private final Authority authority;
  private final Area area;

  AbstractCoordinateSystem(
      final Authority authority,
      final String name,
      final Area area,
      final Unit unit,
      final List<Extension> extensions,
      final Axis... axises) {
    Ensure.ensureArgumentNotNull(name);
    Ensure.ensureArgumentNotNull(unit);
    this.authority = authority;
    this.extensions = extensions;
    this.area = area;
    this.name = name;
    this.unit = unit;
    this.axises = axises;
  }

  @Override
  public Authority getAuthority() {
    return this.authority;
  }

  @Override
  public Iterable<Extension> getExtensions() {
    return this.extensions;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Unit getUnit() {
    return this.unit;
  }

  @Override
  public Area getArea() {
    return this.area;
  }

  @Override
  public ICoordinateSystemType getCoordinateSystemType() {
    return CoordinateSystemType.NONE;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null || !(obj instanceof ICoordinateSystem)) {
      return false;
    }
    final ICoordinateSystem other = (ICoordinateSystem) obj;
    return ObjectUtilities.equals(this.unit, other.getUnit());
  }

  @Override
  public abstract int hashCode();

  @Override
  public Axis[] getAxises() {
    return this.axises;
  }
}
