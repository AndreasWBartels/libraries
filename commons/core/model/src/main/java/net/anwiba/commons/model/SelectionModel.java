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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package net.anwiba.commons.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.utilities.collection.IterableUtilities;
import net.anwiba.commons.utilities.collection.SetUtilities;

public class SelectionModel<T> implements ISelectionModel<T> {

  private final List<ISelectionListener<T>> listeners = new ArrayList<>();
  private final Set<T> selectedObjects = new HashSet<>();

  @Override
  public void setSelectedObject(final T object) {
    boolean changed = false;
    synchronized (this.selectedObjects) {
      if (this.selectedObjects.size() == 1 && isSelected(object)) {
        return;
      }
      if (!this.selectedObjects.isEmpty()) {
        clear();
        changed = true;
      }
      if (object != null) {
        changed |= this.selectedObjects.add(object);
      }
    }
    if (changed) {
      fireSelectionChanged();
    }
  }

  @Override
  public void setSelectedObjects(final Collection<T> objects) {
    boolean changed = false;
    synchronized (this.selectedObjects) {
      if (SetUtilities.equals(this.selectedObjects, objects)) {
        return;
      }
      if (!this.selectedObjects.isEmpty()) {
        clear();
        changed = true;
      }
      if (!objects.isEmpty()) {
        changed |= this.selectedObjects.addAll(objects);
      }
    }
    if (changed) {
      fireSelectionChanged();
    }
  }

  @Override
  public void addSelectedObject(final T object) {
    boolean changed = false;
    synchronized (this.selectedObjects) {
      if (object == null || isSelected(object)) {
        return;
      }
      changed = this.selectedObjects.add(object);
    }
    if (changed) {
      fireSelectionChanged();
    }
  }

  @Override
  public void addSelectedObjects(final Collection<T> objects) {
    boolean changed = false;
    synchronized (this.selectedObjects) {
      if (objects == null || objects.isEmpty()) {
        return;
      }
      final Set<T> diverence = SetUtilities
          .getContainsNot(new HashSet<>(this.selectedObjects), objects);
      changed = this.selectedObjects.addAll(diverence);
    }
    if (changed) {
      fireSelectionChanged();
    }
  }

  @Override
  public boolean isSelected(final T object) {
    synchronized (this.selectedObjects) {
      return this.selectedObjects.contains(object);
    }
  }

  @Override
  public void removeSelectedObject(final T object) {
    boolean removed = false;
    synchronized (this.selectedObjects) {
      if (object == null || !isSelected(object)) {
        return;
      }
      removed = this.selectedObjects.remove(object);
    }
    if (removed) {
      fireSelectionChanged();
    }
  }

  @Override
  public void removeSelectedObjects(final Collection<T> objects) {
    boolean removed = false;
    synchronized (this.selectedObjects) {
      if (objects == null || objects.isEmpty()) {
        return;
      }
      removed = this.selectedObjects.removeAll(objects);
    }
    if (removed) {
      fireSelectionChanged();
    }
  }

  @Override
  public void removeAllSelectedObjects() {
    synchronized (this.selectedObjects) {
      if (this.selectedObjects.isEmpty()) {
        return;
      }
      clear();
    }
    fireSelectionChanged();
  }

  @Override
  public boolean isEmpty() {
    synchronized (this.selectedObjects) {
      return this.selectedObjects.isEmpty();
    }
  }

  @Override
  public int size() {
    synchronized (this.selectedObjects) {
      return this.selectedObjects.size();
    }
  }

  private void clear() {
    synchronized (this.selectedObjects) {
      this.selectedObjects.clear();
    }
  }

  @Override
  public void addSelectionListener(final ISelectionListener<T> listener) {
    synchronized (this.listeners) {
      if (this.listeners.contains(listener)) {
        return;
      }
      this.listeners.add(listener);
    }
  }

  @Override
  public void removeSelectionListener(final ISelectionListener<T> listener) {
    synchronized (this.listeners) {
      this.listeners.remove(listener);
    }
  }

  public void removeSelectionListeners() {
    synchronized (this.listeners) {
      this.listeners.clear();
    }
  }

  private void fireSelectionChanged() {
    final List<ISelectionListener<T>> currentListeners;
    synchronized (this.listeners) {
      currentListeners = new ArrayList<>(this.listeners);
    }
    for (final ISelectionListener<T> listener : currentListeners) {
      listener.selectionChanged(new SelectionEvent<>(this));
    }
  }

  @Override
  public Iterable<T> getSelectedObjects() {
    synchronized (this.selectedObjects) {
      return IterableUtilities.asList(this.selectedObjects);
    }
  }

  @Override
  public IOptional<T, RuntimeException> optional() {
    synchronized (this.selectedObjects) {
      if (isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(this.selectedObjects.iterator().next());
    }
  }
}