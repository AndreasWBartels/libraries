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

import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.thread.process.IProcess;
import net.anwiba.commons.thread.process.IProcessGroup;
import net.anwiba.commons.thread.process.IProcessManager;

public class ProcessQueue implements IProcessQueue {

  private final IProcessManager processManager;

  public ProcessQueue(final IProcessManager processManager) {
    this.processManager = processManager;
  }

  @Override
  public synchronized void add(final ICanceler canceler, final IProcess process) {
    this.processManager.execute(canceler, process);
  }

  @Override
  public synchronized void add(final ICanceler canceler, final IProcessGroup processGroup) {
    for (final IProcess process : processGroup) {
      this.processManager.execute(canceler, process);
    }
  }
}