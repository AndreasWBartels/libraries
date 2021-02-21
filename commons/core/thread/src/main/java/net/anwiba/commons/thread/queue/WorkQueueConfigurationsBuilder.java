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

import java.util.HashMap;
import java.util.Map;

public class WorkQueueConfigurationsBuilder {

  private final IWorkQueueConfiguration defauftConfiguration;
  private final Map<String, IWorkQueueConfiguration> configurations = new HashMap<>();

  public WorkQueueConfigurationsBuilder(final int defaultNumberOfThreads, final int defaultThreadPriority) {
    this.defauftConfiguration = new WorkQueueConfiguration(defaultNumberOfThreads, defaultThreadPriority);
  }

  public WorkQueueConfigurationsBuilder addConfiguration(
      final String queueName,
      final int numberOfThreads,
      final int priority) {
    this.configurations.put(queueName, new WorkQueueConfiguration(numberOfThreads, priority));
    return this;
  }

  public IWorkQueueConfigurations build() {
    return new WorkQueueConfigurations(this.defauftConfiguration, this.configurations);
  }

}
