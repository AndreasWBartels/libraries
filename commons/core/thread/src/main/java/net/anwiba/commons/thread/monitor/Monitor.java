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
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.anwiba.commons.message.IMessageCollector;
import net.anwiba.commons.message.MessageBuilder;

public class Monitor {

  private class Foo {
    private final ThreadInfo info;
    private final long created;

    public Foo(final ThreadInfo info, final long created) {
      this.info = info;
      this.created = created;
    }

    public long getCreated() {
      return this.created;
    }

    public ThreadInfo getInfo() {
      return this.info;
    }
  }

  private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
  private final Map<Long, Foo> blockedThreads = new HashMap<Long, Foo>();
  private final long limit;

  public Monitor(final long limit) {
    this.limit = limit;
  }

  public void run(final IMessageCollector collector) {
    ThreadInfo[] infos = this.threadMXBean.dumpAllThreads(true, true);
    for (ThreadInfo info : infos) {
      Long threadId = Long.valueOf(info.getThreadId());
      State threadState = info.getThreadState();
      if (!Objects.equals(threadState, State.BLOCKED)) {
        this.blockedThreads.remove(threadId);
        continue;
      }
      final long now = System.currentTimeMillis();
      Foo oldInfo = this.blockedThreads.putIfAbsent(threadId, new Foo(info, now));
      if (oldInfo == null) {
        continue;
      }
//      final long blockedCount = oldInfo.getInfo().getBlockedCount();
//      if (blockedCount == info.getBlockedCount()) {
//        continue;
//      }
      final long created = oldInfo.getCreated();
      if (now - created < this.limit) {
        continue;
      }
      final String threadDump = getThreadDump();
      System.err.print(threadDump);
      collector.addMessage(new MessageBuilder().setError()
          .setText("Thread '" + info.getThreadName() + "' bocked")
          .setDescription(threadDump)
          .build());
      return;
    }

  }

  private static String getThreadDump() {
    Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
    StringBuilder out = new StringBuilder();
    for (Map.Entry<Thread, StackTraceElement[]> entry : allStackTraces.entrySet()) {
      Thread thread = entry.getKey();
      StackTraceElement[] elements = entry.getValue();
      out.append(String
          .format("%s | %d | prio=%d | %s", thread.getName(), thread.getId(), thread.getPriority(), thread.getState()));
      out.append('\n');

      for (StackTraceElement element : elements) {
        out.append(element.toString()).append('\n');
      }
      out.append('\n');
    }
    return out.toString();
  }
}
