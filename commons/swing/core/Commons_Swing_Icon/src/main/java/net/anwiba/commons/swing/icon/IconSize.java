/*
 * #%L
 * anwiba commons swing
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

package net.anwiba.commons.swing.icon;

public class IconSize implements IIconSize {

  public static IIconSize create(final String parent, final String path, final int size) {
    final String relativePath = path.startsWith("/") ? path : "/" + path; //$NON-NLS-1$//$NON-NLS-2$
    if (parent == null) {
      return create(relativePath, size);
    }
    final String parentPath = parent.startsWith("/") ? parent.substring(1, parent.length()) : parent; //$NON-NLS-1$
    return create(
        parentPath.endsWith("/") ? parentPath.substring(0, parentPath.length() - 1) + relativePath : parentPath + relativePath, size); //$NON-NLS-1$
  }

  public static IIconSize create(final String path, final int size) {
    return new IconSize(path, size);
  }

  public static IIconSize small(final String path) {
    return create(path, 16);
  }

  public static IIconSize medium(final String path) {
    return create(path, 22);
  }

  public static IIconSize large(final String path) {
    return create(path, 32);
  }

  private final int size;
  private final String path;

  private IconSize(final String path, final int size) {
    this.path = path;
    this.size = size;
  }

  @Override
  public String getPath() {
    return this.path;
  }

  @Override
  public int getSize() {
    return this.size;
  }
}
