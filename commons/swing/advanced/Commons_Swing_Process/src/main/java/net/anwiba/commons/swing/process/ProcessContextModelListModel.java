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
package net.anwiba.commons.swing.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.anwiba.commons.model.AbstractListChangedNotifier;

public class ProcessContextModelListModel extends AbstractListChangedNotifier<ProcessContextModel> {

  List<ProcessContextModel> processes = new ArrayList<>();
  List<IProcessMessageContext> messages = new ArrayList<>();
  private final List<IProcessMessageListener> processMessageListeners = new ArrayList<>();
  private final IProcessModelListener processListener = new IProcessModelListener() {

    @Override
    public void noteChanged(final String value) {
      // nothing to do
    }

    @Override
    public void messageAdded(final IProcessMessageContext message) {
      addMessage(message);
    }
  };
  private final Object mutex = new Object();

  public void addMessage(final IProcessMessageContext message) {
    synchronized (this.mutex) {
      if (this.messages.contains(message)) {
        return;
      }
      this.messages.add(message);
    }
    fireMessageAdded(message);
  }

  public void removeMessage(final IProcessMessageContext message) {
    synchronized (this.mutex) {
      if (!this.messages.remove(message)) {
        return;
      }
    }
    fireMessageRemoved(message);
  }

  public final void addProcessContextModel(final ProcessContextModel process) {
    synchronized (this.mutex) {
      if (process == null || this.processes.contains(process)) {
        return;
      }
      process.addProcessModelListener(this.processListener);
      this.processes.add(process);
    }
    fireObjectsAdded(null, Arrays.asList(process));
  }

  public final void removeProcessContextModel(final ProcessContextModel process) {
    synchronized (this.mutex) {
      if (process == null || !this.processes.contains(process)) {
        return;
      }
      process.removeProcessModelListener(this.processListener);
      this.processes.remove(process);
    }
    fireObjectsRemoved(null, Arrays.asList(process));
  }

  public final synchronized ProcessContextModel[] getProcessModels() {
    synchronized (this.mutex) {
      return this.processes.toArray(new ProcessContextModel[this.processes.size()]);
    }
  }

  public final IProcessMessageContext[] getProcessContextMessages() {
    synchronized (this.mutex) {
      return this.messages.toArray(new IProcessMessageContext[this.messages.size()]);
    }
  }

  public final void addProcessMessageListener(final IProcessMessageListener listener) {
    synchronized (this.processMessageListeners) {
      this.processMessageListeners.add(listener);
    }
  }

  public final void removeProcessMessageListener(final IProcessMessageListener listener) {
    synchronized (this.processMessageListeners) {
      this.processMessageListeners.remove(listener);
    }
  }

  protected final void fireMessageAdded(final IProcessMessageContext message) {
    final List<IProcessMessageListener> listeners = new ArrayList<>();
    synchronized (this.processMessageListeners) {
      listeners.addAll(this.processMessageListeners);
    }
    for (final IProcessMessageListener listener : listeners) {
      listener.messageAdded(message);
    }
  }

  protected final void fireMessageRemoved(final IProcessMessageContext message) {
    final List<IProcessMessageListener> listeners = new ArrayList<>();
    synchronized (this.processMessageListeners) {
      listeners.addAll(this.processMessageListeners);
    }
    for (final IProcessMessageListener listener : listeners) {
      listener.messageRemoved(message);
    }
  }
}