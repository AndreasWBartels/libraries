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

import net.anwiba.commons.thread.cancel.ICanceler;

public class ProcessContext implements IProcessContext {

  private final IProcessMonitor monitor;
  private final ICanceler canceler;
  private final IProcessIdentfier processIdentfier;
  private final String description;
  private final String queueName;

  public ProcessContext(
      final IProcessIdentfier processIdentfier,
      final String description,
      final String queueName,
      final IProcessMonitor monitor,
      final ICanceler canceler) {
    this.processIdentfier = processIdentfier;
    this.description = description;
    this.queueName = queueName;
    this.monitor = monitor;
    this.canceler = canceler;
  }

  @Override
  public ICanceler getCanceler() {
    return this.canceler;
  }

  @Override
  public IProcessMonitor getMonitor() {
    return this.monitor;
  }

  @Override
  public String getQueueName() {
    return this.queueName;
  }

  @Override
  public String getProcessDescription() {
    return this.description;
  }

  @Override
  public IProcessIdentfier getProcessIdentfier() {
    return this.processIdentfier;
  }
}
