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
package net.anwiba.commons.swing.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import net.anwiba.commons.lang.collection.IObjectIterable;
import net.anwiba.commons.lang.collection.ObjectList;
import net.anwiba.commons.model.IChangeableListListener;
import net.anwiba.commons.utilities.ArrayUtilities;
import net.anwiba.commons.utilities.collection.IterableUtilities;
import net.anwiba.commons.utilities.interval.IntegerInterval;

public abstract class AbstractObjectTableModel<T> extends AbstractTableModel implements IObjectTableModel<T> {

  private static final long serialVersionUID = -9054338041837561007L;
  private final Map<T, Set<Integer>> indexByObjectMap = new HashMap<>();
  private final List<T> objects = new ArrayList<>();
  private final IColumnClassProvider columnClassProvider;

  public AbstractObjectTableModel(final List<T> objects, final IColumnClassProvider columnClassProvider) {
    this.columnClassProvider = columnClassProvider;
    this.objects.addAll(objects);
    refreshIndex();
  }

  @Override
  public Class<?> getColumnClass(final int columnIndex) {
    return this.columnClassProvider.getClass(columnIndex);
  }

  @Override
  public final String getColumnName(final int columnIndex) {
    return "Column" + columnIndex; //$NON-NLS-1$
  }

  @Override
  public int size() {
    return getRowCount();
  }

  private void refreshIndex() {
    synchronized (this) {
      this.indexByObjectMap.clear();
      for (int i = 0; i < this.objects.size(); i++) {
        if (!this.indexByObjectMap.containsKey(get(i))) {
          this.indexByObjectMap.put(get(i), new HashSet<Integer>());
        }
        this.indexByObjectMap.get(get(i)).add(Integer.valueOf(i));
      }
    }
  }

  @Override
  public void set(@SuppressWarnings("unchecked") final T... objects) {
    set(Arrays.asList(objects));
  }

  @Override
  public void set(final Iterable<T> objects) {
    final List<T> oldObjects;
    synchronized (this) {
      oldObjects = IterableUtilities.asList(this.objects);
      this.indexByObjectMap.clear();
      this.objects.clear();
      for (final T object : objects) {
        if (!this.indexByObjectMap.containsKey(object)) {
          this.indexByObjectMap.put(object, new HashSet<Integer>());
        }
        final Integer index = Integer.valueOf(this.objects.size());
        this.indexByObjectMap.get(object).add(index);
        this.objects.add(object);
      }
    }
    fireTableDataChanged();
    fireObjectsChanged(oldObjects, IterableUtilities.asList(objects));
  }

  @Override
  public void add(@SuppressWarnings("unchecked") final T... objects) {
    add(Arrays.asList(objects));
  }

  @Override
  public void add(final Iterable<T> objects) {
    final int rows;
    synchronized (this) {
      rows = size();
      for (final T object : objects) {
        if (!this.indexByObjectMap.containsKey(object)) {
          this.indexByObjectMap.put(object, new HashSet<Integer>());
        }
        final Integer index = Integer.valueOf(this.objects.size());
        this.indexByObjectMap.get(object).add(index);
        this.objects.add(object);
      }
    }
    fireTableRowsInserted(rows, this.size() - 1);
    fireObjectsAdded(new IntegerInterval(rows, this.size() - 1), objects);
  }

  @Override
  public T set(final int index, final T object) {
    final T oldObject;
    synchronized (this) {
      if (!(index < getRowCount())) {
        throw new IllegalArgumentException("index out of bounds"); //$NON-NLS-1$
      }
      if (!this.indexByObjectMap.containsKey(object)) {
        this.indexByObjectMap.put(object, new HashSet<Integer>());
      }
      this.indexByObjectMap.get(object).add(Integer.valueOf(this.objects.size()));
      oldObject = this.objects.set(index, object);
    }
    // fireTableDataChanged();
    fireTableRowsUpdated(index, index);
    fireObjectsUpdated(Arrays.asList(Integer.valueOf(index)), Arrays.asList(oldObject), Arrays.asList(object));
    return oldObject;
  }

  @Override
  public synchronized int[] indices(final Iterable<T> objects) {
    final Set<Integer> indexes = new HashSet<>();
    for (final T object : objects) {
      final Set<Integer> objectIndexes = this.indexByObjectMap.get(object);
      indexes.addAll(objectIndexes == null ? new HashSet<Integer>() : objectIndexes);
    }
    return ArrayUtilities.primitives(indexes.toArray(new Integer[indexes.size()]));
  }

