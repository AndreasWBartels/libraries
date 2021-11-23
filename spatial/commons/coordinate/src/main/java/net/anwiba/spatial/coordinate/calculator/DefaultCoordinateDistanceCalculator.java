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
package net.anwiba.spatial.coordinate.calculator;

public class DefaultCoordinateDistanceCalculator implements ICoordinateDistanceCalculator {

  public DefaultCoordinateDistanceCalculator() {
    super();
  }

  @Override
  public double calculate(final double x0, final double y0, final double x1, final double y1) {
    final double dx = Math.abs(x1 - x0);
    final double dy = Math.abs(y1 - y0);
    return Math.sqrt(dx * dx + dy * dy);
  }

}
