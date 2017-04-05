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

import net.anwiba.commons.lang.functional.IProcedure;

public class AbstractListChangedNotifier<T> implements IListChangedNotifier<T> {

  private final ListenerList<IChangeableListListener<T>> listModelListeners = new ListenerList<>();

  @Override
  public final synchronized void addListModelListener(final IChangeableListListener<T> listener) {
    this.listModelListeners.add(listener);
  }

  @Override
  public final synchronized void removeListModelListener(final IChangeableListListener<T> listener) {
    this.listModelListeners.remove(listener);
  }

  protected void fireObjectsChanged(final Iterable<T> oldObjects, final Iterable<T> newObjects) {
    this.listModelListeners.forAllDo(new IProcedure<IChangeableListListener<T>, RuntimeException>() {

      @Override
      public void execute(final IChangeableListListener<T> listener) throws RuntimeException {
        listener.objectsChanged(oldObjects, newObjects);
      }
    });
  }

  protected final synchronized void fireObjectsAdded(final Iterable<Integer> indeces, final Iterable<T> objects) {
    this.listModelListeners.forAllDo(new IProcedure<IChangeableListListener<T>, RuntimeException>() {

      @Override
      public void execute(final IChangeableListListener<T> listener) throws RuntimeException {
        listener.objectsAdded(indeces, objects);
      }
    });
  }

  protected final synchronized void fireObjectsUpdated(
      final Iterable<Integer> indeces,
      final Iterable<T> oldObjects,
      final Iterable<T> newObjects) {
    this.listModelListeners.forAllDo(new IProcedure<IChangeableListListener<T>, RuntimeException>() {

      @Override
      public void execute(final IChangeableListListener<T> listener) throws RuntimeException {
        listener.objectsUpdated(indeces, oldObjects, newObjects);
      }
    });
  }

  protected final synchronized void fireObjectsRemoved(final Iterable<Integer> indeces, final Iterable<T> objects) {
    this.listModelListeners.forAllDo(new IProcedure<IChangeableListListener<T>, RuntimeException>() {

      @Override
      public void execute(final IChangeableListListener<T> listener) throws RuntimeException {
        listener.objectsRemoved(indeces, objects);
      }
    });
  }
}