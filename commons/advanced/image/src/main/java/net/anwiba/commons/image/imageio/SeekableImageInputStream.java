/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2019 Andreas W. Bartels
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
package net.anwiba.commons.image.imageio;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.Stack;

import javax.imageio.IIOException;
import javax.imageio.stream.IIOByteBuffer;
import javax.imageio.stream.ImageInputStream;

import org.eclipse.imagen.media.codec.SeekableStream;

public class SeekableImageInputStream implements ImageInputStream {

  private static final int BYTE_BUF_LENGTH = 8192;
  byte[] byteBuf = new byte[BYTE_BUF_LENGTH];
  private final Stack<Long> markByteStack = new Stack<>();
  private final Stack<Integer> markBitStack = new Stack<>();
  private SeekableStream seekableStream;
  private boolean isClosed = false;
  private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
  private int bitOffset = 0;
  private long flushedPos = 0;

  public SeekableImageInputStream(final SeekableStream seekableStream) {
    this.seekableStream = seekableStream;
  }

  protected final void checkClosed() throws IOException {
    if (this.isClosed) {
      throw new IOException("closed");
    }
  }

  @Override
  public int read() throws IOException {
    checkClosed();
    return this.seekableStream.read();
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    checkClosed();
    return this.seekableStream.read(b, off, len);
  }

  @Override
  public void close() throws IOException {
    this.isClosed = true;
    this.seekableStream = null;
  }

  @Override
  public void setByteOrder(final ByteOrder byteOrder) {
    this.byteOrder = byteOrder;
  }

  @Override
  public ByteOrder getByteOrder() {
    return this.byteOrder;
  }

  @Override
  public int read(final byte[] b) throws IOException {
    checkClosed();
    return this.seekableStream.read(b);
  }

  @Override
  public void readBytes(final IIOByteBuffer buf, int len) throws IOException {
    if (len < 0) {
      throw new IndexOutOfBoundsException("len < 0!");
    }
    if (buf == null) {
      throw new NullPointerException("buf == null!");
    }

    byte[] data = new byte[len];
    len = read(data, 0, len);

    buf.setData(data);
    buf.setOffset(0);
    buf.setLength(len);
  }

  @Override
  public boolean readBoolean() throws IOException {
    checkClosed();
    return this.seekableStream.readBoolean();
  }

  @Override
  public byte readByte() throws IOException {
    checkClosed();
    return this.seekableStream.readByte();
  }

  @Override
  public int readUnsignedByte() throws IOException {
    checkClosed();
    return this.seekableStream.readUnsignedByte();
  }

  @Override
  public short readShort() throws IOException {
    checkClosed();
    if (Objects.equals(this.byteOrder, ByteOrder.LITTLE_ENDIAN)) {
      return this.seekableStream.readShortLE();
    }
    return this.seekableStream.readShort();
  }

  @Override
  public int readUnsignedShort() throws IOException {
    checkClosed();
    if (Objects.equals(this.byteOrder, ByteOrder.LITTLE_ENDIAN)) {
      return this.seekableStream.readUnsignedShortLE();
    }
    return this.seekableStream.readUnsignedShort();
  }

  @Override
  public char readChar() throws IOException {
    checkClosed();
    if (Objects.equals(this.byteOrder, ByteOrder.LITTLE_ENDIAN)) {
      return this.seekableStream.readCharLE();
    }
    return this.seekableStream.readChar();
  }

  @Override
  public int readInt() throws IOException {
    checkClosed();
    if (Objects.equals(this.byteOrder, ByteOrder.LITTLE_ENDIAN)) {
      return this.seekableStream.readIntLE();
    }
    return this.seekableStream.readInt();
  }

  @Override
  public long readUnsignedInt() throws IOException {
    checkClosed();
    if (Objects.equals(this.byteOrder, ByteOrder.LITTLE_ENDIAN)) {
      return this.seekableStream.readUnsignedIntLE();
    }
    return this.seekableStream.readUnsignedInt();
  }

  @Override
  public long readLong() throws IOException {
    checkClosed();
    if (Objects.equals(this.byteOrder, ByteOrder.LITTLE_ENDIAN)) {
      return this.seekableStream.readLongLE();
    }
    return this.seekableStream.readLong();
  }

  @Override
  public float readFloat() throws IOException {
    checkClosed();
    if (Objects.equals(this.byteOrder, ByteOrder.LITTLE_ENDIAN)) {
      return this.seekableStream.readFloatLE();
    }
    return this.seekableStream.readFloat();
  }

  @Override
  public double readDouble() throws IOException {
    checkClosed();
    if (Objects.equals(this.byteOrder, ByteOrder.LITTLE_ENDIAN)) {
      return this.seekableStream.readDoubleLE();
    }
    return this.seekableStream.readDouble();
  }

