/*
 * #%L
 * anwiba eclipse java tools
 * %%
 * Copyright (C) 2007 - 2022 Andreas Bartels
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
package net.anwiba.eclipse.project.dependency.relation;

import net.anwiba.eclipse.project.dependency.java.IItem;
import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IPackage;
import net.anwiba.eclipse.project.dependency.java.IType;
import net.anwiba.eclipse.project.dependency.object.IDependencyRelation;

public class CellValueFactory {

  public static String create(final IDependencyRelation description, final int columnIndex) {
    final IItem item = description.getItem();
    switch (columnIndex) {
    case 0: {
      return description.getRelationType().name();
    }
    case 1: {
      if (item instanceof ILibrary) {
        return ((ILibrary) item).getLibraryType().name();
      }
      if (item instanceof IPackage) {
        return "Package";
      }
      if (item instanceof IType) {
        return ((IType) item).getType().name();
      }
      return null;
    }
    case 2: {
      return item.getName();
    }
    }
    return null;
  }
}
