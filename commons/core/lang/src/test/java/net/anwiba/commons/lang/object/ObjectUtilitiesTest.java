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
package net.anwiba.commons.lang.object;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ObjectUtilitiesTest {

  @Test
  public void testIntArrayEquals() throws Exception {
    final int[] values = { 12, 24, 98, 54 };
    final int[] compareValues = { 12, 23, 98, 54 };
    assertTrue(ObjectUtilities.equals(values, values));
    assertFalse(ObjectUtilities.equals(values, compareValues));
  }

  @Test
  public void testDoubleArrayEquals() throws Exception {
    final double[] values = { 12, 24, 98, 54 };
    final double[] compareValues = { 12, 23, 98, 54 };
    assertTrue(ObjectUtilities.equals(values, values));
    assertFalse(ObjectUtilities.equals(values, compareValues));
  }

  @Test
  public void testByteArrayEquals() throws Exception {
    final byte[] values = { 12, 24, 98, 54 };
    final byte[] compareValues = { 12, 23, 98, 54 };
    assertTrue(ObjectUtilities.equals(values, values));
    assertFalse(ObjectUtilities.equals(values, compareValues));
  }

  @Test
  public void testObjectArrayEquals() throws Exception {
    final Object[] values = { 12, 24, 98, 54 };
    final Object[] compareValues = { 12, 23, 98, 54 };
    assertTrue(ObjectUtilities.equals(values, values));
    assertFalse(ObjectUtilities.equals(values, compareValues));
  }

  public static class TestClass {
    @SuppressWarnings("unused")
    private final String name;
    @SuppressWarnings("unused")
    private final int value;

    public TestClass(final String name, final int value) {
      this.name = name;
      this.value = value;
    }
  }

  @Test
  public void testToString() {
    assertThat(ObjectUtilities.toString(new TestClass("harrie", 10)), equalTo("[harrie, 10]")); //$NON-NLS-1$ //$NON-NLS-2$
    assertThat(ObjectUtilities.toString(new TestClass("harrie", 10)), equalTo("[harrie, 10]")); //$NON-NLS-1$ //$NON-NLS-2$
    final Object object = new Object();
    assertThat(ObjectUtilities.toString(object), equalTo(Integer.toHexString(object.hashCode())));
  }
}