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
package net.anwiba.commons.logging;

import java.util.logging.Level;

public enum LogLevel {
  ALL(Level.ALL),
  DEBUG(ILevel.DEBUG),
  FINEST(Level.FINEST),
  FINER(Level.FINER),
  FINE(Level.FINE),
  CONFIG(Level.CONFIG),
  INFO(ILevel.INFO),
  WARNING(ILevel.WARNING),
  ERROR(ILevel.ERROR),
  SEVERE(Level.SEVERE),
  OFF(Level.OFF);

  private final Level level;

  private LogLevel(final Level level) {
    this.level = level;
  }

  public static LogLevel byLevel(final Level level) {
    int limes = ALL.level.intValue();
    for (final LogLevel logLevel : values()) {
      if (limes < level.intValue() && level.intValue() <= logLevel.level.intValue()) {
        return logLevel;
      }
      limes = logLevel.level.intValue();
    }
    return OFF;
  }
}
