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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.utilities.ByteUtilities;

public class ByteUtilitiesTest {

  @Test
  public void testDoubleBigEdian() throws Exception {
    final byte[] array = new byte[16];
    final int pos = 4;
    assertEquals(pos + 8, ByteUtilities.setDoubleBigEndian(0, array, pos));
    assertEquals(0, ByteUtilities.getDoubleBigEndian(array, pos), 0);

    ByteUtilities.setDoubleBigEndian(34, array, pos);
    assertEquals(34, ByteUtilities.getDoubleBigEndian(array, pos), 0);

    ByteUtilities.setDoubleBigEndian(34.11, array, pos);
    assertEquals(34.11, ByteUtilities.getDoubleBigEndian(array, pos), 0);
  }

  @Test
  public void testDoubleLittleEdian() throws Exception {
    final byte[] array = new byte[16];
    final int pos = 4;
    ByteUtilities.setDoubleLittleEndian(0, array, pos);
    assertEquals(0, ByteUtilities.getDoubleLittleEndian(array, pos), 0);

    ByteUtilities.setDoubleLittleEndian(34, array, pos);
    assertEquals(34, ByteUtilities.getDoubleLittleEndian(array, pos), 0);

    ByteUtilities.setDoubleLittleEndian(34.11, array, pos);
    assertEquals(34.11, ByteUtilities.getDoubleLittleEndian(array, pos), 0);
  }

  @Test
  public void testIntegerBigEdian() throws Exception {
    final byte[] array = new byte[8];
    final int pos = 2;
    assertEquals(pos + 4, ByteUtilities.setIntegerBigEndian(0, array, pos));
    assertEquals(0, ByteUtilities.getIntegerBigEndian(array, pos));

    ByteUtilities.setIntegerBigEndian(34, array, pos);
    assertEquals(34, ByteUtilities.getIntegerBigEndian(array, pos));
  }

  @Test
  public void getIntegerLittleEdian() throws Exception {
    final byte[] array = new byte[8];
    final int pos = 2;
    ByteUtilities.setIntegerLittleEndian(0, array, pos);
    assertEquals(0, ByteUtilities.getIntegerLittleEndian(array, pos));

    ByteUtilities.setIntegerLittleEndian(34, array, pos);
    assertEquals(34, ByteUtilities.getIntegerLittleEndian(array, pos));
  }
}
