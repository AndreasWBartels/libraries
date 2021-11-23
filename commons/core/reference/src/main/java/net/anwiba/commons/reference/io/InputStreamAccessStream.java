/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
import java.io.InputStream;

public final class InputStreamAccessStream implements IAccessStream {
  
  private final InputStream inputStream;

  public InputStreamAccessStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  @Override
  public void close() throws IOException {
    this.inputStream.close();
  }

  @Override
  public long skip(long diff) throws IOException {
    return this.inputStream.skip(diff);
  }

  @Override
  public int read(byte[] array) throws IOException {
    return this.inputStream.read(array);
  }
}
