/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.message.IMessageCollector;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.thread.queue.IQueueNameConstans;

public class ProcessBuilder {

  private String queueName = IQueueNameConstans.METADATA_IO_QUEUE;
  private String description = ""; //$NON-NLS-1$
  private boolean isCancelable = true;
  private IProcessExecutable executable = (m, c, i) -> {};
  private Duration delay = null;

  public ProcessBuilder setQueueName(final String queueName) {
    this.queueName = queueName;
    return this;
  }

  public ProcessBuilder setDescription(final String description) {
    this.description = description;
    return this;
  }

  public ProcessBuilder setExecutable(final IProcessExecutable executable) {
    this.executable = executable;
    return this;
  }

  public ProcessBuilder setCancelable(final boolean isCancelable) {
    this.isCancelable = isCancelable;
    return this;
  }
  
  public ProcessBuilder setDelay(final Duration delay) {
    this.delay = delay;
    return this;
  }

  public IProcess build() {
    return new AbstractProcess(this.queueName, this.description, this.isCancelable) {

      @Override
      public boolean isPeriodic() {
        return ProcessBuilder.this.delay != null && !Duration.ZERO.equals(ProcessBuilder.this.delay);
      }

      @Override
      public long getDelay(final TimeUnit unit) {
        return ProcessBuilder.this.delay == null ? 0 : unit.convert(ProcessBuilder.this.delay);
      }

      @Override
      public void execute(
          final IMessageCollector processMonitor,
          final ICanceler canceler,
          final IProcessIdentfier processIdentfier)
          throws CanceledException {
        ProcessBuilder.this.executable.execute(processMonitor, canceler, processIdentfier);
      }
    };
  }

}
