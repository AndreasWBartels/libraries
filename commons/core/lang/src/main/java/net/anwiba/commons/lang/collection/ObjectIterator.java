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
package net.anwiba.commons.lang.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class ObjectIterator<O> implements Iterator<O> {

  private final Supplier<O> supplier;
  private O object = null;

  public ObjectIterator(Supplier<O> supplier) {
    this.supplier = supplier;
  }

  @Override
  public boolean hasNext() {
    if (object != null) {
      return true;
    }
    object = supplier.get();
    return object != null;
  }

  @Override
  public O next() {
    try {
      if (hasNext()) {
        return object;
      }
      throw new NoSuchElementException();
    } finally {
      object = null;
    }
  }

}
