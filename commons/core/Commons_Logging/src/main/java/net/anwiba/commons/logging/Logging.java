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

import java.util.logging.Handler;
import java.util.logging.Level;

public class Logging {

  private static LoggingFactory factory = new LoggingFactory();
  private static ILogging logging = factory.create(net.anwiba.commons.logging.apache.ApacheLogging.class.getName());

  public static void setLogging(final String className) {
    logging = factory.create(className);
  }

  public static ILogger getLogger(final Class<?> clazz) {
    if (clazz == null) {
      throw new IllegalArgumentException();
    }
    return getLogger(clazz.getName());
  }

  public static ILogger getLogger(final String name) {
    if (logging == null) {
      return new ILogger() {

        @Override
        public void log(final Level level, final String message, final Throwable throwable) {
          // nothing to do
        }

        @Override
        public void log(final Level level, final String message) {
          // nothing to do
        }

        @Override
        public boolean isLoggable(final Level level) {
          return false;
        }

        @Override
        public void log(final Level level, final IMessageFactory messageFactory) {
          // nothing to do
        }
      };
    }
    return logging.getLogger(name);
  }

  public static void setHandler(final Handler... handlers) {
    if (logging == null) {
      return;
    }
    logging.setHandler(handlers);
  }

  public static void setLevel(final Level level, final String nameSpace) {
    if (logging == null) {
      return;
    }
    logging.setLevel(level, nameSpace);
  }
}
