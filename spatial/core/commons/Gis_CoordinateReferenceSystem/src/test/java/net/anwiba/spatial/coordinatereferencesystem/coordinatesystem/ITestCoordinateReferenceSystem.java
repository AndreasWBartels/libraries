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

import net.anwiba.spatial.coordinatereferencesystem.Authority;
import net.anwiba.spatial.coordinatereferencesystem.CoordinateReferenceSystem;
import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;

public interface ITestCoordinateReferenceSystem {

  public static final ICoordinateReferenceSystem DHDN_GK_3 = new CoordinateReferenceSystem(
      new Authority("EPSG", 31467), 31467, ITestCoordinateSystems.DHDN_GK_3); //$NON-NLS-1$
  public static final ICoordinateReferenceSystem GG_WGS_84 = new CoordinateReferenceSystem(
      new Authority("EPSG", 4326), 4326, ITestCoordinateSystems.GG_WGS_84); //$NON-NLS-1$

}
