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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.thread.cancel.Canceler;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.thread.cancel.ICancelerProvider;
import net.anwiba.commons.thread.queue.IWorkQueue;
import net.anwiba.commons.thread.queue.IWorkQueueFactory;

public class ProcessManager implements IProcessManager, IProcessList {

  private static ILogger logger = Logging.getLogger(ProcessManager.class.getName());
  private final Set<IProcessIdentfier> processes = new HashSet<>();
  private final List<IProcessListener> processManagerListeners = new ArrayList<>();
  private final Map<String, IWorkQueue> workQueues = new HashMap<>();
  private final IWorkQueueFactory workQueueFactory;
  private final Object mutex = new Object();

  public ProcessManager(final IWorkQueueFactory workQueuesFactory) {
    this.workQueueFactory = workQueuesFactory;
  }

  @Override
  public void execute(final IProcess process) {
    execute(() -> new Canceler(process.isCancelable()), process);
  }

  @Override
  public void execute(final ICanceler canceler, final IProcess process) {
    if (process.isPeriodic()) {
      throw new IllegalStateException();
    }
    execute(() -> canceler, process);
  }

  private IProcessIdentfier next() {
    IProcessIdentfier nextId = null;
    do {
      nextId = ProcessSequencer.getNextId();
    } while (this.processes.contains(nextId));
    return nextId;
  }

  private void execute(final ICancelerProvider cancelerProvider, final IProcess process) {
    final IWorkQueue workQueue;
    synchronized (this) {
      if (!this.workQueues.containsKey(process.getQueueName())) {
        this.workQueues.put(process.getQueueName(), this.workQueueFactory.create(logger, process.getQueueName()));
      }
      workQueue = this.workQueues.get(process.getQueueName());
    }
    final IProcessIdentfier processIdentfier = next();
    final IProcessMonitor monitor = new ProcessMonitor(processIdentfier, process.getDescription());
    workQueue.execute(new ProcessRunner(monitor, cancelerProvider, this, processIdentfier, process));
  }

  void fireProcessStarted(final IProcessContext context) {
    final List<IProcessListener> listeners = new ArrayList<>();
    synchronized (this.processManagerListeners) {
      listeners.addAll(this.processManagerListeners);
    }
    for (final IProcessListener listener : listeners) {
      listener.processStarted(context);
    }
  }

  void fireProcessFinished(final IProcessIdentfier processIdentfier) {
    final List<IProcessListener> listeners = new ArrayList<>();
    synchronized (this.processManagerListeners) {
      listeners.addAll(this.processManagerListeners);
    }
    for (final IProcessListener listener : listeners) {
      listener.processFinished(processIdentfier);
      if (this.processes.size() == 0) {
        listener.allProgressesFinished();
      }
    }
  }

  @Override
  public void addProcessListener(final IProcessListener listener) {
    synchronized (this.processManagerListeners) {
      this.processManagerListeners.add(listener);
    }
  }

  @Override
  public void removeProgressListener(final IProcessListener listener) {
    synchronized (this.processManagerListeners) {
      this.processManagerListeners.remove(listener);
    }
  }

  @Override
  public void started(final IProcessContext context) {
    synchronized (this.mutex) {
      this.processes.add(context.getProcessIdentfier());
    }
    fireProcessStarted(context);
  }

  @Override
  public void finished(final IProcessIdentfier processIdentfier) {
    synchronized (this.mutex) {
      this.processes.remove(processIdentfier);
    }
    fireProcessFinished(processIdentfier);
  }

  @Override
  public boolean isEmpty() {
    synchronized (this.mutex) {
      return this.processes.isEmpty();
    }
  }

  @Override
  public void shutdown() {
    synchronized (this) {
      for (final IWorkQueue workQueue : this.workQueues.values()) {
        workQueue.shutdown();
      }
    }
  }

  @Override
  public void remove(final IProcessIdentfier identfier) {
    this.workQueues.values().forEach(q -> q.remove(identfier));
  }
}