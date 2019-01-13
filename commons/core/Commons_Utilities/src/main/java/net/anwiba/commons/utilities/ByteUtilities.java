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
package net.anwiba.commons.utilities;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteUtilities {

  public static int getIntegerLittleEdian(final byte[] destination, final int pos) {
    return getInteger(destination, pos, ByteOrder.LITTLE_ENDIAN);
  }

  public static int getIntegerBigEdian(final byte[] destination, final int pos) {
    return getInteger(destination, pos, ByteOrder.BIG_ENDIAN);
  }

  public static int getInteger(final byte[] destination, final int pos, final ByteOrder byteOrder) {
    final ByteBuffer buffer = ByteBuffer.wrap(destination, pos, 4);
    buffer.order(byteOrder);
    return buffer.getInt(pos);
  }

  public static short getShortLittleEdian(final byte[] destination, final int pos) {
    return getShort(destination, pos, ByteOrder.LITTLE_ENDIAN);
  }

  public static short getShort(final byte[] destination, final int pos, final ByteOrder byteOrder) {
    final ByteBuffer buffer = ByteBuffer.wrap(destination, pos, 2);
    buffer.order(byteOrder);
    return buffer.getShort(pos);
  }

  public static int setIntegerLittleEdian(final int value, final byte[] destination, final int pos) {
    return setInteger(value, destination, pos, ByteOrder.LITTLE_ENDIAN);
  }

  public static int setIntegerBigEdian(final int value, final byte[] destination, final int pos) {
    return setInteger(value, destination, pos, ByteOrder.BIG_ENDIAN);
  }

  public static int setInteger(final int value, final byte[] destination, final int pos, final ByteOrder byteOrder) {
    final ByteBuffer buffer = ByteBuffer.wrap(destination);
    buffer.order(byteOrder);
    buffer.putInt(pos, value);
    return pos + 4;
  }

  public static double getDoubleBigEdian(final byte[] destination, final int pos) {
    return getDouble(destination, pos, ByteOrder.BIG_ENDIAN);
  }

  public static double getDoubleLittleEdian(final byte[] destination, final int pos) {
    return getDouble(destination, pos, ByteOrder.LITTLE_ENDIAN);
  }

  public static double getDouble(final byte[] destination, final int pos, final ByteOrder byteOrder) {
    final ByteBuffer buffer = ByteBuffer.wrap(destination, pos, 8);
    buffer.order(byteOrder);
    return buffer.getDouble(pos);
  }

  public static int setDoubleBigEdian(final double value, final byte[] destination, final int pos) {
    return setDouble(value, destination, pos, ByteOrder.BIG_ENDIAN);
  }

  public static int setDoubleLittleEdian(final double value, final byte[] destination, final int pos) {
    return setDouble(value, destination, pos, ByteOrder.LITTLE_ENDIAN);
  }

  public static int setDouble(final double value, final byte[] destination, final int pos, final ByteOrder byteOrder) {
    final ByteBuffer buffer = ByteBuffer.wrap(destination);
    buffer.order(byteOrder);
    buffer.putDouble(pos, value);
    return pos + 8;
  }

  public static float getFloatBigEdian(final byte[] destination, final int pos) {
    return getFloat(destination, pos, ByteOrder.BIG_ENDIAN);
  }

  public static float getFloatLittleEdian(final byte[] destination, final int pos) {
    return getFloat(destination, pos, ByteOrder.LITTLE_ENDIAN);
  }

  public static float getFloat(final byte[] destination, final int pos, final ByteOrder byteOrder) {
    final ByteBuffer buffer = ByteBuffer.wrap(destination, pos, 4);
    buffer.order(byteOrder);
    return buffer.getFloat(pos);
  }

  public static int setFloatBigEdian(final float value, final byte[] destination, final int pos) {
    return setDouble(value, destination, pos, ByteOrder.BIG_ENDIAN);
  }

  public static int setFloatLittleEdian(final float value, final byte[] destination, final int pos) {
    return setDouble(value, destination, pos, ByteOrder.LITTLE_ENDIAN);
  }

  public static int setFloat(final float value, final byte[] destination, final int pos, final ByteOrder byteOrder) {
    final ByteBuffer buffer = ByteBuffer.wrap(destination);
    buffer.order(byteOrder);
    buffer.putFloat(pos, value);
    return pos + 4;
  }
}
