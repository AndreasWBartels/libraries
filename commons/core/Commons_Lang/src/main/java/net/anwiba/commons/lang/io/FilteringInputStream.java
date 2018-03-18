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
package net.anwiba.commons.lang.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.anwiba.commons.lang.io.filter.IFilteringInputStreamValidator;

public class FilteringInputStream extends FilterInputStream {

  private static final int EOF = -1;

  private final IFilteringInputStreamValidator filteringInputStreamValidator;

  public FilteringInputStream(
      final InputStream in,
      final IFilteringInputStreamValidator filteringInputStreamValidator) {
    super(in);
    assert (filteringInputStreamValidator != null);
    this.filteringInputStreamValidator = filteringInputStreamValidator;
  }

  @Override
  public int read() throws IOException {
    if (!this.filteringInputStreamValidator.getQueue().isEmpty()) {
      return this.filteringInputStreamValidator.getQueue().poll();
    }
    int value;
    while ((value = super.read()) > EOF) {
      if (this.filteringInputStreamValidator.accept(value)) {
        return this.filteringInputStreamValidator.getQueue().poll();
      }
    }
    return EOF;
  }

  @Override
  public int read(final byte bytes[], final int offset, final int length) throws IOException {
    if (bytes == null) {
      throw new NullPointerException();
    }
    if (offset < 0 || length < 0 || length > bytes.length - offset) {
      throw new IndexOutOfBoundsException();
    }
    if (length == 0) {
      return 0;
    }
    int character = read();
    if (character == EOF) {
      return EOF;
    }
    bytes[offset] = (byte) character;
    int index = 1;
    try {
      for (; index < length; index++) {
        character = read();
        if (character == EOF) {
          break;
        }
        bytes[offset + index] = (byte) character;
      }
    } catch (final IOException ee) {
      // nothing to do
    }
    return index;
  }

  @Override
  public long skip(final long n) throws IOException {
    for (long i = 0; i < n; i++) {
      if (EOF == read()) {
        return i;
      }
    }
    return n;
  }

  @Override
  public int available() throws IOException {
    return 0;
  }

  @Override
  public synchronized void mark(final int readlimit) {
    // nothing to do
  }

  @Override
  public boolean markSupported() {
    return false;
  }

  @Override
  public synchronized void reset() throws IOException {
    throw new IOException("mark/reset not supported"); //$NON-NLS-1$
  }
}