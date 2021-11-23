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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

import net.anwiba.commons.utilities.ByteUtilities;

public class NumberWriter {

  private final byte[] array = new byte[8];
  private final OutputStream outputStream;
  private final ByteOrder byteOrder;

  public NumberWriter(final OutputStream outputStream, final ByteOrder byteOrder) {
    this.outputStream = outputStream;
    this.byteOrder = byteOrder;
  }

  public void writeByte(final byte value) throws IOException {
    this.outputStream.write(value);
  }

  public void writeInteger(final int value) throws IOException {
    ByteUtilities.setInteger(value, this.array, 0, this.byteOrder);
    this.outputStream.write(this.array, 0, 4);
  }

  public void writeDouble(final double value) throws IOException {
    ByteUtilities.setDouble(value, this.array, 0, this.byteOrder);
    this.outputStream.write(this.array, 0, 8);
  }

  public void writeFloat(final float value) throws IOException {
    ByteUtilities.setFloat(value, this.array, 0, this.byteOrder);
    this.outputStream.write(this.array, 0, 4);
  }
}
