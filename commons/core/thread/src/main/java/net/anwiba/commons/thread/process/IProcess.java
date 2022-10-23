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

import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.message.IMessageCollector;
import net.anwiba.commons.thread.cancel.ICanceler;

import java.util.concurrent.TimeUnit;

public interface IProcess {

  public void execute(IMessageCollector processMonitor, ICanceler canceler, IProcessIdentfier processIdentfier)
      throws CanceledException;

  public String getDescription();

  public String getQueueName();

  public boolean isCancelable();

  @SuppressWarnings("unused")
  default long getDelay(final TimeUnit unit) {
    return 0;
  }

  default boolean isPeriodic() {
    return false;
  }

  static ProcessBuilder builder() {
    return new ProcessBuilder();
  }
}
