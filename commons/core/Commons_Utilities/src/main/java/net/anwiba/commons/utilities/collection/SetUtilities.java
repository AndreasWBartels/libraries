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
package net.anwiba.commons.utilities.collection;

import net.anwiba.commons.lang.functional.IAcceptor;

import java.util.Collection;
import java.util.Set;

public class SetUtilities {

  public static <T> Set<T> getContainsNot(final Set<T> set, final Iterable<T> other) {
    return IterableUtilities.asSet(other, new IAcceptor<T>() {

      @Override
      public boolean accept(final T value) {
        return !set.contains(other);
      }
    });
  }

  public static <T> boolean equals(final Set<T> set, final Collection<T> collection) {
    if (set == collection) {
      return true;
    }
    if (set == null || collection == null || set.size() != collection.size()) {
      return false;
    }
    return set.containsAll(collection);
  }
}