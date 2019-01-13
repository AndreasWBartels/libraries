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

package net.anwiba.commons.reference.utilities;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.logging.ILevel;

public class PathIterableFactory {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(PathIterableFactory.class);

  public <T> Iterable<T> create(
      final IApplicable<Path> applicable,
      final IConverter<Path, T, RuntimeException> converter,
      final Path path) {

    final DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {

      @Override
      public boolean accept(final Path entry) throws IOException {
        if (Files.isSymbolicLink(entry)) {
          return false;
        }
        return Files.isDirectory(entry) || applicable.isApplicable(entry);
      }
    };
    final Stack<Path> paths = new Stack<>();

    paths.add(path);

    return new Iterable<T>() {

      @Override
      public Iterator<T> iterator() {
        return new Iterator<T>() {

          T next = null;

          @Override
          public T next() {
            try {
              if (this.next == null && !hasNext()) {
                throw new NoSuchElementException();
              }
              return this.next;
            } finally {
              this.next = null;
            }
          }

          @Override
          public boolean hasNext() {
            if (this.next != null) {
              return true;
            }
            while (!paths.isEmpty()) {
              final Path nextPath = paths.pop();
              try {
                if (Files.isDirectory(nextPath)) {
                  Files.newDirectoryStream(nextPath, filter).forEach(p -> paths.add(p));
                  continue;
                }
              } catch (final IOException exception) {
                logger.log(ILevel.DEBUG, exception.getMessage(), exception);
              }
              if (applicable.isApplicable(nextPath)) {
                this.next = converter.convert(nextPath);
                return true;
              }
            }
            return false;
          }
        };
      }
    };
  }
}
