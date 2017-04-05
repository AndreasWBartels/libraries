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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import net.anwiba.commons.model.IChangeableListListener;
import net.anwiba.commons.utilities.ArrayUtilities;
import net.anwiba.commons.utilities.collection.IterableUtilities;
import net.anwiba.commons.utilities.interval.IntegerInterval;

public abstract class AbstractObjectTableModel<T> extends AbstractTableModel implements IObjectTableModel<T> {

  private static final long serialVersionUID = -9054338041837561007L;
  private final Map<T, Set<Integer>> indexByobjectMap = new HashMap<>();
  private final List<T> objects = new ArrayList<>();

  public AbstractObjectTableModel(final List<T> objects) {
    this.objects.addAll(objects);
    refreshIndex();
  }

  @Override
  public final String getColumnName(final int columnIndex) {
    return "Column" + columnIndex; //$NON-NLS-1$
  }

  @Override
  public int size() {
    return getRowCount();
  }

  private synchronized void refreshIndex() {
    this.indexByobjectMap.clear();
    for (int i = 0; i < this.objects.size(); i++) {
      if (!this.indexByobjectMap.containsKey(get(i))) {
        this.indexByobjectMap.put(get(i), new HashSet<Integer>());
      }
      this.indexByobjectMap.get(get(i)).add(Integer.valueOf(i));
    }
  }

  @Override
  public synchronized void set(@SuppressWarnings("unchecked") final T... objects) {
    set(Arrays.asList(objects));
  }

  @Override
  public synchronized void set(final Iterable<T> objects) {
    final List<T> oldObjects = IterableUtilities.asList(this.objects);
    this.indexByobjectMap.clear();
    this.objects.clear();
    for (final T object : objects) {
      if (!this.indexByobjectMap.containsKey(object)) {
        this.indexByobjectMap.put(object, new HashSet<Integer>());
      }
      final Integer index = Integer.valueOf(this.getObjects().size());
      this.indexByobjectMap.get(object).add(index);
      this.objects.add(object);
    }
    fireTableDataChanged();
    fireObjectsChanged(oldObjects, IterableUtilities.asList(objects));
  }

  @Override
  public synchronized void add(@SuppressWarnings("unchecked") final T... objects) {
    add(Arrays.asList(objects));
  }

  @Override
  public synchronized void add(final Iterable<T> objects) {
    final int rows = size();
    for (final T object : objects) {
      if (!this.indexByobjectMap.containsKey(object)) {
        this.indexByobjectMap.put(object, new HashSet<Integer>());
      }
      final Integer index = Integer.valueOf(this.getObjects().size());
      this.indexByobjectMap.get(object).add(index);
      this.objects.add(object);
    }
    fireTableRowsInserted(rows, this.size() - 1);
    fireObjectsAdded(new IntegerInterval(rows, this.size() - 1), objects);
  }

  @Override
  public synchronized void set(final int index, final T object) {
    if (!(index < getRowCount())) {
      throw new IllegalArgumentException("index out of bounds"); //$NON-NLS-1$
    }
    if (!this.indexByobjectMap.containsKey(object)) {
      this.indexByobjectMap.put(object, new HashSet<Integer>());
    }
    this.indexByobjectMap.get(object).add(Integer.valueOf(this.getObjects().size()));
    final T oldObject = this.objects.set(index, object);
    // fireTableDataChanged();
    fireTableRowsUpdated(index, index);
    fireObjectsUpdated(Arrays.asList(Integer.valueOf(index)), Arrays.asList(oldObject), Arrays.asList(object));
  }

  @Override
  public synchronized int[] indices(final Iterable<T> objects) {
    final Set<Integer> indexes = new HashSet<>();
    for (final T object : objects) {
      final Set<Integer> objectIndexes = this.indexByobjectMap.get(object);
      indexes.addAll(objectIndexes == null ? new HashSet<Integer>() : objectIndexes);
    }
    return ArrayUtilities.primitives(indexes.toArray(new Integer[indexes.size()]));
  }

  @Override
  public synchronized void remove(@SuppressWarnings("unchecked") final T... objects) {
    final int[] indices = indices(Arrays.asList(objects));
    remove(indices);
  }

