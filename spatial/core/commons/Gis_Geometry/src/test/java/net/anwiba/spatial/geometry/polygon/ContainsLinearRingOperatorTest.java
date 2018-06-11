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
 

package net.anwiba.spatial.geometry.polygon;

import static org.junit.Assert.*;

import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.geometry.ILinearRing;
import net.anwiba.spatial.geometry.TestGeometryFactory;
import net.anwiba.spatial.geometry.polygon.ContainsLinearRingOperator;

import org.junit.Test;

public class ContainsLinearRingOperatorTest {

  @Test
  public void testCoordinate() throws Exception {
    final ILinearRing ring = TestGeometryFactory.createLinearRing();
    final ContainsLinearRingOperator operator = new ContainsLinearRingOperator(ring);
    assertTrue(operator.contains(new Coordinate(100, 60), false));
    assertFalse(operator.contains(new Coordinate(100, 40), false));
  }

  @Test
  public void testCoordinateMirrored() throws Exception {
    final ILinearRing ring = TestGeometryFactory.createMirroredLinearRing();
    final ContainsLinearRingOperator operator = new ContainsLinearRingOperator(ring);
    assertTrue(operator.contains(new Coordinate(60, 100), false));
    assertFalse(operator.contains(new Coordinate(40, 100), false));
  }

  @Test
  public void testRingContaints() throws Exception {
    final ILinearRing ring = TestGeometryFactory.createShellLinearRing();
    final ContainsLinearRingOperator operator = new ContainsLinearRingOperator(ring);
    assertTrue(operator.contains(TestGeometryFactory.createHoleLinearRing(), false));
  }

  @Test
  public void testRingContaintsNot() throws Exception {
    final ILinearRing ring = TestGeometryFactory.createHoleLinearRing();
    final ContainsLinearRingOperator operator = new ContainsLinearRingOperator(ring);
    assertFalse(operator.contains(TestGeometryFactory.createShellLinearRing(), false));
  }
}
