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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtilities {

  /*
   * Get the extension of a file.
   */
  public static String getExtension(final File file) {
    return getExtension(file.getName());
  }

  public static String getExtension(final File file, final String... extensions) {
    return Arrays
        .stream(extensions)
        .filter(extension -> hasExtension(file, extension))
        .findFirst()
        .orElseGet(() -> null);
  }

  public static String getExtension(final String name) {
    final int i = name.lastIndexOf('.');
    return (i > 0 && i < name.length() - 1)
        ? name.substring(i + 1).toLowerCase()
        : null;
  }

  public static File addExtension(final File file, final String extension) {
    return new File(file.getAbsolutePath() + "." + extension); //$NON-NLS-1$
  }

  public static File createFile(final String name) throws IOException {
    final File file = new File(name);
    if (file.exists()) {
      return file;
    }
    final File path = file.getParentFile();
    if (!path.exists()) {
      if (!path.mkdirs()) {
        return null;
      }
    }
    if (!file.createNewFile()) {
      return null;
    }
    return file;
  }

  public static File getWithoutExtension(final File file) {
    if (getExtension(file) == null) {
      return file;
    }
    final String name = file.getName();
    final int index = name.lastIndexOf('.');
    return new File(file.getParent(), name.substring(0, index));
  }

  public static boolean isAbsoluteWindowsFilePath(final String filePath) {
    final Pattern pattern = Pattern.compile("/{0,1}[a-zA-Z]:/"); //$NON-NLS-1$
    final Matcher matcher = pattern.matcher(filePath);
    if (matcher.find()) {
      return matcher.start() == 0;
    }
    return false;
  }

  public static boolean hasExtension(final File file, final String... extentions) {
    return hasExtension(file.getPath(), extentions);
  }

  public static boolean hasExtension(final String path, final String... extentions) {
    if (extentions.length == 1) {
      return path.endsWith(extentions[0]);
    }
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

  public static File getWithoutExtension(final File file, final String... extentions) {
    if (hasExtension(file, extentions)) {
      return getWithoutExtension(file);
    }
    return file;
  }

  public static String getWithoutExtension(final String name) {
    if (getExtension(name) == null) {
      return name;
    }
    final int index = name.lastIndexOf('.');
    return name.substring(0, index);
  }
}
