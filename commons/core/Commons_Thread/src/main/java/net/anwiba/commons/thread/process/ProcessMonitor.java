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
package net.anwiba.commons.thread.process;

import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.message.IMessage;

public class ProcessMonitor implements IProcessMonitor {

  private final List<IProcessMonitorListener> processMonitorListeners = new ArrayList<>();
  private final String processDescription;
  private final IProcessIdentfier processIdentfier;

  public ProcessMonitor(final IProcessIdentfier processIdentfier, final String processDescription) {
    this.processIdentfier = processIdentfier;
    this.processDescription = processDescription;
  }

  @Override
  public void setNote(final String note) {
    fireNoteChanged(note);
  }

  @Override
  public void removeProcessMonitorListener(final IProcessMonitorListener listener) {
    synchronized (this.processMonitorListeners) {
      this.processMonitorListeners.remove(listener);
    }
  }

  @Override
  public void addProcessMonitorListener(final IProcessMonitorListener listener) {
    synchronized (this.processMonitorListeners) {
      this.processMonitorListeners.add(listener);
    }
  }

  private void fireNoteChanged(final String note) {
    final List<IProcessMonitorListener> listeners = new ArrayList<>();
    synchronized (this.processMonitorListeners) {
      listeners.addAll(this.processMonitorListeners);
    }
    for (final IProcessMonitorListener listener : listeners) {
      listener.processNoteChanged(this.processIdentfier, this.processDescription, note);
    }
  }

  @Override
  public void addMessage(final IMessage message) {
    fireMessageAdded(message);
  }

  private void fireMessageAdded(final IMessage message) {
    final List<IProcessMonitorListener> listeners = new ArrayList<>();
    synchronized (this.processMonitorListeners) {
      listeners.addAll(this.processMonitorListeners);
    }
    for (final IProcessMonitorListener listener : listeners) {
      listener.processMessageAdded(this.processIdentfier, this.processDescription, message);
    }
  }
}