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

import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.thread.cancel.ICancelerListener;
import net.anwiba.commons.thread.process.IProcessIdentfier;

public class WorkQueue implements IWorkQueue {

  public static final class ProcessScheduledFuture<V> implements RunnableScheduledFuture<V> {

    private final RunnableScheduledFuture<V> task;
    private final IRunnable runnable;

    public ProcessScheduledFuture(final RunnableScheduledFuture<V> task, final IRunnable runnable) {
      this.task = task;
      this.runnable = runnable;
    }

    public IProcessIdentfier getIdentifier() {
      return this.runnable.getIdentifier();
    }

    @Override
    public void run() {
      final ICancelerListener listner = new ICancelerListener() {

        @Override
        public void canceled() {
          if (ProcessScheduledFuture.this.task.isCancelled()) {
            return;
          }
          ProcessScheduledFuture.this.task.cancel(true);
        }
      };
      try {
        this.runnable.addCancelerListener(listner);
        this.task.run();
      } finally {
        this.runnable.removeCancelerListener(listner);
      }
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
      if (isCancelled()) {
        return true;
      }
      if (!this.runnable.isCancelled()) {
        this.runnable.cancel(mayInterruptIfRunning);
      }
      if (this.task.isCancelled()) {
        return true;
      }
      return this.task.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
      return this.runnable.isCancelled() && this.task.isCancelled();
    }

    @Override
    public boolean isDone() {
      return this.task.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
      return this.task.get();
    }

    @Override
    public V get(final long timeout, final TimeUnit unit)
        throws InterruptedException,
        ExecutionException,
        TimeoutException {
      return this.task.get(timeout, unit);
    }

    @Override
    public long getDelay(final TimeUnit unit) {
      return this.task.getDelay(unit);
    }

    @Override
    public int compareTo(final Delayed o) {
      return this.task.compareTo(o);
    }

    @Override
    public boolean isPeriodic() {
      return this.runnable.isPeriodic();
    }
  }

  public static enum QueueState {

    ACTIVE, SHUTDOWN, TERMINATING, TERMINATED;

    public static QueueState get(final ThreadPoolExecutor executor) {
      if (executor.isShutdown()) {
        return QueueState.SHUTDOWN;
      }
      if (executor.isTerminating()) {
        return QueueState.TERMINATING;
      }
      if (executor.isShutdown()) {
        return QueueState.TERMINATED;
      }
      return QueueState.ACTIVE;
    }

  }

  public static final class ExecutionHandler implements RejectedExecutionHandler {

    private final ILogger logger;
    private final String queueName;

    public ExecutionHandler(final ILogger logger, final String queueName) {
      this.logger = logger;
      this.queueName = queueName;
    }

    @SuppressWarnings("nls")
    public void execute(
        final ProcessScheduledFuture<?> future,
        final int poolSize,
        final int activeCount,
        final int queueSize,
        final QueueState queueState) {
      final String message = "Process "
          + future.getIdentifier().toString()
          + " finished from queue "
          + this.queueName
          + ", queue state is '"
          + queueState.name()
          + "', pool size : "
          + poolSize
          + ", active processes: "
          + activeCount
          + ", queued processes: "
          + queueSize;
      this.logger.log(ILevel.DEBUG, message);
    }

    @SuppressWarnings("nls")
    public void beforeExecute(
        final Thread thread,
        final ProcessScheduledFuture<?> future,
        final int poolSize,
        final int activeCount,
        final int queueSize,
        final QueueState queueState) {
      final String message = "Process "
          + future.getIdentifier().toString()
          + " started from queue "
          + this.queueName
          + ", task '"
          + thread.getName()
          + "', queue state is '"
          + queueState.name()
          + "', pool size : "
          + poolSize
          + ", active processes: "
          + activeCount
          + ", queued processes: "
          + queueSize;
      this.logger.log(ILevel.DEBUG, message);
    }

    @SuppressWarnings("nls")
    public void afterExecute(
        final ProcessScheduledFuture<?> future,
        final Throwable throwable,
        final int poolSize,
        final int activeCount,
        final int queueSize,
        final QueueState queueState) {
      final String message = "Process "
          + future.getIdentifier().toString()
          + " finished from queue "
          + this.queueName
          + ", queue state is '"
          + queueState.name()
          + "', pool size : "
          + poolSize
          + ", active processes: "
          + activeCount
          + ", queued processes: "
          + queueSize;
      this.logger.log(ILevel.DEBUG, message, throwable);
    }

    public String getIdentifierString(final IRunnable runnable) {
      if (runnable == null) {
        return "-"; //$NON-NLS-1$
      }
      return runnable.getIdentifier().toString();
    }

    @SuppressWarnings("nls")
    private void rejectedExecution(
        final ProcessScheduledFuture<?> future,
        final int poolSize,
        final int activeCount,
        final int queueSize,
        final QueueState queueState) {
      final String message = "Process "
          + future.toString()
          + " rejected from queue "
          + this.queueName
          + ", queue state is '"
          + queueState.name()
          + "', pool size : "
          + poolSize
          + ", active processes: "
          + activeCount
          + ", queued processes: "
          + queueSize;
      this.logger.log(ILevel.DEBUG, message);
      throw new RejectedExecutionException(message);
    }

