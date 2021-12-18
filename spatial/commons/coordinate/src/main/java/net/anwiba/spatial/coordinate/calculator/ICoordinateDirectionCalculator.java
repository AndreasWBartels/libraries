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

import net.anwiba.commons.utilities.math.Angle;
import net.anwiba.commons.utilities.math.DirectionAngle;
import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.ICoordinate;

public interface ICoordinateDirectionCalculator {

  default DirectionAngle calculate(final double x0, final double y0, final double x1, final double y1) {
    return calculate(Coordinate.of(x0, y0), Coordinate.of(x1, y1));
  }

  DirectionAngle calculate(final ICoordinate prior, final ICoordinate next);

  default Angle
      calculate(final double x0, final double y0, final double x1, final double y1, final double x2, final double y2) {
    return calculate(Coordinate.of(x0, y0), Coordinate.of(x1, y1), Coordinate.of(x2, y2));
  }

  Angle calculate(final ICoordinate prior, ICoordinate center, final ICoordinate next);

}