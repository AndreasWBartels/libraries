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
import java.util.List;

public abstract class AbstractObjectChangedNotifier implements IObjectChangedNotifier {

  private final List<IChangeableObjectListener> listeners = new ArrayList<>();

  @Override
  public final void addChangeListener(final IChangeableObjectListener listener) {
    synchronized (this.listeners) {
      this.listeners.add(listener);
    }
  }

  @Override
  public final synchronized void removeChangeListener(final IChangeableObjectListener listener) {
    synchronized (this.listeners) {
      this.listeners.remove(listener);
    }
  }

  protected final synchronized void fireObjectChanged() {
    final List<IChangeableObjectListener> currentListeners;
    synchronized (this.listeners) {
      currentListeners = new ArrayList<>(this.listeners);
    }
    for (final IChangeableObjectListener listener : currentListeners) {
      listener.objectChanged();
    }
  }
}