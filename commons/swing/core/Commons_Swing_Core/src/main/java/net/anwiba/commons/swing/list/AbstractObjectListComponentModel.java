/*
 * #%L
 * anwiba commons swing
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
package net.anwiba.commons.swing.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;

import net.anwiba.commons.model.IChangeableListListener;
import net.anwiba.commons.utilities.ArrayUtilities;

public abstract class AbstractObjectListComponentModel<T> extends AbstractListModel<T> implements IListModel<T> {

  private static final long serialVersionUID = -5250421928985085227L;
  private final Map<T, Integer> indexByobjectMap = new HashMap<>();
  private final List<T> objects = new ArrayList<>();

  public AbstractObjectListComponentModel(final List<T> objects) {
    this.objects.addAll(objects);
    refreshIndex();
  }

  private void refreshIndex() {
    synchronized (this) {
      this.indexByobjectMap.clear();
      for (int i = 0; i < this.objects.size(); i++) {
        this.indexByobjectMap.put(getObject(i), Integer.valueOf(i));
      }
    }
  }

  @Override
  public int getSize() {
    synchronized (this) {
      return this.objects.size();
    }
  }

  public void add(@SuppressWarnings({ "unchecked", "hiding" }) final T... objects) {

    if (objects.length == 0) {
      return;
    }
    int rows = 0;
    synchronized (this) {
      rows = getSize();
      for (int i = 0; i < objects.length; i++) {
        this.indexByobjectMap.put(objects[i], Integer.valueOf(getSize()));
        this.objects.add(objects[i]);
      }
    }
    fireIntervalAdded(this, rows, getSize() - 1);
    fireObjectAdded(Arrays.asList(objects));
  }

  public void remove(@SuppressWarnings({ "unchecked", "hiding" }) final T... objects) {
    if (objects.length == 0) {
      return;
    }
    int[] indices = new int[]{};
    synchronized (this) {
      indices = getIndicesOf(Arrays.asList(objects));
      if (indices.length == 0) {
        return;
      }
      Arrays.sort(indices);
      for (int i = indices.length - 1; i >= 0; i--) {
        final int index = indices[i];
        if (!this.objects.remove(getObject(index))) {
          indices[i] = -1;
        }
      }
      refreshIndex();
    }
    for (int i = indices.length - 1; i >= 0; i--) {
      final int index = indices[i];
      if (index != -1) {
        fireIntervalRemoved(this, index, index);
      }
    }
  }

  public void removeAll() {
    int rows = 0;
    @SuppressWarnings("hiding")
    final List<T> objects;
    synchronized (this) {
      rows = getSize();
      if (rows == 0) {
        return;
      }
      objects = new ArrayList<>(this.objects);
      this.objects.clear();
      this.indexByobjectMap.clear();
    }
    fireIntervalRemoved(this, 0, rows - 1);
    fireObjectRemoved(objects);
  }

  @Override
  public T getObject(final int index) {
    return this.objects.get(index);
  }

  @Override
  public T getElementAt(final int index) {
    return getObject(index);
  }

  @Override
  public int[] getIndicesOf(@SuppressWarnings("hiding") final List<T> objects) {
    final List<Integer> indexes = new ArrayList<>();
    for (final T object : objects) {
      final Integer index = this.indexByobjectMap.get(object);
      if (index == null) {
        continue;
      }
      indexes.add(index);
    }
    return ArrayUtilities.primitives(indexes.toArray(new Integer[indexes.size()]));
  }

  private final List<IChangeableListListener<T>> listModelListeners = new ArrayList<>();

  @Override
  public final void addListModelListener(final IChangeableListListener<T> listener) {
    synchronized (this.listModelListeners) {
      this.listModelListeners.add(listener);
    }
  }

  @Override
  public final void removeListModelListener(final IChangeableListListener<T> listener) {
    synchronized (this.listModelListeners) {
      this.listModelListeners.remove(listener);
    }
  }

  protected final void fireObjectAdded(@SuppressWarnings("hiding") final Iterable<T> objects) {
    final List<IChangeableListListener<T>> currentListModelListeners = new ArrayList<>();
    synchronized (this.listModelListeners) {
      currentListModelListeners.addAll(this.listModelListeners);
    }
    for (final IChangeableListListener<T> listener : currentListModelListeners) {
      listener.objectsAdded(null, objects);
    }
  }

  protected final void fireObjectRemoved(@SuppressWarnings("hiding") final Iterable<T> objects) {
    final List<IChangeableListListener<T>> currentListModelListeners = new ArrayList<>();
    synchronized (this.listModelListeners) {
      currentListModelListeners.addAll(this.listModelListeners);
    }
    for (final IChangeableListListener<T> listener : currentListModelListeners) {
      listener.objectsRemoved(null, objects);
    }
  }

  @Override
  public Iterator<T> iterator() {
    return this.objects.iterator();
  }

}
