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
package net.anwiba.commons.process;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.process.cancel.ICanceler;
import net.anwiba.commons.process.cancel.ICancelerListener;
import net.anwiba.commons.process.queue.IRunnable;

public final class ProcessRunner implements IRunnable {

  private final static ILogger logger = Logging.getLogger(ProcessRunner.class.getName());

  private final IProcessList processes;
  private final IProcess process;
  private final IProcessMonitor monitor;
  private final IProcessIdentfier processIdentfier;
  private final ICancelerProvider cancelerProvider;

  private final List<ICancelerListener> listeners = new ArrayList<>();
  private ICanceler canceler;

  public ProcessRunner(
      final IProcessMonitor monitor,
      final ICancelerProvider cancelerProvider,
      final IProcessList processes,
      final IProcessIdentfier processIdentfier,
      final IProcess process) {
    this.cancelerProvider = cancelerProvider;
    this.processes = processes;
    this.processIdentfier = processIdentfier;
    this.process = process;
    this.monitor = monitor;
  }

  @Override
  public void run() {
    try {
      synchronized (this.listeners) {
        this.canceler = this.cancelerProvider.get();
        this.listeners.forEach(l -> this.canceler.addCancelerListener(l));
      }
      synchronized (this.processes) {
        this.processes.started(
            new ProcessContext(
                this.processIdentfier,
                this.process.getDescription(),
                this.process.getQueueName(),
                this.monitor,
                this.canceler));
      }
      logger.log(ILevel.DEBUG, MessageFormat.format("process {0} started", this.processIdentfier)); //$NON-NLS-1$
      this.process.execute(this.monitor, this.canceler, this.processIdentfier);
      logger.log(ILevel.DEBUG, MessageFormat.format("process {0} finished", this.processIdentfier)); //$NON-NLS-1$
    } catch (final InterruptedException exception) {
      logger.log(ILevel.DEBUG, MessageFormat.format("process {0} interrupted", this.processIdentfier)); //$NON-NLS-1$
    } catch (final RuntimeException throwable) {
      this.monitor.addMessage(
          new Message(this.process.getDescription(), throwable.getLocalizedMessage(), throwable, MessageType.ERROR));
      logger.log(ILevel.ERROR, "", throwable); //$NON-NLS-1$
    } finally {
      synchronized (this.listeners) {
        this.listeners.forEach(l -> this.canceler.removeCancelerListener(l));
        this.canceler = null;
      }
      synchronized (this.processes) {
        this.processes.finished(this.processIdentfier);
      }
    }
  }

  @Override
  public long getDelay(final TimeUnit unit) {
    return this.process.getDelay(unit);
  }

  @Override
  public boolean isPeriodic() {
    return this.process.isPeriodic();
  }

  @Override
  public boolean isCancelled() {
    if (this.canceler == null) {
      return false;
    }
    return this.canceler.isCanceled();
  }

  @Override
  public String toString() {
    return MessageFormat.format("Process {0}", this.processIdentfier); //$NON-NLS-1$
  }

  @Override
  public IProcessIdentfier getIdentifier() {
    return this.processIdentfier;
  }

  @Override
  public void addCancelerListener(final ICancelerListener listener) {
    synchronized (this.listeners) {
      if (this.canceler != null) {
        this.canceler.addCancelerListener(listener);
      }
      this.listeners.add(listener);
    }
  }

  @Override
  public void removeCancelerListener(final ICancelerListener listener) {
    synchronized (this.listeners) {
      if (this.canceler != null) {
        this.canceler.removeCancelerListener(listener);
      }
      this.listeners.remove(listener);
    }
  }

  @Override
  public boolean cancel(final boolean mayInterruptIfRunning) {
    if (this.canceler == null) {
      return true;
    }
    if (this.canceler.isEnabled() && mayInterruptIfRunning) {
      this.canceler.cancel();
    }
    return this.canceler.isCanceled();
  }

}