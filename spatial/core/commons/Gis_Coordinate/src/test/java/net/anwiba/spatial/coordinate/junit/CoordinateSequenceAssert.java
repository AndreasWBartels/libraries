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
package net.anwiba.spatial.coordinate.junit;

import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.ICoordinateSequence;

public class CoordinateSequenceAssert extends CoordinateAssert {

  public static void assertEquals(final ICoordinateSequence expected, final ICoordinateSequence actual) {
    assertEquals("", expected, actual); //$NON-NLS-1$
  }

  public static void assertEquals(
      final String message,
      final ICoordinateSequence expected,
      final ICoordinateSequence actual) {
    if (expected.getNumberOfCoordinates() != actual.getNumberOfCoordinates()) {
      fail(message
          + "number of coordinates differed, expected.numberOfCoordinates=" + expected.getNumberOfCoordinates() //$NON-NLS-1$
          + " actual.numberOfCoordinates=" + actual.getNumberOfCoordinates()); //$NON-NLS-1$
      return;
    }
    if (expected.getDimension() != actual.getDimension()) {
      fail(message + "coordinate dimension differed, expected.dimension=" + expected.getDimension() //$NON-NLS-1$
          + " actual.dimension=" + actual.getDimension()); //$NON-NLS-1$
      return;
    }
    if (expected.isMeasured() != actual.isMeasured()) {
      fail(message + "value of is measured differed, expected.isMeasured=" + expected.isMeasured() //$NON-NLS-1$
          + " actual.isMeasured=" + actual.isMeasured()); //$NON-NLS-1$
      return;
    }
    for (int i = 0; i < expected.getNumberOfCoordinates(); i++) {
      final ICoordinate expectedCoordinate = expected.getCoordinateN(i);
      final ICoordinate actualCoordinate = actual.getCoordinateN(i);
      CoordinateAssert.assertEquals(message + "coordinate " + i + ", ", expectedCoordinate, actualCoordinate); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

}
