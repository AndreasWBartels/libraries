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

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.ILogging;

public class JavaLogging implements ILogging {

  @Override
  public ILogger getLogger(final String name) {
    return getLogger(name, null);
  }

  private ILogger getLogger(final String name, final String resourceBundleName) {
    final LogManager manager = LogManager.getLogManager();
    Logger logger = manager.getLogger(name);
    if (logger != null) {
      return (ILogger) logger;
    }
    logger = new LoggerCover(name, resourceBundleName);
    manager.addLogger(logger);
    return (ILogger) logger;
  }

  @Override
  public void setHandler(final Handler... handlers) {
    final Logger logger = getRootLogger();
    for (final Handler handler : handlers) {
      logger.addHandler(handler);
    }
  }

  private Logger getRootLogger() {
    final LogManager manager = LogManager.getLogManager();
    return manager.getLogger(""); //$NON-NLS-1$
  }

  @Override
  public void setLevel(final Level level, final String nameSpace) {
    ((Logger) getLogger(nameSpace)).setLevel(level);
  }

  public static Level create(final ILevel level) {
    return LevelFactory.createLevel(level.getName(), level.intValue());
  }
}
