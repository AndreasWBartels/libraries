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
package net.anwiba.spatial.coordinatereferencesystem.axis;

import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;

public interface IAxisSorterFactory {

  default IAxisSorter createSorter(final ICoordinateReferenceSystem coordinateReferenceSystem) {
    return createSorter(AxisOrder.COORDINATEREFERENCESYSTEM, coordinateReferenceSystem);
  }

  IAxisSorter createSorter(AxisOrder axisOrder, ICoordinateReferenceSystem coordinateReferenceSystem);

  default IAxisSorter createInvertSorter(final ICoordinateReferenceSystem coordinateReferenceSystem) {
    return createInvertSorter(AxisOrder.COORDINATEREFERENCESYSTEM, coordinateReferenceSystem);
  }

  IAxisSorter createInvertSorter(AxisOrder axisOrder, ICoordinateReferenceSystem coordinateReferenceSystem);

}
