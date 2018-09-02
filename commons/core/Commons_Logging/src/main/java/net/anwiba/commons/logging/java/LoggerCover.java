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
package net.anwiba.commons.logging.java;

import java.util.logging.Level;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.IMessageFactory;

class LoggerCover extends java.util.logging.Logger implements ILogger {

  protected LoggerCover(final String name, final String resourceBundleName) {
    super(name, resourceBundleName);
  }

  @Override
  public void log(final Level level, final String message, final Throwable throwable) {
    super.log(level, message, throwable);
  }

  @Override
  public void log(final ILevel level, final IMessageFactory factory) {
    if (!isLoggable(level)) {
      return;
    }
    log(level, factory.create());
  }

  @Override
  public void log(final ILevel level, final String message) {
    log(level, message, null);
  }

  @Override
  public void log(final ILevel level, final String message, final Throwable throwable) {
    super.log(JavaLogging.create(level), message, throwable);
  }

  @Override
  public boolean isLoggable(final ILevel level) {
    return isLoggable(JavaLogging.create(level));
  }

}
