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

import net.anwiba.commons.utilities.ByteUtilities;

import org.junit.Test;

import static org.junit.Assert.*;

public class ByteUtilitiesTest {

  @Test
  public void testDoubleBigEdian() throws Exception {
    final byte[] array = new byte[16];
    final int pos = 4;
    assertEquals(pos + 8, ByteUtilities.setDoubleBigEdian(0, array, pos));
    assertEquals(0, ByteUtilities.getDoubleBigEdian(array, pos), 0);

    ByteUtilities.setDoubleBigEdian(34, array, pos);
    assertEquals(34, ByteUtilities.getDoubleBigEdian(array, pos), 0);

    ByteUtilities.setDoubleBigEdian(34.11, array, pos);
    assertEquals(34.11, ByteUtilities.getDoubleBigEdian(array, pos), 0);
  }

  @Test
  public void testDoubleLittleEdian() throws Exception {
    final byte[] array = new byte[16];
    final int pos = 4;
    ByteUtilities.setDoubleLittleEdian(0, array, pos);
    assertEquals(0, ByteUtilities.getDoubleLittleEdian(array, pos), 0);

    ByteUtilities.setDoubleLittleEdian(34, array, pos);
    assertEquals(34, ByteUtilities.getDoubleLittleEdian(array, pos), 0);

    ByteUtilities.setDoubleLittleEdian(34.11, array, pos);
    assertEquals(34.11, ByteUtilities.getDoubleLittleEdian(array, pos), 0);
  }

  @Test
  public void testIntegerBigEdian() throws Exception {
    final byte[] array = new byte[8];
    final int pos = 2;
    assertEquals(pos + 4, ByteUtilities.setIntegerBigEdian(0, array, pos));
    assertEquals(0, ByteUtilities.getIntegerBigEdian(array, pos));

    ByteUtilities.setIntegerBigEdian(34, array, pos);
    assertEquals(34, ByteUtilities.getIntegerBigEdian(array, pos));
  }

  @Test
  public void getIntegerLittleEdian() throws Exception {
    final byte[] array = new byte[8];
    final int pos = 2;
    ByteUtilities.setIntegerLittleEdian(0, array, pos);
    assertEquals(0, ByteUtilities.getIntegerLittleEdian(array, pos));

    ByteUtilities.setIntegerLittleEdian(34, array, pos);
    assertEquals(34, ByteUtilities.getIntegerLittleEdian(array, pos));
  }
}
