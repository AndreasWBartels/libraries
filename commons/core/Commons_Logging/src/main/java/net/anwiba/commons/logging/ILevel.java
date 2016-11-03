/*
 * #%L anwiba commons core %% Copyright (C) 2007 - 2016 Andreas Bartels %% This program is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 2.1 of the License,
 * or (at your option) any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Lesser Public License for more details. You should
 * have received a copy of the GNU General Lesser Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/lgpl-2.1.html>. #L%
 */
package net.anwiba.commons.logging;

import java.util.logging.Level;

public interface ILevel {

  final static public Level ALL = Level.ALL;
  final static public Level INFO = Level.INFO;
  final static public Level WARNING = Level.WARNING;
  final static public Level FATAL = Level.SEVERE;
  final static public Level ERROR = LevelFactory.createLevel("ERROR", 1000); //$NON-NLS-1$
  final static public Level DEBUG = LevelFactory.createLevel("DEBUG", 400); //$NON-NLS-1$

}
