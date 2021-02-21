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
package net.anwiba.commons.utilities.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;

public class UriInputStream extends InputStream {

  private final InputStream inputStream;

  public UriInputStream(final URI uri) throws MalformedURLException, IOException {
    this.inputStream = uri.toURL().openStream();
  }

  @Override
  public int available() throws IOException {
    return this.inputStream.available();
  }

  @Override
  public int read() throws IOException {
    return this.inputStream.read();
  }

  @Override
  public int read(final byte[] b) throws IOException {
    return this.inputStream.read(b);
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    return this.inputStream.read(b, off, len);
  }

  @Override
  public long skip(final long n) throws IOException {
    return this.inputStream.skip(n);
  }

  @Override
  public synchronized void reset() throws IOException {
    this.inputStream.reset();
  }

  @Override
  public boolean markSupported() {
    return this.inputStream.markSupported();
  }

  @Override
  public void close() throws IOException {
    this.inputStream.close();
  }
}
