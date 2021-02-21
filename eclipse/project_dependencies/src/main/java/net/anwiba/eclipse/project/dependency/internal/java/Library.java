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
package net.anwiba.eclipse.project.dependency.internal.java;

import java.util.HashMap;
import java.util.Map;

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.eclipse.project.dependency.java.IDependency;
import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IPackage;
import net.anwiba.eclipse.project.dependency.java.IPath;
import net.anwiba.eclipse.project.dependency.java.IType;
import net.anwiba.eclipse.project.dependency.java.LibraryType;

public class Library implements ILibrary {

  private final Dependencies dependencies = new Dependencies();
  private final Map<IPath, IPackage> packages = new HashMap<>();
  private final Map<IPath, IType> types = new HashMap<>();
  private final String name;
  private final LibraryType type;
  private final Map<String, ILibrary> usedBy = new HashMap<>();
  private final Map<String, ILibrary> implemetedFrom = new HashMap<>();
  private final Map<String, ILibrary> implemeted = new HashMap<>();
  private final Map<String, ILibrary> usedLibraries = new HashMap<>();

  public Library(final String name, final LibraryType type) {
    this.name = name;
    this.type = type;
  }

  @Override
  public boolean containts(final IType type) {
    return this.types.containsValue(type);
  }

  @Override
  public Iterable<ILibrary> getImplementedFromLibraries() {
    return this.implemetedFrom.values();
  }

  @Override
  public Iterable<ILibrary> getImplementedLibraries() {
    return this.implemeted.values();
  }

  @Override
  public Iterable<ILibrary> getUsedLibraries() {
    return this.usedLibraries.values();
  }

  @Override
  public Iterable<ILibrary> getUsedByLibraries() {
    return this.usedBy.values();
  }

  public void add(final IDependency dependency) {
    if (dependency == null || this.getIdentifier().equals(dependency.getIdentifier())) {
      return;
    }
    if (dependency.containts(this)) {
      throw new IllegalArgumentException("dependency cycle detected"); //$NON-NLS-1$
    }
    this.dependencies.add(dependency);
  }

  public void add(final IPackage pakkage) {
    if (pakkage == null) {
      return;
    }
    this.packages.put(pakkage.getPath(), pakkage);
  }

  public void add(final IType type) {
    if (type == null) {
      return;
    }
    if (!this.equals(type.getLibrary())) {
      throw new IllegalArgumentException();
    }
    this.types.put(type.getPath(), type);
  }

  @Override
  public Iterable<IType> getTypes() {
    return this.types.values();
  }

  @Override
  public String getIdentifier() {
    return getName();
  }

  @Override
  public IType getType(final IPath path) {
    if (path == null) {
      return null;
    }
    if (this.types.containsKey(path)) {
      return this.types.get(path);
    }
    return null;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public LibraryType getLibraryType() {
    return this.type;
  }

  @Override
  public boolean containts(final IPath path) {
    return this.types.containsKey(path);
  }

  @Override
  public Dependencies getDependencies() {
    return this.dependencies;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof ILibrary) {
      final ILibrary other = (ILibrary) obj;
      return ObjectUtilities.equals(this.getIdentifier(), other.getIdentifier());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return ObjectUtilities.hashCode(this.getIdentifier());
  }

  public void addUsedBy(final ILibrary library) {
    if (this.equals(library)) {
      return;
    }
    this.usedBy.put(library.getIdentifier(), library);
  }

  public void addUse(final ILibrary library) {
    if (this.equals(library)) {
      return;
    }
    this.usedLibraries.put(library.getIdentifier(), library);
  }

  public void addImplemetedFrom(final ILibrary library) {
    if (this.equals(library)) {
      return;
    }
    this.implemetedFrom.put(library.getIdentifier(), library);
  }

  @Override
  public void addImplemets(final ILibrary library) {
    if (this.equals(library)) {
      return;
    }
    this.implemeted.put(library.getIdentifier(), library);
  }

  public void reset() {
    this.dependencies.reset();
    this.usedBy.clear();
    this.usedLibraries.clear();
    this.implemetedFrom.clear();
    for (final IType type : this.types.values()) {
      ((Type) type).reset();
    }
  }

  @Override
  public boolean isInstance(final ILibrary library) {
    if (this.implemetedFrom.isEmpty()) {
      return false;
    }
    final boolean containsKey = this.implemetedFrom.containsKey(library.getIdentifier());
    return containsKey;
  }
}
