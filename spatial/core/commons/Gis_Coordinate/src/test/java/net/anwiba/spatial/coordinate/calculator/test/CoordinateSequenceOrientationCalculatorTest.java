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
 
package net.anwiba.spatial.coordinate.calculator.test;

import org.junit.Test;

import net.anwiba.spatial.coordinate.CoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.ICoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.calculator.CoordinateSequenceOrientationCalculator;

import static org.junit.Assert.*;

public class CoordinateSequenceOrientationCalculatorTest {

  private final ICoordinateSequenceFactory coordinateSequenceFactory = new CoordinateSequenceFactory();

  @Test
  public void orientationPositive() throws Exception {
    final double[] xs = new double[] { 5, 0, 0, 5, 5 };
    final double[] ys = new double[] { 5, 5, 0, 0, 5 };
    assertTrue(CoordinateSequenceOrientationCalculator.isOrientationPositive(this.coordinateSequenceFactory.create(
        xs,
        ys)));
  }

  @Test
  public void orientationNegative() throws Exception {
    final double[] xs = new double[] { 5, 5, 0, 0, 5 };
    final double[] ys = new double[] { 5, 0, 0, 5, 5 };
    assertFalse(CoordinateSequenceOrientationCalculator.isOrientationPositive(this.coordinateSequenceFactory.create(
        xs,
        ys)));
  }

}
