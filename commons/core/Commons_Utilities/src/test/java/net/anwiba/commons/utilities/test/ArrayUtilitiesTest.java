/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
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
package net.anwiba.commons.utilities.test;

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.utilities.ArrayUtilities;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayUtilitiesTest {

  @Test
  public void testGetMin() throws Exception {
    final double[] values = { 12, 24, 98, 54 };
    assertEquals(12, ArrayUtilities.getMin(values), 0);
  }

  @Test
  public void testGetMax() throws Exception {
    final double[] values = { 12, 24, 98, 54 };
    assertEquals(98, ArrayUtilities.getMax(values), 0);
  }

  @Test
  public void testByteArrayConcat() throws Exception {
    final byte[] values = { 12, 24, 98, 54 };
    final byte[] compareValues = { 98, 54 };
    assertTrue(ObjectUtilities.equals(
        new byte[] { 12, 24, 98, 54, 98, 54 },
        ArrayUtilities.concat(values, compareValues)));
  }

  @Test
  public void testIntArrayConcat() throws Exception {
    final int[] values = { 12, 24, 98, 54 };
    final int[] compareValues = { 98, 54 };
    assertTrue(ObjectUtilities.equals(
        new int[] { 12, 24, 98, 54, 98, 54 },
        ArrayUtilities.concat(values, compareValues)));
  }

  @Test
  public void testIntArrayContains() throws Exception {
    final int[] values = { 12, 24, 98, 54 };
    assertTrue(ArrayUtilities.contains(values, 98));
    assertFalse(ArrayUtilities.contains(values, 198));
  }

  @Test
  public void testDoubleArrayConcat() throws Exception {
    final double[] values = { 12, 24, 98, 54 };
    final double[] compareValues = { 98, 54 };
    assertTrue(ObjectUtilities.equals(
        new double[] { 12, 24, 98, 54, 98, 54 },
        ArrayUtilities.concat(values, compareValues)));
  }

  @Test
  public void testGenericArrayConcat() throws Exception {
    final String[] values = { "12", "24", "98", "54" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    final String[] compareValues = { "98", "54" }; //$NON-NLS-1$//$NON-NLS-2$
    assertTrue(ObjectUtilities.equals(new String[] { "12", "24", "98", "54", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        "54" }, ArrayUtilities.concat(String.class, values, "54"))); //$NON-NLS-1$//$NON-NLS-2$
    assertTrue(ObjectUtilities.equals(new String[] { "54", "12", "24", "98", "54" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    }, ArrayUtilities.concat(String.class, "54", values))); //$NON-NLS-1$
    assertTrue(ObjectUtilities.equals(new String[] { "12", "24", "98", "54", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        "98", "54" }, ArrayUtilities.concat(String.class, values, compareValues))); //$NON-NLS-1$//$NON-NLS-2$
  }

  @Test
  public void testReverse() throws Exception {
    double[] values = new double[] { 1, 2, 3, 4 };
    assertTrue(ObjectUtilities.equals(new double[] { 4, 3, 2, 1 }, ArrayUtilities.reverse(values)));
    values = new double[] { 1, 2, 3, 4, 5 };
    assertTrue(ObjectUtilities.equals(new double[] { 5, 4, 3, 2, 1 }, ArrayUtilities.reverse(values)));
  }

  @Test
  public void testConvert() throws Exception {
    final Long[] values = new Long[] { Long.valueOf(1) };
    final Integer[] results = ArrayUtilities.convert(new IConverter<Long, Integer, RuntimeException>() {

      @Override
      public Integer convert(final Long input) {
        return Integer.valueOf(input.intValue());
      }
    }, values, Integer.class);
    assertEquals(1, results.length);
    assertEquals(1, results[0].intValue());
  }

  @Test
  public void testPrimitives() {
    final Integer[] values = new Integer[] { Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2) };
    assertArrayEquals(new int[] { 0, 1, 2 }, ArrayUtilities.primitives(values));
    final Double[] doubles = new Double[] { Double.valueOf(0), Double.valueOf(1), Double.valueOf(2) };
    assertArrayEquals(new double[] { 0, 1, 2 }, ArrayUtilities.primitives(doubles), 0);
  }
}
