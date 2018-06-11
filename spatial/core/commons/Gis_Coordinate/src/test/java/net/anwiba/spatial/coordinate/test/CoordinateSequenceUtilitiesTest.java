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

import static net.anwiba.spatial.coordinate.junit.CoordinateSequenceAssert.*;

import org.junit.Test;

import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.CoordinateSequence;
import net.anwiba.spatial.coordinate.CoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.CoordinateSequenceUtilities;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinate.ICoordinateSequenceFactory;

public class CoordinateSequenceUtilitiesTest {

  private final ICoordinateSequenceFactory coordinateSequenceFactory = new CoordinateSequenceFactory();

  @Test
  public void testConcat() throws Exception {
    final ICoordinate expectedCoordinate = new Coordinate(5, 5);
    final ICoordinateSequence expectedCoordinateSequence = this.coordinateSequenceFactory.create(expectedCoordinate);
    assertEquals(expectedCoordinateSequence, CoordinateSequenceUtilities.concat(null, expectedCoordinateSequence));
    assertEquals(
        expectedCoordinateSequence,
        CoordinateSequenceUtilities.concat(expectedCoordinateSequence, (CoordinateSequence) null));
    final ICoordinateSequence expectedCoordinateSequence01 =
        this.coordinateSequenceFactory.create(new ICoordinate[] { expectedCoordinate, expectedCoordinate });
    assertEquals(
        expectedCoordinateSequence01,
        CoordinateSequenceUtilities.concat(expectedCoordinateSequence, expectedCoordinateSequence));
    assertEquals(
        expectedCoordinateSequence01,
        CoordinateSequenceUtilities.concat(new ICoordinateSequence[] { expectedCoordinateSequence,
            expectedCoordinateSequence }));
  }

  @Test
  public void testCopy() throws Exception {
    final ICoordinate expectedCoordinate = new Coordinate(5, 5);
    final ICoordinateSequence expectedCoordinateSequence = this.coordinateSequenceFactory.create(expectedCoordinate);
    assertEquals(expectedCoordinateSequence, CoordinateSequenceUtilities.copy(expectedCoordinateSequence));
  }

  @Test
  public void testCopyPart() throws Exception {
    final double[] xs = new double[] { 0, 5, 6, 7, 8, 9, 4 };
    final double[] ys = new double[] { 4, 56, 78, 9, 0, 4, 7 };
    final double[] exs = new double[] { 6, 7, 8, 9 };
    final double[] eys = new double[] { 78, 9, 0, 4 };

    final ICoordinateSequence expectedCoordinateSequence = this.coordinateSequenceFactory.create(exs, eys);
    assertEquals(
        expectedCoordinateSequence,
        CoordinateSequenceUtilities.copy(this.coordinateSequenceFactory.create(xs, ys), 2, 4));
  }

  @Test
  public void testRevers() throws Exception {
    final double[] xs = new double[] { 5, 5, 0, 0, 5 };
    final double[] ys = new double[] { 5, 0, 0, 5, 5 };
    final ICoordinateSequence expectedCoordinateSequence = this.coordinateSequenceFactory.create(xs, ys);
    final ICoordinateSequence coordinateSequence = this.coordinateSequenceFactory.create(ys, xs);
    assertEquals(expectedCoordinateSequence, CoordinateSequenceUtilities.reverse(coordinateSequence));
  }
}