  @Override
  public void remove(@SuppressWarnings("unchecked") final T... objects) {
    final int[] indices = indices(Arrays.asList(objects));
    remove(indices);
  }

  @Override
  public void remove(final Iterable<T> objects) {
    final int[] indices = indices(objects);
    remove(indices);
  }

  @Override
  public void remove(final int... indices) {
    final List<Integer> removedIndices = new ArrayList<>();
    final List<T> removedObjects = new ArrayList<>();
    synchronized (this) {
      if (indices.length == 0) {
        return;
      }
      Arrays.sort(indices);
      for (int i = indices.length - 1; i >= 0; i--) {
        final int index = indices[i];
        final T object = get(index);
        final T removedObject = this.objects.remove(index);
        if (removedObject != null) {
          final Set<Integer> indizes = this.indexByObjectMap.get(removedObject);
          if (indizes.remove(Integer.valueOf(index)) && indizes.isEmpty()) {
            this.indexByObjectMap.remove(object);
          }
          removedIndices.add(index);
          removedObjects.add(object);
        }
      }
    }
    removedIndices.forEach(i -> fireTableRowsDeleted(i, i));
    fireObjectsRemoved(removedIndices, removedObjects);
  }

  @Override
  public void removeAll() {
    final int rows;
    final List<T> objects;
    synchronized (this) {
      rows = getRowCount();
      objects = new ArrayList<>(this.objects);
      this.objects.clear();
      this.indexByObjectMap.clear();
    }
    fireTableRowsDeleted(0, rows - 1);
    fireObjectsRemoved(new IntegerInterval(0, rows - 1), objects);
  }

  @Override
  public final IObjectIterable<T> values() {
    return new ObjectList<>(Collections.unmodifiableList(IterableUtilities.asList(this.objects)));
  }

  @Override
  public boolean isEmpty() {
    synchronized (this) {
      return this.objects.isEmpty();
    }
  }

  @Override
  public final int getRowCount() {
    synchronized (this) {
      return this.objects.size();
    }
  }

  @Override
  public Collection<T> get(final int... indices) {
    synchronized (this) {
      final List<T> result = new ArrayList<>();
      for (final int index : indices) {
        result.add(get(index));
      }
      return result;
    }
  }

  @Override
  public T get(final int rowIndex) {
    synchronized (this) {
      if (rowIndex < 0) {
        return null;
      }
      if (!(rowIndex < this.objects.size())) {
        return null;
      }
      return this.objects.get(rowIndex);
    }
  }

  protected List<T> getObjects() {
    synchronized (this) {
      final LinkedList<T> result = new LinkedList<>();
      this.objects.forEach(o -> result.add(o));
      return result;
    }
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

  protected final void fireObjectsAdded(final Iterable<Integer> indeces, final Iterable<T> objects) {
    final List<IChangeableListListener<T>> currentListModelListeners;
    synchronized (this.listModelListeners) {
      currentListModelListeners = new ArrayList<>(this.listModelListeners);
    }
    for (final IChangeableListListener<T> listener : currentListModelListeners) {
      listener.objectsAdded(indeces, objects);
    }
  }

  protected void fireObjectsChanged(final List<T> oldObjects, final List<T> newObjects) {
    final List<IChangeableListListener<T>> currentListModelListeners;
    synchronized (this.listModelListeners) {
      currentListModelListeners = new ArrayList<>(this.listModelListeners);
    }
    for (final IChangeableListListener<T> listener : currentListModelListeners) {
      listener.objectsChanged(oldObjects, newObjects);
    }
  }

  protected final void fireObjectsUpdated(
      final Iterable<Integer> indeces,
      final List<T> oldObjects,
      final List<T> newObjects) {
    final List<IChangeableListListener<T>> currentListModelListeners;
    synchronized (this.listModelListeners) {
      currentListModelListeners = new ArrayList<>(this.listModelListeners);
    }
    for (final IChangeableListListener<T> listener : currentListModelListeners) {
      listener.objectsUpdated(indeces, oldObjects, newObjects);
    }
  }

  protected final void fireObjectsRemoved(final Iterable<Integer> indeces, final Iterable<T> objects) {
    final List<IChangeableListListener<T>> currentListModelListeners;
    synchronized (this.listModelListeners) {
      currentListModelListeners = new ArrayList<>(this.listModelListeners);
    }
    for (final IChangeableListListener<T> listener : currentListModelListeners) {
      listener.objectsRemoved(indeces, objects);
    }
  }
}
