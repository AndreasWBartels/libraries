/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.commons.message.observer;

import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.message.IMessage;

public final class ObservableMessageCollector implements IObservableMessageCollector {
  @Override
  public void setNote(final String note) {
    // TODO Auto-generated method stub

  }

  @Override
  public void addMessage(final IMessage message) {
    fireMessageAdded(message);
  }

  private final List<IMessageAddedListener> listeners = new ArrayList<>();

  @Override
  public void addMessageAddedListener(final IMessageAddedListener listener) {
    synchronized (this.listeners) {
      this.listeners.add(listener);
    }
  }

  @Override
  public void removeMessageAddedListener(final IMessageAddedListener listener) {
    synchronized (this.listeners) {
      this.listeners.remove(listener);
    }
  }

  protected final void fireMessageAdded(final IMessage message) {
    final List<IMessageAddedListener> currentListeners;
    synchronized (this.listeners) {
      currentListeners = new ArrayList<>(this.listeners);
    }
    for (final IMessageAddedListener listener : currentListeners) {
      listener.messageAdded(message);
    }
  }
}