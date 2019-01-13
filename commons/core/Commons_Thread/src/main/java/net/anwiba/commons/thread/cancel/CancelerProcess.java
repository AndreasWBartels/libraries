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
package net.anwiba.commons.thread.cancel;

import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.message.IMessageCollector;
import net.anwiba.commons.thread.process.IProcess;
import net.anwiba.commons.thread.process.IProcessIdentfier;
import net.anwiba.commons.thread.queue.IQueueNameConstans;

public final class CancelerProcess implements IProcess {

  private final ICanceler canceler;
  private final String descrition;

  public CancelerProcess(final ICanceler canceler, final String descrition) {
    this.canceler = canceler;
    this.descrition = descrition;
  }

  @Override
  public String getQueueName() {
    return IQueueNameConstans.CANCEL_QUEUE;
  }

  @Override
  public String getDescription() {
    return "Cancel process: " + this.descrition; //$NON-NLS-1$
  }

  @Override
  public void execute(
      final IMessageCollector processMonitor,
      @SuppressWarnings("hiding") final ICanceler canceler,
      final IProcessIdentfier processIdentfier)
      throws CanceledException {
    this.canceler.cancel();
  }

  @Override
  public boolean isCancelable() {
    return false;
  }
}
