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

import java.util.logging.Handler;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;

import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.ILogging;

public class Log4j2Logging implements ILogging {

  @Override
  public ILogger getLogger(final String name) {
    return new Logger(LogManager.getLogger(name));
  }

  @Override
  public void setHandler(final Handler... handlers) {
    // nothing to do
  }

  @Override
  public void setLevel(final Level level, final String nameSpace) {
    // nothing to do
  }

}
