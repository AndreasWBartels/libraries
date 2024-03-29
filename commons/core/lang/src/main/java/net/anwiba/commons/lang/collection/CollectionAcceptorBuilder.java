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
package net.anwiba.commons.lang.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.anwiba.commons.lang.functional.IAcceptor;

public class CollectionAcceptorBuilder<T> {

  private final List<IAcceptor<T>> acceptors = new ArrayList<>();
  private IAcceptor<T> otherwise = v -> false;

  public static class CollectionAcceptor<T> implements IAcceptor<Collection<T>> {

    private final List<IAcceptor<T>> acceptors;
    private final IAcceptor<T> otherwise;

    public CollectionAcceptor(final Collection<IAcceptor<T>> acceptors, final IAcceptor<T> otherwise) {
      this.acceptors = new ArrayList<>(acceptors);
      this.otherwise = otherwise;
    }

    @Override
    public boolean accept(final Collection<T> values) {
      if (values.size() < this.acceptors.size()) {
        return false;
      }
      final Iterator<IAcceptor<T>> iterator = this.acceptors.iterator();
      boolean result = true;
      for (T value : values) {
        if (iterator.hasNext()) {
          result = result && iterator.next().accept(value);
          continue;
        }
        result = result && this.otherwise.accept(value);
      }
      return result;
    }
  }

  public CollectionAcceptorBuilder<T> accept(final IAcceptor<T> acceptor) {
    this.acceptors.add(acceptor);
    return this;
  }

  public CollectionAcceptorBuilder<T> otherwise(@SuppressWarnings("hiding") final IAcceptor<T> otherwise) {
    this.otherwise = otherwise;
    return this;
  }

  public IAcceptor<Collection<T>> build() {
    return new CollectionAcceptor<>(this.acceptors, this.otherwise);
  }

}
