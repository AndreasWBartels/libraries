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

import java.util.ArrayList;

import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.unit.Unit;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.unit.UnitType;

public interface ICoordinateSystemConstants {

  @SuppressWarnings("nls")
  public static final ICoordinateSystem NONE = new AbstractCoordinateSystem(
      null,
      "NONE",
      null,
      new Unit("NONE", UnitType.UNKNOWN, 1),
      new ArrayList<Extension>()) {

    private static final long serialVersionUID = 1L;

    @Override
    public int hashCode() {
      return 0;
    }
  };
}