  @Override
  public String readLine() throws IOException {
    checkClosed();
    return this.seekableStream.readLine();
  }

  @Override
  public String readUTF() throws IOException {
    checkClosed();
    return this.seekableStream.readUTF();
  }

  @Override
  public void readFully(final byte[] b, int off, int len) throws IOException {
    if (off < 0 || len < 0 || off + len > b.length || off + len < 0) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > b.length!");
    }

    while (len > 0) {
      int nbytes = read(b, off, len);
      if (nbytes == -1) {
        throw new EOFException();
      }
      off += nbytes;
      len -= nbytes;
    }
  }

  @Override
  public void readFully(final byte[] b) throws IOException {
    readFully(b, 0, b.length);
  }

  @Override
  public void readFully(final short[] s, int off, int len) throws IOException {
    // Fix 4430357 - if off + len < 0, overflow occurred
    if (off < 0 || len < 0 || off + len > s.length || off + len < 0) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > s.length!");
    }

    while (len > 0) {
      int nelts = Math.min(len, this.byteBuf.length / 2);
      readFully(this.byteBuf, 0, nelts * 2);
      toShorts(this.byteBuf, s, off, nelts);
      off += nelts;
      len -= nelts;
    }
  }

  @Override
  public void readFully(final char[] c, int off, int len) throws IOException {
    // Fix 4430357 - if off + len < 0, overflow occurred
    if (off < 0 || len < 0 || off + len > c.length || off + len < 0) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length!");
    }

    while (len > 0) {
      int nelts = Math.min(len, this.byteBuf.length / 2);
      readFully(this.byteBuf, 0, nelts * 2);
      toChars(this.byteBuf, c, off, nelts);
      off += nelts;
      len -= nelts;
    }
  }

  @Override
  public void readFully(final int[] i, int off, int len) throws IOException {
    // Fix 4430357 - if off + len < 0, overflow occurred
    if (off < 0 || len < 0 || off + len > i.length || off + len < 0) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > i.length!");
    }

    while (len > 0) {
      int nelts = Math.min(len, this.byteBuf.length / 4);
      readFully(this.byteBuf, 0, nelts * 4);
      toInts(this.byteBuf, i, off, nelts);
      off += nelts;
      len -= nelts;
    }
  }

  @Override
  public void readFully(final long[] l, int off, int len) throws IOException {
    // Fix 4430357 - if off + len < 0, overflow occurred
    if (off < 0 || len < 0 || off + len > l.length || off + len < 0) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > l.length!");
    }

    while (len > 0) {
      int nelts = Math.min(len, this.byteBuf.length / 8);
      readFully(this.byteBuf, 0, nelts * 8);
      toLongs(this.byteBuf, l, off, nelts);
      off += nelts;
      len -= nelts;
    }
  }

  @Override
  public void readFully(final float[] f, int off, int len) throws IOException {
    // Fix 4430357 - if off + len < 0, overflow occurred
    if (off < 0 || len < 0 || off + len > f.length || off + len < 0) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > f.length!");
    }

    while (len > 0) {
      int nelts = Math.min(len, this.byteBuf.length / 4);
      readFully(this.byteBuf, 0, nelts * 4);
      toFloats(this.byteBuf, f, off, nelts);
      off += nelts;
      len -= nelts;
    }
  }

  @Override
  public void readFully(final double[] d, int off, int len) throws IOException {
    // Fix 4430357 - if off + len < 0, overflow occurred
    if (off < 0 || len < 0 || off + len > d.length || off + len < 0) {
      throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > d.length!");
    }

    while (len > 0) {
      int nelts = Math.min(len, this.byteBuf.length / 8);
      readFully(this.byteBuf, 0, nelts * 8);
      toDoubles(this.byteBuf, d, off, nelts);
      off += nelts;
      len -= nelts;
    }
  }

  private void toShorts(final byte[] b, final short[] s, final int off, final int len) {
    int boff = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int j = 0; j < len; j++) {
        int b0 = b[boff];
        int b1 = b[boff + 1] & 0xff;
        s[off + j] = (short) ((b0 << 8) | b1);
        boff += 2;
      }
    } else {
      for (int j = 0; j < len; j++) {
        int b0 = b[boff + 1];
        int b1 = b[boff] & 0xff;
        s[off + j] = (short) ((b0 << 8) | b1);
        boff += 2;
      }
    }
  }

  private void toChars(final byte[] b, final char[] c, final int off, final int len) {
    int boff = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int j = 0; j < len; j++) {
        int b0 = b[boff];
        int b1 = b[boff + 1] & 0xff;
        c[off + j] = (char) ((b0 << 8) | b1);
        boff += 2;
      }
    } else {
      for (int j = 0; j < len; j++) {
        int b0 = b[boff + 1];
        int b1 = b[boff] & 0xff;
        c[off + j] = (char) ((b0 << 8) | b1);
        boff += 2;
      }
    }
  }

  private void toInts(final byte[] b, final int[] i, final int off, final int len) {
    int boff = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int j = 0; j < len; j++) {
        int b0 = b[boff];
        int b1 = b[boff + 1] & 0xff;
        int b2 = b[boff + 2] & 0xff;
        int b3 = b[boff + 3] & 0xff;
        i[off + j] = (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
        boff += 4;
      }
    } else {
      for (int j = 0; j < len; j++) {
        int b0 = b[boff + 3];
        int b1 = b[boff + 2] & 0xff;
        int b2 = b[boff + 1] & 0xff;
        int b3 = b[boff] & 0xff;
        i[off + j] = (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
        boff += 4;
      }
    }
  }

  private void toLongs(final byte[] b, final long[] l, final int off, final int len) {
    int boff = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int j = 0; j < len; j++) {
        int b0 = b[boff];
        int b1 = b[boff + 1] & 0xff;
        int b2 = b[boff + 2] & 0xff;
        int b3 = b[boff + 3] & 0xff;
        int b4 = b[boff + 4];
        int b5 = b[boff + 5] & 0xff;
        int b6 = b[boff + 6] & 0xff;
        int b7 = b[boff + 7] & 0xff;

        int i0 = (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
        int i1 = (b4 << 24) | (b5 << 16) | (b6 << 8) | b7;

        l[off + j] = ((long) i0 << 32) | (i1 & 0xffffffffL);
        boff += 8;
      }
    } else {
      for (int j = 0; j < len; j++) {
        int b0 = b[boff + 7];
        int b1 = b[boff + 6] & 0xff;
        int b2 = b[boff + 5] & 0xff;
        int b3 = b[boff + 4] & 0xff;
        int b4 = b[boff + 3];
        int b5 = b[boff + 2] & 0xff;
        int b6 = b[boff + 1] & 0xff;
        int b7 = b[boff] & 0xff;

        int i0 = (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
        int i1 = (b4 << 24) | (b5 << 16) | (b6 << 8) | b7;

        l[off + j] = ((long) i0 << 32) | (i1 & 0xffffffffL);
        boff += 8;
      }
    }
  }

  private void toFloats(final byte[] b, final float[] f, final int off, final int len) {
    int boff = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int j = 0; j < len; j++) {
        int b0 = b[boff];
        int b1 = b[boff + 1] & 0xff;
        int b2 = b[boff + 2] & 0xff;
        int b3 = b[boff + 3] & 0xff;
        int i = (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
        f[off + j] = Float.intBitsToFloat(i);
        boff += 4;
      }
    } else {
      for (int j = 0; j < len; j++) {
        int b0 = b[boff + 3];
        int b1 = b[boff + 2] & 0xff;
        int b2 = b[boff + 1] & 0xff;
        int b3 = b[boff + 0] & 0xff;
        int i = (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
        f[off + j] = Float.intBitsToFloat(i);
        boff += 4;
      }
    }
  }

  private void toDoubles(final byte[] b, final double[] d, final int off, final int len) {
    int boff = 0;
    if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
      for (int j = 0; j < len; j++) {
        int b0 = b[boff];
        int b1 = b[boff + 1] & 0xff;
        int b2 = b[boff + 2] & 0xff;
        int b3 = b[boff + 3] & 0xff;
        int b4 = b[boff + 4];
        int b5 = b[boff + 5] & 0xff;
        int b6 = b[boff + 6] & 0xff;
        int b7 = b[boff + 7] & 0xff;

        int i0 = (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
        int i1 = (b4 << 24) | (b5 << 16) | (b6 << 8) | b7;
        long l = ((long) i0 << 32) | (i1 & 0xffffffffL);

        d[off + j] = Double.longBitsToDouble(l);
        boff += 8;
      }
    } else {
      for (int j = 0; j < len; j++) {
        int b0 = b[boff + 7];
        int b1 = b[boff + 6] & 0xff;
        int b2 = b[boff + 5] & 0xff;
        int b3 = b[boff + 4] & 0xff;
        int b4 = b[boff + 3];
        int b5 = b[boff + 2] & 0xff;
        int b6 = b[boff + 1] & 0xff;
        int b7 = b[boff] & 0xff;

        int i0 = (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
        int i1 = (b4 << 24) | (b5 << 16) | (b6 << 8) | b7;
        long l = ((long) i0 << 32) | (i1 & 0xffffffffL);

        d[off + j] = Double.longBitsToDouble(l);
        boff += 8;
      }
    }
  }

  @Override
  public long getStreamPosition() throws IOException {
    checkClosed();
    return this.seekableStream.getFilePointer();
  }

  @Override
  public int getBitOffset() throws IOException {
    checkClosed();
    return this.bitOffset;
  }

  @Override
  public void setBitOffset(final int bitOffset) throws IOException {
    checkClosed();
    if (bitOffset < 0 || bitOffset > 7) {
      throw new IllegalArgumentException("bitOffset must be betwwen 0 and 7!");
    }
    this.bitOffset = bitOffset;
  }

  @Override
  public int readBit() throws IOException {
    checkClosed();

    // Compute final bit offset before we call read() and seek()
    int newBitOffset = (this.bitOffset + 1) & 0x7;

    int val = read();
    if (val == -1) {
      throw new EOFException();
    }

    if (newBitOffset != 0) {
      // Move byte position back if in the middle of a byte
      seek(getStreamPosition() - 1);
      // Shift the bit to be read to the rightmost position
      val >>= 8 - newBitOffset;
    }
    this.bitOffset = newBitOffset;

    return val & 0x1;
  }

  @Override
  public long readBits(final int numBits) throws IOException {
    checkClosed();

    if (numBits < 0 || numBits > 64) {
      throw new IllegalArgumentException();
    }
    if (numBits == 0) {
      return 0L;
    }

    // Have to read additional bits on the left equal to the bit offset
    int bitsToRead = numBits + this.bitOffset;

    // Compute final bit offset before we call read() and seek()
    int newBitOffset = (this.bitOffset + numBits) & 0x7;

    // Read a byte at a time, accumulate
    long accum = 0L;
    while (bitsToRead > 0) {
      int val = read();
      if (val == -1) {
        throw new EOFException();
      }

      accum <<= 8;
      accum |= val;
      bitsToRead -= 8;
    }

    // Move byte position back if in the middle of a byte
    if (newBitOffset != 0) {
      seek(getStreamPosition() - 1);
    }
    this.bitOffset = newBitOffset;

    // Shift away unwanted bits on the right.
    accum >>>= (-bitsToRead); // Negative of bitsToRead == extra bits read

    // Mask out unwanted bits on the left
    accum &= (-1L >>> (64 - numBits));

    return accum;
  }

  @Override
  public long length() throws IOException {
    return -1L;
  }

  @Override
  public int skipBytes(final int n) throws IOException {
    checkClosed();
    int skipBytes = this.seekableStream.skipBytes(n);
    this.bitOffset = 0;
    return skipBytes;
  }

  @Override
  public long skipBytes(final long n) throws IOException {
    checkClosed();
    long skipBytes = this.seekableStream.skip(n);
    this.bitOffset = 0;
    return skipBytes;
  }

  @Override
  public void seek(final long pos) throws IOException {
    checkClosed();
    this.seekableStream.seek(pos);
    this.bitOffset = 0;
  }

  /**
   * Pushes the current stream position onto a stack of marked positions.
   */
  @Override
  public void mark() {
    try {
      this.markByteStack.push(Long.valueOf(getStreamPosition()));
      this.markBitStack.push(Integer.valueOf(getBitOffset()));
    } catch (IOException e) {
    }
  }

  /**
   * Resets the current stream byte and bit positions from the stack of marked positions.
   *
   * <p>
   * An {@code IOException} will be thrown if the previous marked position lies in the discarded portion of the stream.
   *
   * @exception IOException if an I/O error occurs.
   */
  @Override
  public void reset() throws IOException {
    if (this.markByteStack.empty()) {
      return;
    }

    long pos = this.markByteStack.pop().longValue();
    if (pos < this.flushedPos) {
      throw new IIOException("Previous marked position has been discarded!");
    }
    seek(pos);

    int offset = this.markBitStack.pop().intValue();
    setBitOffset(offset);
  }

  @Override
  public void flushBefore(final long pos) throws IOException {
    checkClosed();
    if (pos < this.flushedPos) {
      throw new IndexOutOfBoundsException("pos < flushedPos!");
    }
    if (pos > getStreamPosition()) {
      throw new IndexOutOfBoundsException("pos > getStreamPosition()!");
    }
    // Invariant: flushedPos >= 0
    this.flushedPos = pos;
  }

  @Override
  public void flush() throws IOException {
    flushBefore(getStreamPosition());
  }

  @Override
  public long getFlushedPosition() {
    return this.flushedPos;
  }

  @Override
  public boolean isCached() {
    return true;
  }

  @Override
  public boolean isCachedMemory() {
    return true;
  }

  @Override
  public boolean isCachedFile() {
    return false;
  }
}
