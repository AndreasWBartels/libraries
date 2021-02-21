/*
 * #%L
 * anwiba eclipse java tools
 * %%
 * Copyright (C) 2007 - 2021 Andreas Bartels
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
package net.anwiba.eclipse.project.dependency.object;

import org.eclipse.jdt.core.IJavaElement;

public enum ItemType {
  UNKOWN(0), PACKAGE_ROOT(IJavaElement.PACKAGE_FRAGMENT_ROOT), PACKAGE(IJavaElement.PACKAGE_FRAGMENT), CLASS(
      IJavaElement.CLASS_FILE), COMPILATION_UNIT(IJavaElement.COMPILATION_UNIT), TYPE(IJavaElement.TYPE);

  private final int elementType;

  private ItemType(final int elementType) {
    this.elementType = elementType;
  }

  public static ItemType getByElementType(final int elementType) {
    final ItemType[] values = values();
    for (final ItemType itemType : values) {
      if (itemType.elementType == elementType) {
        return itemType;
      }
    }
    return UNKOWN;
  }
}