    @Override
    public void rejectedExecution(final Runnable runnable, final ThreadPoolExecutor executor) {
      rejectedExecution(
          (ProcessScheduledFuture<?>) runnable,
          executor.getPoolSize(),
          executor.getActiveCount(),
          executor.getQueue().size(),
          QueueState.get(executor));
    }
  }

  private final ScheduledThreadPoolExecutor executor;

  public static IWorkQueue create(
      final ILogger logger,
      final String queueName,
      final int threadCount,
      final boolean asDaemon,
      final int priority) {
    final ThreadFactory factory = new ThreadFactory() {

      AtomicInteger counter = new AtomicInteger();
      ThreadFactory threadFactory = Executors.defaultThreadFactory();

      @Override
      public Thread newThread(final Runnable runnable) {
        final Thread thread = this.threadFactory.newThread(runnable);
        thread.setName(queueName + "-T-" + this.counter.getAndIncrement()); //$NON-NLS-1$
        thread.setPriority(priority);
        thread.setDaemon(asDaemon);
        return thread;
      }
    };
    final ExecutionHandler handler = new ExecutionHandler(logger, queueName);
    final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(threadCount, factory, handler) {

      @Override
      protected <V> RunnableScheduledFuture<V> decorateTask(
          final Runnable runnable,
          final RunnableScheduledFuture<V> task) {
        return new ProcessScheduledFuture<>(task, (IRunnable) runnable);
      }

      @Override
      protected void beforeExecute(final Thread thread, final Runnable runnable) {
        super.beforeExecute(thread, runnable);
        final ProcessScheduledFuture<?> future = (ProcessScheduledFuture<?>) runnable;
        handler.beforeExecute(thread, future, getPoolSize(), getActiveCount(), getQueue().size(), QueueState.get(this));
      }

      @Override
      public ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
        final ProcessScheduledFuture<?> future = (ProcessScheduledFuture<?>) super.schedule(command, delay, unit);
        handler.execute(future, getPoolSize(), getActiveCount(), getQueue().size(), QueueState.get(this));
        return future;
      }

      @Override
      protected void afterExecute(final Runnable runnable, final Throwable throwable) {
        super.afterExecute(runnable, throwable);
        final ProcessScheduledFuture<?> future = (ProcessScheduledFuture<?>) runnable;
        handler
            .afterExecute(future, throwable, getPoolSize(), getActiveCount(), getQueue().size(), QueueState.get(this));
      }

    };
    return new WorkQueue(executor);
  }

  public WorkQueue(final ScheduledThreadPoolExecutor executor) {
    this.executor = executor;
  }

  @Override
  public ICancelableRunnable execute(final IRunnable runnable) throws IllegalStateException {
    try {
      final long delay = runnable.getDelay(TimeUnit.MILLISECONDS);
      if (runnable.isPeriodic()) {
        final ProcessScheduledFuture<?> scheduledFuture = (ProcessScheduledFuture<?>) this.executor
            .scheduleAtFixedRate(runnable, delay, delay, TimeUnit.MILLISECONDS);
        return new ICancelableRunnable() {

          @Override
          public IProcessIdentfier getIdentifier() {
            return scheduledFuture.getIdentifier();
          }

          @Override
          public boolean isCanceled() {
            return scheduledFuture.isCancelled();
          }

          @Override
          public boolean isCancel() {
            return scheduledFuture.cancel(true);
          }
        };
      }
      final ProcessScheduledFuture<?> scheduledFuture = (ProcessScheduledFuture<?>) this.executor
          .schedule(runnable, delay, TimeUnit.MILLISECONDS);
      return new ICancelableRunnable() {

        @Override
        public IProcessIdentfier getIdentifier() {
          return scheduledFuture.getIdentifier();
        }

        @Override
        public boolean isCanceled() {
          return scheduledFuture.isCancelled();
        }

        @Override
        public boolean isCancel() {
          return scheduledFuture.cancel(true);
        }
      };
    } catch (final RejectedExecutionException exception) {
      throw new IllegalStateException(exception);
    }
  }

  @Override
  public void shutdown() {
    this.executor.shutdown();
  }

  @Override
  public void waitForWorkQueueFinished(final long timeout) {
    try {
      this.executor.awaitTermination(timeout, TimeUnit.SECONDS);
    } catch (final InterruptedException exception) {
      this.executor.shutdownNow();
    }
  }

  @Override
  public void remove(final IProcessIdentfier identifier) {
    final List<ProcessScheduledFuture<?>> futures = this.executor
        .getQueue()
        .stream()
        .map(r -> (ProcessScheduledFuture<?>) r)
        .collect(Collectors.toList());
    for (final ProcessScheduledFuture<?> future : futures) {
      if (future.getIdentifier().equals(identifier)) {
        this.executor.getQueue().remove(future);
        return;
      }
    }
  }

  @Override
  public void cancel(final IProcessIdentfier identifier) {
    final List<ProcessScheduledFuture<?>> futures = this.executor
        .getQueue()
        .stream()
        .map(r -> (ProcessScheduledFuture<?>) r)
        .collect(Collectors.toList());
    for (final ProcessScheduledFuture<?> future : futures) {
      if (future.getIdentifier().equals(identifier)) {
        future.cancel(true);
        return;
      }
    }
  }

}