  @Override
  public synchronized void remove(final Iterable<T> objects) {
    final int[] indices = indices(objects);
    remove(indices);
  }

  @Override
  public synchronized void remove(final int... indices) {
    if (indices.length == 0) {
      return;
    }
    Arrays.sort(indices);
    final List<T> removedObjects = new ArrayList<>();
    final Set<Integer> indexSet = new HashSet<>();
    for (int i = indices.length - 1; i >= 0; i--) {
      final int index = indices[i];
      final T object = get(index);
      final T removedObject = this.objects.remove(index);
      if (removedObject != null) {
        final Set<Integer> indizes = this.indexByobjectMap.get(removedObject);
        if (indizes.remove(Integer.valueOf(index)) && indizes.isEmpty()) {
          this.indexByobjectMap.remove(object);
        }
        removedObjects.add(object);
        fireTableRowsDeleted(index, index);
      }
    }
    fireObjectsRemoved(indexSet, removedObjects);
  }

  @Override
  public synchronized void removeAll() {
    final int rows = getRowCount();
    final List<T> objects = new ArrayList<>(this.objects);
    this.objects.clear();
    this.indexByobjectMap.clear();
    fireTableRowsDeleted(0, rows - 1);
    fireObjectsRemoved(new IntegerInterval(0, rows - 1), objects);
  }

  @Override
  public synchronized Iterable<T> values() {
    return Collections.unmodifiableList(IterableUtilities.asList(this.objects));
  }

  @Override
  public boolean isEmpty() {
    return this.objects.isEmpty();
  }

  @Override
  public synchronized final int getRowCount() {
    return this.objects.size();
  }

  @Override
  public synchronized Collection<T> get(final int... indices) {
    final List<T> result = new ArrayList<>();
    for (final int index : indices) {
      result.add(get(index));
    }
    return result;
  }

  @Override
  public synchronized T get(final int rowIndex) {
    if (rowIndex < 0) {
      return null;
    }
    if (!(rowIndex < this.objects.size())) {
      return null;
    }
    return this.objects.get(rowIndex);
  }

  protected List<T> getObjects() {
    return this.objects;
  }

  private final List<IChangeableListListener<T>> listModelListeners = new ArrayList<>();

  @Override
  public final synchronized void addListModelListener(final IChangeableListListener<T> listener) {
    this.listModelListeners.add(listener);
  }

  @Override
  public final synchronized void removeListModelListener(final IChangeableListListener<T> listener) {
    this.listModelListeners.remove(listener);
  }

  protected final synchronized void fireObjectsAdded(final Iterable<Integer> indeces, final Iterable<T> objects) {
    final List<IChangeableListListener<T>> currentListModelListeners = new ArrayList<>(this.listModelListeners);
    for (final IChangeableListListener<T> listener : currentListModelListeners) {
      listener.objectsAdded(indeces, objects);
    }
  }

  protected void fireObjectsChanged(final List<T> oldObjects, final List<T> newObjects) {
    final List<IChangeableListListener<T>> currentListModelListeners = new ArrayList<>(this.listModelListeners);
    for (final IChangeableListListener<T> listener : currentListModelListeners) {
      listener.objectsChanged(oldObjects, newObjects);
    }
  }

  protected final synchronized void fireObjectsUpdated(
      final Iterable<Integer> indeces,
      final List<T> oldObjects,
      final List<T> newObjects) {
    final List<IChangeableListListener<T>> currentListModelListeners = new ArrayList<>(this.listModelListeners);
    for (final IChangeableListListener<T> listener : currentListModelListeners) {
      listener.objectsUpdated(indeces, oldObjects, newObjects);
    }
  }

  protected final synchronized void fireObjectsRemoved(final Iterable<Integer> indeces, final Iterable<T> objects) {
    final List<IChangeableListListener<T>> currentListModelListeners = new ArrayList<>(this.listModelListeners);
    for (final IChangeableListListener<T> listener : currentListModelListeners) {
      listener.objectsRemoved(indeces, objects);
    }
  }
}