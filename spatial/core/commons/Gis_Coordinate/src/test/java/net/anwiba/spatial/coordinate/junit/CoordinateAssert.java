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

import org.junit.Assert;

import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinate.IEnvelope;

public class CoordinateAssert extends Assert {

  public static void assertEquals(final IEnvelope expected, final IEnvelope actual) {
    assertEquals("", expected.getMinimum(), actual.getMinimum()); //$NON-NLS-1$
    assertEquals("", expected.getMaximum(), actual.getMaximum()); //$NON-NLS-1$
  }

  public static void assertEquals(final ICoordinate expected, final ICoordinate actual) {
    assertEquals("", expected, actual); //$NON-NLS-1$
  }

  public static void assertEquals(final ICoordinate[] expected, final ICoordinate[] actual) {
    if (expected.length != actual.length) {
      fail("coordinate array length differed, expected.length=" //$NON-NLS-1$
          + expected.length
          + " actual.length=" //$NON-NLS-1$
          + actual.length);
      return;
    }
    for (int i = 0; i < expected.length; i++) {
      assertEquals("array coordinate" + i + ", ", expected[i], actual[i]); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  public static void assertEquals(final String message, final ICoordinate expected, final ICoordinate actual) {
    if (expected.getDimension() != actual.getDimension()) {
      fail(message
          + "coordinate dimension differed, expected.dimension=" //$NON-NLS-1$
          + expected.getDimension()
          + " actual.dimension=" //$NON-NLS-1$
          + actual.getDimension());
      return;
    }
    if (expected.isMeasured() != actual.isMeasured()) {
      fail(message
          + "value of is measured differed, expected.isMeasured=" //$NON-NLS-1$
          + expected.isMeasured()
          + " actual.isMeasured=" //$NON-NLS-1$
          + actual.isMeasured());
      return;
    }
    for (int i = 0; i < expected.getDimension(); i++) {
      if (expected.getValue(i) != actual.getValue(i)) {
        fail(message
            + "value of dimension " //$NON-NLS-1$
            + i
            + " differed, expected.value=" //$NON-NLS-1$
            + expected.getValue(i)
            + " actual.value=" //$NON-NLS-1$
            + actual.getValue(i));
      }
    }
    if (expected.isMeasured() && expected.getMeasuredValue() != actual.getMeasuredValue()) {
      fail(message
          + "measured value differed, expected.value=" //$NON-NLS-1$
          + expected.getMeasuredValue()
          + " actual.value=" //$NON-NLS-1$
          + actual.getMeasuredValue());
    }
  }

}
