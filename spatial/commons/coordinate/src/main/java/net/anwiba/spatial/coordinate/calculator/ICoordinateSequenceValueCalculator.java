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
package net.anwiba.spatial.coordinate.calculator;

import java.util.List;

import net.anwiba.spatial.coordinate.CoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;

public interface ICoordinateSequenceValueCalculator {

  double calculate(ICoordinateSequence coordinateSequence);

  default double calculate(final List<ICoordinate> coordinates) {
    return calculate(new CoordinateSequenceFactory().create(coordinates));
  }
}
