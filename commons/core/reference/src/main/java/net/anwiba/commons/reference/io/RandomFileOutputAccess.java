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

public class RandomFileOutputAccess implements IRandomOutputAccess {

  private RandomAccessFile randomAccessFile;

  public RandomFileOutputAccess(RandomAccessFile randomAccessFile) {
    this.randomAccessFile = randomAccessFile;
  }

  @Override
  public void write(int b) throws IOException {
    this.randomAccessFile.write(b);
  }

  @Override
  public void write(byte[] b) throws IOException {
    this.randomAccessFile.write(b);
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    this.randomAccessFile.write(b, off, len);
  }

  @Override
  public void writeBoolean(boolean v) throws IOException {
    this.randomAccessFile.writeBoolean(v);
  }

  @Override
  public void writeByte(int v) throws IOException {
    this.randomAccessFile.writeByte(v);
  }

  @Override
  public void writeShort(int v) throws IOException {
    this.randomAccessFile.writeShort(v);
  }

  @Override
  public void writeChar(int v) throws IOException {
    this.randomAccessFile.writeChar(v);
  }

  @Override
  public void writeInt(int v) throws IOException {
    this.randomAccessFile.writeInt(v);
  }

  @Override
  public void writeLong(long v) throws IOException {
    this.randomAccessFile.writeLong(v);
  }

  @Override
  public void writeFloat(float v) throws IOException {
    this.randomAccessFile.writeFloat(v);
  }

  @Override
  public void writeDouble(double v) throws IOException {
    this.randomAccessFile.writeDouble(v);
  }

  @Override
  public void writeBytes(String s) throws IOException {
    this.randomAccessFile.writeBytes(s);
  }

  @Override
  public void writeChars(String s) throws IOException {
    this.randomAccessFile.writeChars(s);
  }

  @Override
  public void writeUTF(String s) throws IOException {
    this.randomAccessFile.writeUTF(s);
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
