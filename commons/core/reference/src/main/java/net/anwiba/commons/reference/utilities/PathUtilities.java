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
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import net.anwiba.commons.lang.optional.Optional;

public class PathUtilities {

  /*
   * Get the extension of a file.
   */
  public static String getExtension(final Path path) {
    return getExtension(Optional.of(path.getFileName()).convert(n -> n.toString()).getOr(() -> ""));
  }

  public static String getExtension(final Path path, final String... extensions) {
    return Arrays
        .stream(extensions)
        .filter(extension -> hasExtension(path, extension))
        .findFirst()
        .orElseGet(() -> null);
  }

  public static String getExtension(final String name) {
    final int i = name.lastIndexOf('.');
    return (i > 0 && i < name.length() - 1)
        ? name.substring(i + 1).toLowerCase()
        : null;
  }

  public static Path addExtension(final Path path, final String extension) {
    final String name = Optional.of(path.getFileName()).convert(n -> n.toString()).getOr(() -> "");
    return path.getParent() == null
        ? path.getFileSystem().getPath(name + "." + extension)
        : path.getFileSystem().getPath(path.getParent().toString(), name + "." + extension);
  }

  public static Path getFileWithoutExtension(final Path path) {
    if (getExtension(path) == null) {
      return path;
    }
    final String name = Optional.of(path.getFileName()).convert(n -> n.toString()).getOr(() -> "");
    final int index = name.lastIndexOf('.');
    return path.getParent() == null
        ? path.getFileSystem().getPath(name.substring(0, index))
        : path.getFileSystem().getPath(path.getParent().toString(), name.substring(0, index));
  }

  public static boolean hasExtension(final Path path, final String... extentions) {
    final String value = getExtension(path);
    if (value == null) {
      return false;
    }
    for (final String extention : extentions) {
      if (value.equalsIgnoreCase(extention)) {
        return true;
      }
    }
    return false;
  }

  public static Path getFileWithoutExtention(final Path path, final String... extentions) {
    if (hasExtension(path, extentions)) {
      return getFileWithoutExtension(path);
    }
    return path;
  }

  public static FileSystem getFileSystem(final URI uri) throws IOException {
    try {
      if (Objects.equals(uri.getScheme(), FileSystems.getDefault().provider().getScheme())) {
        return FileSystems.getDefault();
      }
      // return FileSystems.newFileSystem(path, uri.getClass().getClassLoader());
      return FileSystems.newFileSystem(uri, Map.of());
    } catch (FileSystemAlreadyExistsException e) {
      return FileSystems.getFileSystem(uri);
    }
  }

  public static Path create(final URI uri) throws IOException {
    return getFileSystem(uri).provider().getPath(uri);
  }

  public static Path create(final List<String> path) {
    return create(path.toArray(String[]::new));
  }

  public static Path create(final String[] path) {
    if (path == null) {
      return null;
    }
    if (path.length == 0) {
      return Paths.get("");
    }
    if (path.length == 1) {
      return Paths.get(path[0]);
    }
    String[] subpath = new String[path.length-1];
    System.arraycopy(path, 1, subpath, 0, path.length-1);
    return Paths.get(path[0], subpath);
  }

  public static List<Path> collectFolders(final Path folder, final Predicate<Path> predicate) throws IOException {
    List<Path> folders = new LinkedList<>();
    Files.walkFileTree(folder, new SimpleFileVisitor<>() {

      public FileVisitResult postVisitDirectory(final Path dir, final IOException exc)
          throws IOException {
        if (predicate.test(dir)) {
          folders.add(dir);
        }
        return FileVisitResult.CONTINUE;
      };
    });
    return folders;
  }

  public static void deleteIfExits(final Path folder) throws IOException {
    if (!Files.exists(folder)) {
      return;
    }
    Files.walkFileTree(folder, new SimpleFileVisitor<>() {

      public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        FileVisitResult result = super.visitFile(file, attrs);
        Files.deleteIfExists(file);
        return result;
      };

      public FileVisitResult postVisitDirectory(final Path dir, final IOException exc)
          throws IOException {
        FileVisitResult result = super.postVisitDirectory(dir, exc);
        Files.deleteIfExists(dir);
        return result;
      };
    });
  }
}
