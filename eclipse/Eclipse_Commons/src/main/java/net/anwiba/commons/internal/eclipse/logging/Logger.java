/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2017 Andreas Bartels
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
package net.anwiba.commons.internal.eclipse.logging;

import net.anwiba.commons.eclipse.logging.ILevel;
import net.anwiba.commons.eclipse.logging.ILogger;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;

public class Logger implements ILogger {

  private final ILog log;
  private final String pluginId;

  public Logger(final ILog log, final String pluginId) {
    this.log = log;
    this.pluginId = pluginId;
  }

  @Override
  public void log(final ILevel level, final String message) {
    this.log.log(new Status(level.getCode(), this.pluginId, message));
  }

  @Override
  public void log(final ILevel level, final Throwable throwable) {
    log(level, throwable.getLocalizedMessage(), throwable);
  }

  @Override
  public void log(final ILevel level, final String message, final Throwable throwable) {
    this.log.log(new Status(level.getCode(), this.pluginId, message, throwable));
  }

}
