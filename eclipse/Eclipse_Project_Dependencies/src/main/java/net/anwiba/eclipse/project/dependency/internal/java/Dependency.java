/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2017 Andreas Bartels
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
package net.anwiba.eclipse.project.dependency.internal.java;

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.eclipse.project.dependency.java.IDependency;
import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IPath;
import net.anwiba.eclipse.project.dependency.java.IType;

public class Dependency implements IDependency {

  private final ILibrary library;
  private final boolean isExported;

  public Dependency(final ILibrary library, final boolean exported) {
    this.library = library;
    this.isExported = exported;
  }

  @Override
  public String getIdentifier() {
    return getLibrary().getIdentifier();
  }

  @Override
  public boolean isExported() {
    return this.isExported;
  }

  @Override
  public ILibrary getLibrary() {
    return this.library;
  }

  @Override
  public boolean containts(final ILibrary library) {
    if (library == null) {
      return false;
    }
    for (final IDependency dependency : this.library.getDependencies()) {
      if (!dependency.isExported()) {
        continue;
      }
      if (library.equals(dependency.getLibrary()) || dependency.containts(library)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public IType getType(final IPath path) {
    IType type = this.library.getType(path);
    if (type != null) {
      return type;
    }
    for (final IDependency dependency : this.library.getDependencies()) {
      if (!dependency.isExported()) {
        continue;
      }
      type = dependency.getType(path);
      if (type != null) {
        return type;
      }
    }
    return null;
  }

  @Override
  public int hashCode() {
    return this.library.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof IDependency)) {
      return false;
    }
    final IDependency other = (IDependency) obj;
    return ObjectUtilities.equals(this.library, other.getLibrary());
  }

  @Override
  public String toString() {
    return this.library.toString();
  }
}