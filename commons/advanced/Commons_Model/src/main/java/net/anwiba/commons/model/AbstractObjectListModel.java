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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import net.anwiba.commons.utilities.ArrayUtilities;
import net.anwiba.commons.utilities.collection.IterableUtilities;
import net.anwiba.commons.utilities.interval.IntegerInterval;

public abstract class AbstractObjectListModel<T> extends AbstractListChangedNotifier<T> implements IObjectListModel<T> {

  private final Object semaphor = new Object();
  private final Map<T, Set<Integer>> indexByobjectMap = new HashMap<>();
  private final List<T> objects = new ArrayList<>();

  public AbstractObjectListModel(final List<T> objects) {
    this.objects.addAll(objects);
    refreshIndex();
  }

  private void refreshIndex() {
    this.indexByobjectMap.clear();
    for (int i = 0; i < size(); i++) {
      if (!this.indexByobjectMap.containsKey(get(i))) {
        this.indexByobjectMap.put(get(i), new HashSet<Integer>());
      }
      this.indexByobjectMap.get(get(i)).add(Integer.valueOf(i));
    }
  }

  @Override
  public int size() {
    synchronized (this.semaphor) {
      return this.objects.size();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void set(final T... objects) {
    set(Arrays.asList(objects));
  }

  @Override
  public void set(final Iterable<T> objects) {
    final List<T> oldObjects = new ArrayList<>();
    synchronized (this.semaphor) {
      oldObjects.addAll(IterableUtilities.asList(this.objects));
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
    }
    fireObjectsChanged(oldObjects, objects);
  }

  @Override
  public void set(final int index, final T object) {
    final AtomicReference<T> oldObjectReference = new AtomicReference<>();
    synchronized (this.semaphor) {
      if (!(index < size())) {
        throw new IllegalArgumentException("index out of bounds"); //$NON-NLS-1$
      }
      oldObjectReference.set(this.objects.set(index, object));
      refreshIndex();
    }
    fireObjectsUpdated(
        Arrays.asList(Integer.valueOf(index)),
        Arrays.asList(oldObjectReference.get()),
        Arrays.asList(object));
  }

  @SuppressWarnings("unchecked")
  @Override
  public synchronized void add(@SuppressWarnings("hiding") final T... objects) {
    add(Arrays.asList(objects));
  }

  @Override
  public void add(@SuppressWarnings("hiding") final Iterable<T> objects) {
    final AtomicInteger oldNumberOfRows = new AtomicInteger();
    final AtomicInteger newNumberOfRows = new AtomicInteger();
    synchronized (this.semaphor) {
      oldNumberOfRows.set(size());
      for (final T object : objects) {
        if (!this.indexByobjectMap.containsKey(object)) {
          this.indexByobjectMap.put(object, new HashSet<Integer>());
        }
        final Integer index = Integer.valueOf(this.getObjects().size());
        this.indexByobjectMap.get(object).add(index);
        this.objects.add(object);
      }
      newNumberOfRows.set(size());
    }
    fireObjectsAdded(new IntegerInterval(oldNumberOfRows.get(), newNumberOfRows.get() - 1), objects);
  }

  @Override
  public int[] indices(@SuppressWarnings("hiding") final Iterable<T> objects) {
    synchronized (this.semaphor) {
      final Set<Integer> indexes = new HashSet<>();
      for (final T object : objects) {
        final Set<Integer> objectIndexes = this.indexByobjectMap.get(object);
        indexes.addAll(objectIndexes);
      }
      return ArrayUtilities.primitives(indexes.toArray(new Integer[indexes.size()]));
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void remove(@SuppressWarnings("hiding") final T... objects) {
    final int[] indices = indices(Arrays.asList(objects));
    remove(indices);
  }

  @Override
  public void remove(@SuppressWarnings("hiding") final Iterable<T> objects) {
    final int[] indices = indices(objects);
    remove(indices);
  }

  @Override
  public void remove(final int... indices) {
    final AtomicReference<Set<Integer>> indiciesOfRemovedObjectsReference = new AtomicReference<>();
    final AtomicReference<Iterable<T>> removedObjectsReference = new AtomicReference<>();
    synchronized (this.semaphor) {
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
          //          final Set<Integer> indizes = this.indexByobjectMap.get(removedObject);
          //          if (indizes.remove(Integer.valueOf(index)) && indizes.isEmpty()) {
          //            this.indexByobjectMap.remove(object);
          //          }
          removedObjects.add(object);
        }
      }
      indiciesOfRemovedObjectsReference.set(indexSet);
      removedObjectsReference.set(removedObjects);
      refreshIndex();
    }
    fireObjectsRemoved(indiciesOfRemovedObjectsReference.get(), removedObjectsReference.get());
  }

  @Override
  public void removeAll() {
    final AtomicInteger oldNumberOfRows = new AtomicInteger();
    final AtomicReference<Iterable<T>> oldObjectsReference = new AtomicReference<>();
    synchronized (this.semaphor) {
      oldNumberOfRows.set(size());
      oldObjectsReference.set(values());
      this.objects.clear();
      this.indexByobjectMap.clear();
    }
    fireObjectsRemoved(new IntegerInterval(0, oldNumberOfRows.get() - 1), oldObjectsReference.get());
  }

  @Override
  public final Iterable<T> values() {
    synchronized (this.semaphor) {
      return Collections.unmodifiableList(IterableUtilities.asList(this.objects));
    }
  }

  @Override
  public final boolean isEmpty() {
    synchronized (this.semaphor) {
      return this.objects.isEmpty();
    }
  }

  @Override
  public final Collection<T> get(final int... indices) {
    synchronized (this.semaphor) {
      final List<T> result = new ArrayList<>();
      for (final int index : indices) {
        result.add(get(index));
      }
      return result;
    }
  }

  @Override
  public final T get(final int index) {
    synchronized (this.semaphor) {
      if (index == -1) {
        return null;
      }
      return this.objects.get(index);
    }
  }

  private List<T> getObjects() {
    return this.objects;
  }
}