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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class LevelFactory {

  private static Map<Integer, Map<String, Level>> levels = new HashMap<>();

  private static class LoggingLevel extends Level {

    private LoggingLevel(final String name, final int value) {
      super(name, value, "sun.util.logging.resources.logging"); //$NON-NLS-1$
    }

    private static final long serialVersionUID = 1L;

  }

  public static Level createLevel(final String name, final int value) {
    if (!levels.containsKey(value)) {
      levels.put(value, new HashMap<>());
    }
    final Map<String, Level> map = levels.get(value);
    if (!map.containsKey(name)) {
      final LoggingLevel level = new LoggingLevel(name, value);
      map.put(name, level);
    }
    return levels.get(value).get(name);
  }

  public static Level add(final Level level) {
    if (!levels.containsKey(level.intValue())) {
      levels.put(level.intValue(), new HashMap<>());
    }
    final Map<String, Level> map = levels.get(level.intValue());
    if (!map.containsKey(level.getName())) {
      map.put(level.getName(), level);
    }
    return level;
  }
}
