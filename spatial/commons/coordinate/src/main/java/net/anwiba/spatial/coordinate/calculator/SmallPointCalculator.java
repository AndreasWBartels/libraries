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

import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.ICoordinate;

public class SmallPointCalculator {

  public static final double TOLERANCE = 0.005;
  private final ICoordinateDistanceCalculator coordinateDistanceCalculator = new DefaultCoordinateDistanceCalculator();
  private final double x1;
  private final double y1;
  private final double x0;
  private final double y0;
  private final double a;
  private final double o;

  public SmallPointCalculator(final ICoordinate c0, final ICoordinate c1) {
    this(TOLERANCE, c0, c1);
  }

  public SmallPointCalculator(final double tolerance, final ICoordinate c0, final ICoordinate c1) {
    this.x0 = c0.getXValue();
    this.y0 = c0.getYValue();
    this.x1 = c1.getXValue();
    this.y1 = c1.getYValue();
    final double d = this.coordinateDistanceCalculator.calculateDistance(this.x0, this.y0, this.x1, this.y1);
    if (Math.abs(d) <= tolerance) {
      throw new IllegalArgumentException("base points are equal"); //$NON-NLS-1$
    }
    this.a = (this.x1 - this.x0) / d;
    this.o = (this.y1 - this.y0) / d;
  }

  public ICoordinate calculate(final double s, final double r) {
    final double xs = this.x0 + this.a * s + this.o * r;
    final double ys = this.y0 + this.o * s - this.a * r;
    return new Coordinate(xs, ys);
  }
}
