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
package net.anwiba.commons.datasource.history;

import java.time.Duration;
import java.time.LocalDateTime;

import net.anwiba.commons.datasource.constaint.IResourceConstraintProvider;
import net.anwiba.commons.datasource.constaint.IResourceConstraintStorage;
import net.anwiba.commons.datasource.constaint.ResourceConstraint;
import net.anwiba.commons.datasource.resource.IResourceDescription;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.message.IMessageCollector;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.thread.process.AbstractProcess;
import net.anwiba.commons.thread.process.IProcessIdentfier;
import net.anwiba.commons.thread.process.IProcessManager;
import net.anwiba.commons.thread.queue.IQueueNameConstans;
import net.anwiba.commons.utilities.time.UserDateTimeUtilities;

public class DatasourceEventLogger implements IDatasourceEventLogger {

  private final IDatasourceEventStorage eventStorage;
  private final IResourceConstraintStorage constraintStorage;
  private final IResourceConstraintProvider constraintProvider;
  private final IProcessManager processManager;

  public DatasourceEventLogger(
      final IProcessManager processManager,
      final IDatasourceEventStorage eventStorage,
      final IResourceConstraintProvider constraintProvider,
      final IResourceConstraintStorage constraintStorage) {
    this.processManager = processManager;
    this.eventStorage = eventStorage;
    this.constraintProvider = constraintProvider;
    this.constraintStorage = constraintStorage;
  }

  @Override
  public void log(
      final String kind,
      final IResourceDescription sourceResourceDescription,
      final IResourceDescription targetResourceDescription,
      final String join,
      final String condition,
      final Long numberOfRows,
      final Duration duration) {
    final LocalDateTime date = UserDateTimeUtilities.now();
    this.processManager.execute(new AbstractProcess(IQueueNameConstans.AUDIT_LOGGING_QUEUE, "Audit logging", false) {

      @Override
      public void execute(
          final IMessageCollector processMonitor,
          final ICanceler canceler,
          final IProcessIdentfier processIdentfier)
          throws CanceledException {
        DatasourceEventLogger.this.eventStorage.save(
            new DatasourceEvent(
                kind,
                targetResourceDescription,
                sourceResourceDescription,
                date,
                duration,
                join,
                condition,
                numberOfRows));
        Optional.of(sourceResourceDescription).consume(
            d -> DatasourceEventLogger.this.constraintProvider
                .getConstaints(d)
                .stream()
                .convert(c -> ResourceConstraint.of(targetResourceDescription, c.getLicense(), c.getMaintainer()))
                .foreach(c -> DatasourceEventLogger.this.constraintStorage.save(c)));
      }
    });
  }
}
