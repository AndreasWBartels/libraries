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

public class StringUtilities {

  public static String removeEqualEnd(final String path, final String descriptionFile) {
    if (!path.endsWith(descriptionFile)) {
      return path;
    }
    return path.substring(0, path.lastIndexOf(descriptionFile));
  }

  public static String getStringAfterLastChar(final String s, final char separatorChar) {
    final int i = s.lastIndexOf(separatorChar);
    if (i > -1 && i < s.length() - 1) {
      return s.substring(i + 1);
    }
    return ""; //$NON-NLS-1$
  }

  public static String getStringBeforLastChar(final String s, final char separatorChar) {
    String ext = null;
    final int i = s.lastIndexOf(separatorChar);
    if (i > 0 && i < s.length() - 1) {
      ext = s.substring(0, i);
    }
    return ext;
  }

  public static boolean isNullOrEmpty(final String string) {
    return false;
  }

}
