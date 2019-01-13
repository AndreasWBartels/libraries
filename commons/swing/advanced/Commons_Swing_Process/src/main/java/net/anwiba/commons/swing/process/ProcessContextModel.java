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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.thread.process.IProcessContext;
import net.anwiba.commons.thread.process.IProcessIdentfier;
import net.anwiba.commons.thread.process.IProcessMonitorListener;

public class ProcessContextModel {

  private String note;
  private final IProcessContext context;
  private final Object mutex = new Object();

  public ProcessContextModel(final IProcessContext context) {
    Ensure.ensureArgumentNotNull(context);
    this.context = context;
    context.getMonitor().addProcessMonitorListener(new IProcessMonitorListener() {

      @Override
      public void processNoteChanged(
          final IProcessIdentfier processIdentfier,
          final String processDescription,
          @SuppressWarnings("hiding") final String note) {
        setNote(note);
      }

      @Override
      public void processMessageAdded(
          final IProcessIdentfier processIdentfier,
          final String processDescription,
          final IMessage message) {
        final LocalDateTime time = LocalDateTime.now();
        final IProcessMessageContext messageContext = new IProcessMessageContext() {

          @Override
          public IProcessIdentfier getProcessIdentfier() {
            return processIdentfier;
          }

          @Override
          public String getProcessDescription() {
            return processDescription;
          }

          @Override
          public IMessage getMessage() {
            return message;
          }

          @Override
          public LocalDateTime getTime() {
            return time;
          }
        };
        addMessage(messageContext);
      }
    });
  }

  public String getDescription() {
    return this.context.getProcessDescription();
  }

  public boolean isEnabled() {
    return this.context.getCanceler().isEnabled() && !this.context.getCanceler().isCanceled();
  }

  public ICanceler getCanceler() {
    return this.context.getCanceler();
  }

  public void setNote(final String note) {
    synchronized (this.mutex) {
      if (ObjectUtilities.equals(note, this.note)) {
        return;
      }
      this.note = note;
    }
    fireNoteChanged(note);
  }

  public String getNote() {
    synchronized (this.mutex) {
      return this.note;
    }
  }

  public void addMessage(final IProcessMessageContext messageContext) {
    fireMessageAdded(messageContext);
  }

  private void fireMessageAdded(final IProcessMessageContext messageContext) {
    final List<IProcessModelListener> listeners = new ArrayList<>();
    synchronized (this.processModelListeners) {
      listeners.addAll(this.processModelListeners);
    }
    for (final IProcessModelListener listener : listeners) {
      listener.messageAdded(messageContext);
    }
  }

  private final List<IProcessModelListener> processModelListeners = new ArrayList<>();

  public final void addProcessModelListener(final IProcessModelListener listener) {
    synchronized (this.processModelListeners) {
      this.processModelListeners.add(listener);
    }
  }

  public final void removeProcessModelListener(final IProcessModelListener listener) {
    synchronized (this.processModelListeners) {
      this.processModelListeners.remove(listener);
    }
  }

  private final void fireNoteChanged(final String value) {
    final List<IProcessModelListener> listeners = new ArrayList<>();
    synchronized (this.processModelListeners) {
      listeners.addAll(this.processModelListeners);
    }
    for (final IProcessModelListener listener : listeners) {
      listener.noteChanged(value);
    }
  }
}
