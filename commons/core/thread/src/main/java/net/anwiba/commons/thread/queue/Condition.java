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

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;

public class Condition {

  private final ILogger logger;
  private boolean value;

  public Condition(final ILogger logger, final boolean value) {
    Ensure.ensureArgumentNotNull(logger);
    this.logger = logger;
    this.value = value;
  }

  public synchronized boolean isTrue() {
    return this.value;
  }

  public synchronized void setFalse() {
    this.logger.log(ILevel.DEBUG, "setting condition to false"); //$NON-NLS-1$
    this.value = false;
  }

  public synchronized void setTrue() {
    this.logger.log(ILevel.DEBUG, "setting condition to true"); //$NON-NLS-1$
    this.value = true;
    notifyAll();
  }

  public synchronized void releaseAll() {
    notifyAll();
    this.logger.log(ILevel.DEBUG, "released all"); //$NON-NLS-1$
  }

  public synchronized void releaseOne() {
    notify();
    this.logger.log(ILevel.DEBUG, "released one"); //$NON-NLS-1$
  }

  public synchronized void waitForTrue(final long timeout) throws InterruptedException {
    if (!this.value) {
      this.logger.log(ILevel.DEBUG, "waiting to become true"); //$NON-NLS-1$
      wait(timeout);
    }
    if (this.value) {
      this.logger.log(ILevel.DEBUG, "now true"); //$NON-NLS-1$
    } else {
      this.logger.log(ILevel.WARNING, "timed out, condition is still false!"); //$NON-NLS-1$
    }
  }

  public synchronized void waitForTrue() throws InterruptedException {
    waitForTrue(0);
  }
}