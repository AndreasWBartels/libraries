/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
package net.anwiba.commons.thread.job;

import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.message.IMessageCollector;
import net.anwiba.commons.thread.cancel.ICanceler;

public class DescribedTaskBuilder {

  private String description = "note"; //$NON-NLS-1$
  private ITask task = new ITask() {

    @Override
    public void run(final IMessageCollector progressMonitor, final ICanceler canceler)
        throws CanceledException,
          RuntimeException {
    }
  };

  public IDescribedTask build() {
    return new IDescribedTask() {

      @Override
      public void run(final IMessageCollector progressMonitor, final ICanceler canceler)
          throws CanceledException,
            ExcecutionFaildException {
        DescribedTaskBuilder.this.task.run(progressMonitor, canceler);
      }

      @Override
      public String getDescription() {
        return DescribedTaskBuilder.this.description;
      }

    };
  }

  public DescribedTaskBuilder setDescription(final String description) {
    this.description = description;
    return this;
  }

  public DescribedTaskBuilder setTask(final ITask task) {
    this.task = task;
    return this;
  }

}
