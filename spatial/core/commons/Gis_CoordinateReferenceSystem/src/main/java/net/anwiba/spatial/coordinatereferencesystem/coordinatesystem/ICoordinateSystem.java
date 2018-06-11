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

import net.anwiba.spatial.coordinatereferencesystem.Authority;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.unit.Unit;

public interface ICoordinateSystem extends Serializable {

  public String getName();

  public Unit getUnit();

  public ICoordinateSystemType getCoordinateSystemType();

  public Axis[] getAxises();

  Authority getAuthority();

  Iterable<Extension> getExtensions();

  Area getArea();

}