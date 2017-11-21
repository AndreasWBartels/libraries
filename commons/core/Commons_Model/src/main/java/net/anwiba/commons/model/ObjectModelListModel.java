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

import java.util.ArrayList;
import java.util.List;

public class ObjectModelListModel<T> extends AbstractListChangedNotifier<ObjectModel<T>> {

  private final List<ObjectModel<T>> objectModels = new ArrayList<>();
  private final IChangeableObjectListener objectModelsListener = new IChangeableObjectListener() {

    @Override
    public void objectChanged() {
      fireValueChanged();
    }
  };

  public synchronized void add(final ObjectModel<T> object) {
    final List<ObjectModel<T>> list = new ArrayList<>(1);
    list.add(object);
    addAll(list);
  }

  public synchronized void addAll(final Iterable<ObjectModel<T>> objects) {
    final List<ObjectModel<T>> list = new ArrayList<>();
    for (final ObjectModel<T> object : objects) {
      if (this.objectModels.contains(object)) {
        continue;
      }
      object.addChangeListener(this.objectModelsListener);
      list.add(object);
    }
    if (!list.isEmpty()) {
      this.objectModels.addAll(list);
      fireObjectsAdded(null, list);
    }
  }

  public synchronized void remove(final ObjectModel<T> object) {
    final List<ObjectModel<T>> list = new ArrayList<>(1);
    list.add(object);
    removeAll(list);
  }

  public synchronized void removeAll(final Iterable<ObjectModel<T>> objects) {
    final List<ObjectModel<T>> list = new ArrayList<>();
    for (final ObjectModel<T> object : objects) {
      if (this.objectModels.remove(object)) {
        object.removeChangeListener(this.objectModelsListener);
        list.add(object);
      }
    }
    if (!list.isEmpty()) {
      fireObjectsRemoved(null, list);
    }
  }

  public synchronized void removeAll() {
    if (this.objectModels.isEmpty()) {
      return;
    }
    final List<ObjectModel<T>> list = new ArrayList<>(this.objectModels);
    this.objectModels.clear();
    fireObjectsRemoved(null, list);
  }

  public synchronized Iterable<ObjectModel<T>> models() {
    return this.objectModels;
  }

  public synchronized ObjectModel<T> last() {
    return this.objectModels.get(this.objectModels.size() - 1);
  }

  public synchronized boolean isEmpty() {
    return this.objectModels.isEmpty();
  }

  public synchronized int size() {
    return this.objectModels.size();
  }

  private final ListenerList<IChangeableObjectListener> changeableObjectListeners = new ListenerList<>();

  public final synchronized void addChangeListener(final IChangeableObjectListener listener) {
    this.changeableObjectListeners.add(listener);
  }

  public final synchronized void removeChangeListener(final IChangeableObjectListener listener) {
    this.changeableObjectListeners.remove(listener);
  }

  protected final synchronized void fireValueChanged() {
    this.changeableObjectListeners.forAllDo(new IProcedure<IChangeableObjectListener, RuntimeException>() {

      @Override
      public void execute(final IChangeableObjectListener listener) throws RuntimeException {
        listener.objectChanged();
      }
    });
  }
}