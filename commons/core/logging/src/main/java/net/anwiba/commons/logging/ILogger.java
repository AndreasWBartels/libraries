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

public interface ILogger {

  void log(ILevel level, IMessageFactory factory);

  default void all(final IMessageFactory factory) {
    log(ILevel.ALL, factory);
  }

  default void fine(final IMessageFactory factory) {
    log(ILevel.FINE, factory);
  }

  default void debug(final IMessageFactory factory) {
    log(ILevel.DEBUG, factory);
  }

  default void info(final IMessageFactory factory) {
    log(ILevel.INFO, factory);
  }

  default void warning(final IMessageFactory factory) {
    log(ILevel.WARNING, factory);
  }

  default void error(final IMessageFactory factory) {
    log(ILevel.ERROR, factory);
  }

  default void fatal(final IMessageFactory factory) {
    log(ILevel.FATAL, factory);
  }

  default void serve(final IMessageFactory factory) {
    log(ILevel.SEVERE, factory);
  }

  void log(ILevel level, String message);

  default void all(final String message) {
    log(ILevel.ALL, message);
  }

  default void fine(final String message) {
    log(ILevel.FINE, message);
  }

  default void debug(final String message) {
    log(ILevel.DEBUG, message);
  }

  default void info(final String message) {
    log(ILevel.INFO, message);
  }

  default void warning(final String message) {
    log(ILevel.WARNING, message);
  }

  default void error(final String message) {
    log(ILevel.ERROR, message);
  }

  default void fatal(final String message) {
    log(ILevel.FATAL, message);
  }

  default void serve(final String message) {
    log(ILevel.SEVERE, message);
  }

  void log(ILevel level, String message, Throwable throwable);

  default void all(final String message, final Throwable throwable) {
    log(ILevel.ALL, message, throwable);
  }

  default void fine(final String message, final Throwable throwable) {
    log(ILevel.FINE, message, throwable);
  }

  default void debug(final String message, final Throwable throwable) {
    log(ILevel.DEBUG, message, throwable);
  }

  default void info(final String message, final Throwable throwable) {
    log(ILevel.INFO, message, throwable);
  }

  default void warning(final String message, final Throwable throwable) {
    log(ILevel.WARNING, message, throwable);
  }

  default void error(final String message, final Throwable throwable) {
    log(ILevel.ERROR, message, throwable);
  }

  default void fatal(final String message, final Throwable throwable) {
    log(ILevel.FATAL, message, throwable);
  }

  default void serve(final String message, final Throwable throwable) {
    log(ILevel.SEVERE, message, throwable);
  }

  boolean isLoggable(ILevel level);

}
