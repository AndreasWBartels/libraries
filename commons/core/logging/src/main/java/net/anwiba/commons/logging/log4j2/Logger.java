/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
package net.anwiba.commons.logging.log4j2;

import java.util.Objects;

import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.MessageSupplier;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.LogMessage;
import net.anwiba.commons.logging.ILogMessageFactory;

public class Logger implements ILogger {

  private final org.apache.logging.log4j.Logger logger;

  public Logger(final org.apache.logging.log4j.Logger logger) {
    this.logger = logger;
  }

  @Override
  public void doLog(ILevel level, ILogMessageFactory message) {
    if (!isLoggable(level)) {
      return;
    }
    MessageSupplier supplier = new MessageSupplier() {
      
      @Override
      public Message get() {
        return new Message() {
          
          LogMessage logMessage = null;

          @Override
          public Throwable getThrowable() {
            if (logMessage == null) {
              logMessage = message.create(level);
            }
            return logMessage.throwable();
          }
          
          @Override
          public Object[] getParameters() {
            return null;
          }
          
          @Override
          public String getFormattedMessage() {
            if (logMessage == null) {
              logMessage = message.create(level);
            }
            return logMessage.message();
          }
          
          @Override
          public String getFormat() {
            return null;
          }
        };
      }
    };
    if (Objects.equals(ILevel.ALL, level)
        || Objects.equals(ILevel.FINE, level)) {
      this.logger.trace(supplier);
    }
    if (Objects.equals(ILevel.DEBUG, level)) {
      this.logger.debug(supplier);
    }
    if (Objects.equals(ILevel.INFO, level)) {
      this.logger.info(supplier);
    }
    if (Objects.equals(ILevel.WARNING, level)) {
      this.logger.warn(supplier);
    }
    if (Objects.equals(ILevel.ERROR, level)) {
      this.logger.error(supplier);
    }
    if (Objects.equals(ILevel.SEVERE, level)
        || Objects.equals(ILevel.FATAL, level)) {
      this.logger.fatal(supplier);
    }
  }

  @Override
  public boolean isLoggable(final ILevel level) {
    if (Objects.equals(ILevel.ALL, level)
        || Objects.equals(ILevel.FINE, level)) {
      return this.logger.isTraceEnabled();
    }
    if (Objects.equals(ILevel.DEBUG, level)) {
      return this.logger.isDebugEnabled();
    }
    if (Objects.equals(ILevel.INFO, level)) {
      return this.logger.isInfoEnabled();
    }
    if (Objects.equals(ILevel.WARNING, level)) {
      return this.logger.isWarnEnabled();
    }
    if (Objects.equals(ILevel.ERROR, level)) {
      return this.logger.isErrorEnabled();
    }
    if (Objects.equals(ILevel.SEVERE, level)
        || Objects.equals(ILevel.FATAL, level)) {
      return this.logger.isFatalEnabled();
    }
    return false;
  }

}
