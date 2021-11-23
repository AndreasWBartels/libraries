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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.functional.IProcedure;

public class ListenerList<L> {
  private final List<L> listeners;

  public ListenerList() {
    this(new ArrayList<L>());
  }

  private ListenerList(final List<L> listeners) {
    Ensure.ensureArgumentNotNull(listeners);
    this.listeners = listeners;
  }

  public synchronized void add(final L listener) {
    Ensure.ensureArgumentNotNull(listener);
    this.listeners.add(listener);
  }

  public synchronized void remove(final L listener) {
    this.listeners.remove(listener);
  }

  public void forAllDo(final IProcedure<L, RuntimeException> procedure) {
    final Collection<L> cloneList;
    synchronized (this) {
      cloneList = new ArrayList<>(this.listeners);
    }
    for (final L listener : cloneList) {
      procedure.execute(listener);
    }
  }

  public void forAllDoLastListenerFirst(final IProcedure<L, RuntimeException> procedure) {
    final List<L> cloneList;
    synchronized (this) {
      cloneList = new ArrayList<>(this.listeners);
    }
    Collections.reverse(cloneList);
    for (final L listener : cloneList) {
      procedure.execute(listener);
    }
  }

  public synchronized ListenerList<L> getClone() {
    return new ListenerList<>(new ArrayList<>(this.listeners));
  }

  public synchronized boolean contains(final L listener) {
    return this.listeners.contains(listener);
  }

  public synchronized void clear() {
    this.listeners.clear();
  }

  public synchronized boolean isEmpty() {
    return this.listeners.isEmpty();
  }

  public synchronized int getSize() {
    return this.listeners.size();
  }
}