/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2019 Andreas W. Bartels
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
package net.anwiba.commons.thread.monitor;

import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.IMessageCollector;
import net.anwiba.commons.message.Message;

public class ThreadMonitor {

  private class ThreadObject {

    private final long created;
    private final long id;

    public ThreadObject(
        final long id,
        final long created) {
      this.id = id;
      this.created = created;
    }

    public long getCreated() {
      return this.created;
    }

    public long getId() {
      return this.id;
    }

  }

  private final static ILogger logger = Logging.getLogger(ThreadMonitor.class.getName());

  private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
  private final Map<Long, ThreadObject> blockedThreads = new ConcurrentHashMap<Long, ThreadObject>();
  private final long limit;

  public ThreadMonitor(final long limit) {
    this.limit = limit;
  }

  public void run(final IMessageCollector collector) {
    final ThreadInfo[] threads = this.threadMXBean.dumpAllThreads(true, true);
    for (ThreadInfo thread : threads) {
      final Long threadId = Long.valueOf(thread.getThreadId());
      if (!Objects.equals(thread.getThreadState(), State.BLOCKED)) {
        this.blockedThreads.remove(threadId);
        continue;
      }
      final long now = System.currentTimeMillis();
      final ThreadObject oldInfo =
          this.blockedThreads.putIfAbsent(threadId, new ThreadObject(thread.getThreadId(), now));
      if (oldInfo == null) {
        continue;
      }
      final long created = oldInfo.getCreated();
      if (now - created < this.limit) {
        continue;
      }
      final String threadDump = getThreadDump(threads);
      if (!Objects.equals(thread.getThreadState(), State.BLOCKED)) {
        this.blockedThreads.remove(threadId);
        continue;
      }
      final IMessage message = Message.builder()
          .error()
          .text("Thread '" + thread.getThreadName() + "' bocked")
          .description(threadDump)
          .build();
      logger.warning(message.getText());
      logger.debug(message.getDescription());
      collector.addMessage(message);
      return;
    }

  }

  private static String getThreadDump(final ThreadInfo[] threads) {
    final StringBuilder out = new StringBuilder();
    for (ThreadInfo thread : threads) {
      out.append(String
          .format("name=%s | id=%d | prio=%d | state=%s",
              thread.getThreadName(),
              thread.getThreadId(),
              thread.getPriority(),
              thread.getThreadState()));
      out.append('\n');

      if (Objects.equals(thread.getThreadState(), State.BLOCKED)) {
        out.append(String
            .format("id=%d | count=%d | blocked=%d ms | lock=%s",
                thread.getLockOwnerId(),
                thread.getBlockedCount(),
                thread.getBlockedTime(),
                Optional.of(thread.getLockInfo()).convert(i -> i.getClassName()).getOr(() -> "Unkown")));
        out.append('\n');
      }
      for (MonitorInfo monitor : thread.getLockedMonitors()) {
        monitor.getClassName();
      }

      for (StackTraceElement element : thread.getStackTrace()) {
        out.append(element.toString()).append('\n');
      }
      out.append('\n');
    }
    return out.toString();
  }
}
