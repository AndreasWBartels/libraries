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

public class Job implements IJob {

  private final IJobConfiguration configuration;

  public Job(final IJobConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void execute(final IMessageCollector monitor, final ICanceler canceler)
      throws ExcecutionFaildException,
      CanceledException {
    for (final IDescribedTask tasks : this.configuration.getSubTasks()) {
      if (canceler.isCanceled()) {
        return;
      }
      monitor.setNote(tasks.getDescription());
      tasks.run(monitor, canceler);
    }
  }

  @Override
  public String getTitle() {
    return this.configuration.getTitle();
  }

  @Override
  public String getDescription() {
    return this.configuration.getDescription();
  }

  @Override
  public String getText() {
    return this.configuration.getText();
  }
}
