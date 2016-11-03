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

package net.anwiba.commons.resource.utilities;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.functional.IConverter;

public class FileIterableFactory {

  public <T> Iterable<T> create(
      final IApplicable<File> applicable,
      final IConverter<File, T, RuntimeException> converter,
      final File file) {
    final FileFilter filter = new FileFilter() {

      @Override
      public boolean accept(final File pathname) {
        if (Files.isSymbolicLink(file.toPath())) {
          return false;
        }
        return pathname.isDirectory() || applicable.isApplicable(pathname);
      }
    };
    final Stack<File> files = new Stack<>();

    files.add(file);

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
            while (!files.isEmpty()) {
              final File nextFile = files.pop();
              if (nextFile.isDirectory()) {
                final File[] childFiles = nextFile.listFiles(filter);
                for (final File childFile : childFiles) {
                  files.add(childFile);
                }
                continue;
              }
              if (applicable.isApplicable(nextFile)) {
                this.next = converter.convert(nextFile);
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
