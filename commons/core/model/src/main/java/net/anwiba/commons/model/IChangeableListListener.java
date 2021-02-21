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
package net.anwiba.commons.model;

@SuppressWarnings("unused")
public interface IChangeableListListener<T> {

  default void objectsChanged(final Iterable<T> objects) {
  }

  default void objectsUpdated(
      final Iterable<Integer> indeces,
      final Iterable<T> oldObjects,
      final Iterable<T> newObjects) {
  }

  default void objectsRemoved(final Iterable<Integer> indeces, final Iterable<T> object) {
  }

  default void objectsChanged(final Iterable<T> oldObjects, final Iterable<T> newObjects) {
  }

  default void objectsAdded(final Iterable<Integer> indeces, final Iterable<T> object) {
  }
}
