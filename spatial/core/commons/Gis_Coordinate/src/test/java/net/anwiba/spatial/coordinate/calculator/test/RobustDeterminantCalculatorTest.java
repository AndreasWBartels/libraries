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

import net.anwiba.spatial.coordinate.calculator.RobustDeterminantCalculator;

import static org.junit.Assert.*;

public class RobustDeterminantCalculatorTest {
  @Test
  public void testPlus() throws Exception {
    assertEquals(0, RobustDeterminantCalculator.signOfDet(0, 0, 0, 0));
    assertEquals(0, RobustDeterminantCalculator.signOfDet(0, 0, 0, 1));
    assertEquals(0, RobustDeterminantCalculator.signOfDet(0, 0, 1, 0));
    assertEquals(0, RobustDeterminantCalculator.signOfDet(0, 0, 1, 1));
    assertEquals(0, RobustDeterminantCalculator.signOfDet(0, 1, 0, 0));
    assertEquals(0, RobustDeterminantCalculator.signOfDet(0, 1, 0, 1));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(0, 1, 1, 0));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(0, 1, 1, 1));
    assertEquals(0, RobustDeterminantCalculator.signOfDet(1, 0, 0, 0));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(1, 0, 0, 1));
    assertEquals(0, RobustDeterminantCalculator.signOfDet(1, 0, 1, 0));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(1, 0, 1, 1));
    assertEquals(0, RobustDeterminantCalculator.signOfDet(1, 1, 0, 0));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(1, 1, 0, 1));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(1, 1, 1, 0));
  }

  @Test
  public void testMinus() throws Exception {
    assertEquals(0, RobustDeterminantCalculator.signOfDet(0, 0, 0, -1));
    assertEquals(0, RobustDeterminantCalculator.signOfDet(0, 0, -1, 0));
    assertEquals(0, RobustDeterminantCalculator.signOfDet(0, -1, 0, 0));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(0, -1, -1, 0));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(0, -1, -1, -1));
    assertEquals(0, RobustDeterminantCalculator.signOfDet(-1, 0, 0, 0));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(-1, 0, 0, -1));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(-1, 0, -1, -1));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(-1, -1, 0, -1));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(-1, -1, -1, 0));
  }

  @Test
  public void testNoZero() throws Exception {
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(-1, -1, -1, 1));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(-1, -1, 1, -1));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(-1, 1, -1, -1));
    assertEquals(0, RobustDeterminantCalculator.signOfDet(-1, 1, 1, -1));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(-1, 1, 1, 1));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(1, -1, -1, -1));
    assertEquals(0, RobustDeterminantCalculator.signOfDet(1, -1, -1, 1));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(1, -1, 1, 1));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(1, 1, -1, 1));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(1, 1, 1, -1));
  }

  @Test
  public void testNoZeroPlus() throws Exception {
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(1, 2, 3, 4));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(1, 2, 4, 3));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(1, 4, 2, 3));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(4, 1, 2, 3));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(4, 1, 3, 2));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(4, 3, 1, 2));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(3, 4, 1, 2));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(3, 4, 2, 1));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(3, 2, 4, 1));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(2, 3, 4, 1));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(2, 3, 1, 4));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(2, 1, 3, 4));

    assertEquals(-1, RobustDeterminantCalculator.signOfDet(2, 2, 2, 1));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(2, 2, 1, 2));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(2, 1, 2, 2));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(2, 1, 1, 2));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(2, 1, 1, 1));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(1, 2, 2, 2));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(1, 2, 2, 1));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(1, 2, 1, 1));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(1, 1, 2, 1));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(1, 1, 1, 2));
  }

  @Test
  public void testNoZeroMinus() throws Exception {
    assertEquals(1, RobustDeterminantCalculator.signOfDet(-1, -1, -1, -2));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(-1, -1, -2, -1));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(-1, -2, -1, -1));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(-1, -2, -2, -1));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(-1, -2, -2, -2));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(-2, -1, -1, -1));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(-2, -1, -1, -2));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(-2, -1, -2, -2));
    assertEquals(1, RobustDeterminantCalculator.signOfDet(-2, -2, -1, -2));
    assertEquals(-1, RobustDeterminantCalculator.signOfDet(-2, -2, -2, -1));
  }
}