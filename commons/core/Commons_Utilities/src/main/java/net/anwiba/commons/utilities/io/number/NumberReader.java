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
package net.anwiba.commons.utilities.io.number;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

import net.anwiba.commons.lang.counter.Counter;
import net.anwiba.commons.utilities.ByteUtilities;

public class NumberReader {

  private final byte[] array = new byte[8];
  private final Counter counter = new Counter(0);
  private final InputStream inputStream;
  private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

  public NumberReader(final InputStream inputStream) {
    this.inputStream = new BufferedInputStream(inputStream);
  }

  public void setByteOrder(final ByteOrder byteOrder) {
    this.byteOrder = byteOrder;
  }

  public byte readByte() throws IOException {
    read(1);
    return this.array[0];
  }

  public int readInteger() throws IOException {
    read(4);
    return ByteUtilities.getInteger(this.array, 0, this.byteOrder);
  }

  public double readDouble() throws IOException {
    read(8);
    return ByteUtilities.getDouble(this.array, 0, this.byteOrder);
  }

  public double readFloat() throws IOException {
    read(4);
    return ByteUtilities.getFloat(this.array, 0, this.byteOrder);
  }

  private void read(final int length) throws IOException {
    final int readed = this.inputStream.read(this.array, 0, length);
    if (readed < length) {
      throw new IOException("exepected " + length + " byte, but got " + readed); //$NON-NLS-1$//$NON-NLS-2$
    }
    this.counter.add(readed);
  }
}