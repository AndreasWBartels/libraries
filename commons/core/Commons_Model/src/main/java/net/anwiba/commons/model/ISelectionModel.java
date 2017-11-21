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

public interface ISelectionModel<T> {

  public abstract void setSelectedObject(final T object);

  public abstract void setSelectedObjects(final Collection<T> objects);

  public abstract void addSelectedObject(final T object);

  public abstract void addSelectedObjects(final Collection<T> objects);

  public abstract boolean isSelected(final T object);

  public abstract void removeSelectedObject(final T object);

  public abstract void removeSelectedObjects(final Collection<T> objects);

  public abstract void removeAllSelectedObjects();

  public abstract boolean isEmpty();

  public abstract int size();

  public abstract void addSelectionListener(final ISelectionListener<T> listener);

  public abstract void removeSelectionListener(final ISelectionListener<T> listener);

  public abstract Iterable<T> getSelectedObjects();

}