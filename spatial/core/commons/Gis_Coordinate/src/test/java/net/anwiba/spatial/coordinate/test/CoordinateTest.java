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
 
package net.anwiba.spatial.coordinate.test;

import org.junit.Test;

import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.ICoordinate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CoordinateTest {

  @Test
  public void test() throws Exception {
    final ICoordinate actualCoordinate = new Coordinate(3, 4, 8, true);
    assertEquals(actualCoordinate, actualCoordinate);
    assertEquals(3, actualCoordinate.getXValue(), 0);
    assertEquals(4, actualCoordinate.getYValue(), 0);
    assertEquals(8, actualCoordinate.getMeasuredValue(), 0);
    assertEquals(2, actualCoordinate.getDimension());
    assertTrue(actualCoordinate.isMeasured());
  }

  @Test
  public void testHashCode() {
    final Coordinate expectedCoordinate = new Coordinate(3, 4, 8, true);
    assertEquals(expectedCoordinate.hashCode(), new Coordinate(3, 4, 8, true).hashCode());
    assertEquals(expectedCoordinate.hashCode(), new Coordinate(3, 4, 8, false).hashCode());
    assertFalse(expectedCoordinate.hashCode() == new Coordinate(3, 4, 2, true).hashCode());
  }
}
