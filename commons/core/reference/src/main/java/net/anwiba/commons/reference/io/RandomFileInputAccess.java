/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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
package net.anwiba.commons.reference.io;

import java.io.IOException;
import java.io.RandomAccessFile;

public final class RandomFileInputAccess implements IRandomInputAccess {
  private final RandomAccessFile randomAccessFile;

  public RandomFileInputAccess(RandomAccessFile randomAccessFile) {
    this.randomAccessFile = randomAccessFile;
  }

  @Override
  public int skipBytes(int n) throws IOException {
    return this.randomAccessFile.skipBytes(n);
  }

  @Override
  public int readUnsignedShort() throws IOException {
    return this.randomAccessFile.readUnsignedShort();
  }

  @Override
  public int readUnsignedByte() throws IOException {
    return this.randomAccessFile.readUnsignedByte();
  }

  @Override
  public String readUTF() throws IOException {
    return this.randomAccessFile.readUTF();
  }

  @Override
  public short readShort() throws IOException {
    return this.randomAccessFile.readShort();
  }

  @Override
  public long readLong() throws IOException {
    return this.randomAccessFile.readLong();
  }

  @Override
  public String readLine() throws IOException {
    return this.randomAccessFile.readLine();
  }

  @Override
  public int readInt() throws IOException {
    return this.randomAccessFile.readInt();
  }

  @Override
  public void readFully(byte[] b, int off, int len) throws IOException {
    this.randomAccessFile.readFully(b, off, len);          
  }

  @Override
  public void readFully(byte[] b) throws IOException {
    this.randomAccessFile.readFully(b);          
  }

  @Override
  public float readFloat() throws IOException {
    return this.randomAccessFile.readFloat();
  }

  @Override
  public double readDouble() throws IOException {
    return this.randomAccessFile.readDouble();
  }

  @Override
  public char readChar() throws IOException {
    return this.randomAccessFile.readChar();
  }

  @Override
  public byte readByte() throws IOException {
    return this.randomAccessFile.readByte();
  }

  @Override
  public boolean readBoolean() throws IOException {
    return this.randomAccessFile.readBoolean();
  }

  @Override
  public void close() throws IOException {
    this.randomAccessFile.close();
  }

  @Override
  public void seek(long pos) throws IOException {
    this.randomAccessFile.seek(pos);          
  }

  @Override
  public long getPointer() throws IOException {
    return this.randomAccessFile.getFilePointer();
  }

  @Override
  public long length() throws IOException {
    return this.randomAccessFile.length();
  }
}
