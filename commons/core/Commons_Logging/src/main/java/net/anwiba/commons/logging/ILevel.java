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

import net.anwiba.commons.logging.java.LevelFactory;

@SuppressWarnings("nls")
public interface ILevel {

  public static final class Level implements ILevel {

    private final java.util.logging.Level level;

    public Level(final java.util.logging.Level level) {
      this.level = level;
    }

    @Override
    public String getName() {
      return level.getName();
    }

    @Override
    public int intValue() {
      return level.intValue();
    }
  }

  final static public ILevel ALL = new Level(LevelFactory.add(java.util.logging.Level.ALL));
  final static public ILevel INFO = new Level(LevelFactory.add(java.util.logging.Level.INFO));
  final static public ILevel WARNING = new Level(LevelFactory.add(java.util.logging.Level.WARNING));
  final static public ILevel FATAL = new Level(LevelFactory.add(java.util.logging.Level.SEVERE));
  public static final ILevel SEVERE = new Level(LevelFactory.add(java.util.logging.Level.SEVERE));
  final static public ILevel ERROR = new Level(LevelFactory.createLevel("ERROR", 1000));
  final static public ILevel DEBUG = new Level(LevelFactory.createLevel("DEBUG", 400));
  public static final ILevel FINE = new Level(LevelFactory.add(java.util.logging.Level.FINE));

  public String getName();

  public int intValue();

}
