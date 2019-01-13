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
package net.anwiba.commons.lang.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class AbstractObjectList<T> implements IMutableObjectList<T> {

  private final Object semaphor = new Object();
  private final List<T> objects = new ArrayList<>();

  public AbstractObjectList(final Collection<T> objects) {
    this.objects.addAll(objects);
  }

  @Override
  public Collection<T> toCollection() {
    synchronized (this.semaphor) {
      return Collections.unmodifiableCollection(this.objects);
    }
  }

  @Override
  public List<T> toList() {
    synchronized (this.semaphor) {
      return Collections.unmodifiableList(this.objects);
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
    synchronized (this.semaphor) {
      this.objects.clear();
      for (final T object : objects) {
        this.objects.add(object);
      }
    }
  }

  @Override
  public T set(final int index, final T object) {
    synchronized (this.semaphor) {
      if (!(index < size())) {
        throw new IllegalArgumentException("index out of bounds"); //$NON-NLS-1$
      }
      return this.objects.set(index, object);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public synchronized void add(@SuppressWarnings("hiding") final T... objects) {
    add(Arrays.asList(objects));
  }

  @Override
  public void add(@SuppressWarnings("hiding") final Iterable<T> objects) {
    synchronized (this.semaphor) {
      for (final T object : objects) {
        this.objects.add(object);
      }
    }
  }

  @Override
  public int[] indices(@SuppressWarnings("hiding") final Iterable<T> objects) {
    final Set<T> set = StreamSupport.stream(objects.spliterator(), false).collect(Collectors.toSet());
    final List<Integer> indexes = new LinkedList<>();
    synchronized (this.semaphor) {
      for (int j = 0; j < this.objects.size(); j++) {
        if (set.contains(this.objects.get(j))) {
          indexes.add(j);
        }
      }
      return indexes.stream().mapToInt(i -> i).toArray();
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
    synchronized (this.semaphor) {
      if (indices.length == 0) {
        return;
      }
      Arrays.sort(indices);
      final List<T> removedObjects = new ArrayList<>();
      for (int i = indices.length - 1; i >= 0; i--) {
        final int index = indices[i];
        final T object = get(index);
        final T removedObject = this.objects.remove(index);
        if (removedObject != null) {
          removedObjects.add(object);
        }
      }
    }
  }

  @Override
  public void removeAll() {
    synchronized (this.semaphor) {
      this.objects.clear();
    }
  }

  @Override
  public final IObjectIterable<T> values() {
    synchronized (this.semaphor) {
      return new ObjectList<>(Collections.unmodifiableList(this.objects.stream().collect(Collectors.toList())));
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

  @Override
  public IObjectIterator<T> iterator() {
    final Iterator<T> iterator = this.objects.iterator();
    return new IObjectIterator<T>() {

      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public T next() {
        return iterator.next();
      }
    };
  }
}
