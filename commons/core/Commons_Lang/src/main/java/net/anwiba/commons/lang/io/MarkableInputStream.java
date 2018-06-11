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

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MarkableInputStream extends FilterInputStream {

  private final int available;

  public MarkableInputStream(final InputStream in, final long contentLength) {
    this(in, available(in, contentLength), in.markSupported());
  }

  private static int available(final InputStream in, final long contentLength) {
    try {
      final int available = in.available();
      return available <= 1
          ? contentLength < 0 || contentLength > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) contentLength
          : available;
    } catch (final IOException exception) {
      return Integer.MAX_VALUE;
    }
  }

  public MarkableInputStream(final InputStream in, final int available, final boolean markSupported) {
    super(markSupported ? in : new BufferedInputStream(in));
    this.available = available;
  }

  @Override
  public int available() throws IOException {
    return this.available;
  }
}
