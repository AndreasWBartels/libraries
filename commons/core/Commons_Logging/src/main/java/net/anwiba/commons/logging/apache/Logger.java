/*
 * #%L
 * anwiba commons core
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
package net.anwiba.commons.logging.apache;

import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.IMessageFactory;
import net.anwiba.commons.logging.LogLevel;

import java.util.logging.Level;

import org.apache.commons.logging.Log;

public final class Logger implements ILogger {

  private final Log log;

  public Logger(final Log log) {
    this.log = log;
  }

  @Override
  public void log(final Level level, final String message, final Throwable throwable) {
    switch (LogLevel.byLevel(level)) {
      case ALL: {
        this.log.trace(message, throwable);
        return;
      }
      case DEBUG:
      case FINEST:
      case FINER: {
        this.log.debug(message, throwable);
        return;
      }
      case FINE:
      case CONFIG:
      case INFO: {
        this.log.info(message, throwable);
        return;
      }
      case WARNING: {
        this.log.warn(message, throwable);
        return;
      }
      case ERROR: {
        this.log.error(message, throwable);
        return;
      }
      case SEVERE: {
        this.log.fatal(message, throwable);
        return;
      }
      case OFF: {
        return;
      }
    }
    throw new RuntimeException("Unreachable code reached"); //$NON-NLS-1$
  }

  @Override
  public void log(final Level level, final String message) {
    log(level, message, null);
  }

  @Override
  public boolean isLoggable(final Level level) {
    switch (LogLevel.byLevel(level)) {
      case ALL: {
        return this.log.isTraceEnabled();
      }
      case DEBUG:
      case FINEST:
      case FINER: {
        return this.log.isDebugEnabled();
      }
      case FINE:
      case CONFIG:
      case INFO: {
        return this.log.isInfoEnabled();
      }
      case WARNING: {
        return this.log.isWarnEnabled();
      }
      case ERROR: {
        return this.log.isErrorEnabled();
      }
      case SEVERE: {
        return this.log.isFatalEnabled();
      }
      case OFF: {
        return false;
      }
    }
    throw new RuntimeException("Unreachable code reached"); //$NON-NLS-1$
  }

  @Override
  public void log(final Level level, final IMessageFactory factory) {
    if (!isLoggable(level)) {
      return;
    }
    log(level, factory.create());
  }
}