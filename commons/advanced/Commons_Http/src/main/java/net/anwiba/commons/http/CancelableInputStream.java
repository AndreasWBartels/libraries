/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.commons.http;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

import net.anwiba.commons.thread.cancel.ICanceler;

public class CancelableInputStream extends FilterInputStream {

  private final ICanceler canceler;

  public CancelableInputStream(final ICanceler canceler, final InputStream in) {
    super(in);
    this.canceler = canceler;
  }

  @Override
  public int read() throws IOException {
    try {
      this.canceler.check();
      return super.read();
    } catch (final InterruptedException exception) {
      throw new InterruptedIOException();
    }
  }

}