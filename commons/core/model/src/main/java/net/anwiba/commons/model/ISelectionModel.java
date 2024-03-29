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

import java.util.Collection;

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.stream.IStream;

public interface ISelectionModel<T> {

  void setSelectedObject(final T object);

  void setSelectedObjects(final Collection<T> objects);

  void addSelectedObject(final T object);

  void addSelectedObjects(final Collection<T> objects);

  boolean isSelected(final T object);

  void removeSelectedObject(final T object);

  void removeSelectedObjects(final Collection<T> objects);

  void removeAllSelectedObjects();

  boolean isEmpty();

  int size();

  void addSelectionListener(final ISelectionListener<T> listener);

  void removeSelectionListener(final ISelectionListener<T> listener);

  Iterable<T> getSelectedObjects();

  IOptional<T, RuntimeException> optional();

  IStream<T, RuntimeException> stream();

}