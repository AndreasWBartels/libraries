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
package net.anwiba.commons.thread.queue;

import java.text.MessageFormat;
import java.util.LinkedList;

import net.anwiba.commons.lang.counter.ICounter;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;

public final class PoolWorker extends Thread {

  private final LinkedList<IRunnable> queue;
  private final ILogger logger;
  private final Condition acceptingWork;
  private final ICounter activeWorkersCounter;
  private final Condition workQueueFinished;

  PoolWorker(
      final ILogger logger,
      final ThreadGroup threadGroup,
      final String threadName,
      final ICounter activeWorkersCounter,
      final LinkedList<IRunnable> queue,
      final Condition acceptingWork,
      final Condition workQueueFinished) {
    super(threadGroup, threadName);
    this.logger = logger;
    this.activeWorkersCounter = activeWorkersCounter;
    this.queue = queue;
    this.acceptingWork = acceptingWork;
    this.workQueueFinished = workQueueFinished;
    final boolean asDeamon = threadGroup.isDaemon();
    setDaemon(asDeamon);
  }

  @Override
  public void run() {
    while (this.acceptingWork.isTrue()) {
      final IRunnable runnable = next();
      if (runnable != null) {
        execute(runnable);
      }
    }
  }

  private void execute(final IRunnable runnable) {
    try {
      this.activeWorkersCounter.increment();
      runnable.run();
    } catch (final Throwable e) {
      this.logger.log(
          ILevel.ERROR,
          MessageFormat.format(
              "{0}: Error on Runnable[{1} {2}].", //$NON-NLS-1$
              getName(),
              runnable.getClass().getName(),
              runnable.getIdentifier()),
          e);
    } finally {
      this.activeWorkersCounter.decrement();
      checkWorkQueueFinished();
    }
  }

  private IRunnable next() {
    final IRunnable runnable;
    final int queueSize;
    synchronized (this.queue) {
      while (this.queue.isEmpty()) {
        try {
          if (!this.acceptingWork.isTrue()) {
            this.logger.log(ILevel.DEBUG, getName() + ": Ending PoolWorker"); //$NON-NLS-1$
            checkWorkQueueFinished();
            return null;
          }
          this.queue.wait();
        } catch (final InterruptedException exception) {
          this.logger.log(ILevel.DEBUG, getName() + ": Interupted"); //$NON-NLS-1$
        }
      }
      runnable = this.queue.removeFirst();
      queueSize = this.queue.size();
    }
    if (this.logger.isLoggable(ILevel.DEBUG)) {
      this.logger.log(
          ILevel.DEBUG,
          MessageFormat.format(
              "{0}: dequeued Process {2} Runnable[{1}]. Current size of queue: {3}", //$NON-NLS-1$
              getName(),
              runnable.getClass().getName(),
              runnable.getIdentifier(),
              String.valueOf(queueSize)));
    }
    return runnable;
  }

  private void checkWorkQueueFinished() {
    if (this.acceptingWork.isTrue()) {
      if (this.activeWorkersCounter.value() == 0 && this.queue.isEmpty()) {
        this.workQueueFinished.setTrue();
      }
    }
  }
}