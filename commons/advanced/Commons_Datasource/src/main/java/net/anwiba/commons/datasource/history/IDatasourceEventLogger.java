/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.commons.datasource.history;

import java.time.Duration;

import net.anwiba.commons.datasource.resource.IResourceDescription;

@SuppressWarnings("nls")
public interface IDatasourceEventLogger {

  default void created(final IResourceDescription targetResourceDescription) {
    log("CREATED", null, targetResourceDescription, null, null, null, null);
  }

  default void indexed(final IResourceDescription targetResourceDescription, final Duration duration) {
    log("INDEXED", null, targetResourceDescription, null, null, null, duration);
  }

  default void renamed(
      final IResourceDescription sourceResourceDescription,
      final IResourceDescription targetResourceDescription,
      final Duration duration) {
    log("RENAMED", sourceResourceDescription, targetResourceDescription, null, null, null, duration);
  }

  default void analyzed(final IResourceDescription targetResourceDescription, final Duration duration) {
    log("ANALYZED", null, targetResourceDescription, null, null, null, duration);
  }

  default void deleted(final IResourceDescription targetResourceDescription) {
    log("DELETED", null, targetResourceDescription, null, null, null, null);
  }

  default void added(
      final IResourceDescription sourceResourceDescription,
      final IResourceDescription targetResourceDescription,
      final String condition,
      final Long numberOfRows,
      final Duration duration) {
    log("ADDED", sourceResourceDescription, targetResourceDescription, null, condition, numberOfRows, duration);
  }

  default void changed(
      final IResourceDescription sourceResourceDescription,
      final IResourceDescription targetResourceDescription,
      final String join,
      final String condition,
      final Long numberOfRows,
      final Duration duration) {
    log("CHANGED", sourceResourceDescription, targetResourceDescription, join, condition, numberOfRows, duration);
  }

  default void restored(
      final IResourceDescription targetResourceDescription,
      final Long numberOfRows,
      final Duration duration) {
    log("RESTORED", null, targetResourceDescription, null, null, numberOfRows, duration);
  }

  default void cleaned(
      final IResourceDescription targetResourceDescription,
      final Long numberOfRows,
      final Duration duration) {
    log("CLEANED", null, targetResourceDescription, null, null, numberOfRows, duration);
  }

  default void removed(
      final IResourceDescription sourceResourceDescription,
      final IResourceDescription targetResourceDescription,
      final String join,
      final String condition,
      final Long numberOfRows,
      final Duration duration) {
    log("REMOVED", sourceResourceDescription, targetResourceDescription, join, condition, numberOfRows, duration);
  }

  void log(
      String kind,
      IResourceDescription sourceResourceDescription,
      IResourceDescription targetResourceDescription,
      final String join,
      final String condition,
      final Long numberOfRows,
      final Duration duration);

}
