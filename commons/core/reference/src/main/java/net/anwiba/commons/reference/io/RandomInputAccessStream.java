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

public final class RandomInputAccessStream implements IAccessStream {
  private final IRandomInputAccess randomAccess;

  public RandomInputAccessStream(final IRandomInputAccess randomAccess) {
    this.randomAccess = randomAccess;
  }

  @Override
  public void close() throws IOException {
    this.randomAccess.close();
  }

  @Override
  public long skip(final long diff) throws IOException {
    if (diff == 0) {
      return 0;
    }
    long pos = this.randomAccess.getPointer();
    long len = this.randomAccess.length();
    long newpos = pos + diff;
    if (newpos < 0) {
      newpos = 0;
    }
    if (newpos > len) {
      newpos = len;
    }
    this.randomAccess.seek(newpos);
    return newpos - pos;
  }

  @Override
  public int read(final byte[] array) throws IOException {
    long pos = this.randomAccess.getPointer();
    this.randomAccess.readFully(array);
    long newpos = this.randomAccess.getPointer();
    return (int) (newpos - pos);
  }
}
