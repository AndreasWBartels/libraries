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
package net.anwiba.eclipse.project.dependency.internal.java;

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IPackage;
import net.anwiba.eclipse.project.dependency.java.IPath;
import net.anwiba.eclipse.project.dependency.java.IType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Package implements IPackage {

  private final IPath path;
  private final String identifier;
  private final String name;
  private final List<IType> list = new ArrayList<>();
  private final Map<IPath, List<IType>> types = new HashMap<>();
  private final ILibrary library;

  public Package(final ILibrary library, final IPath path) {
    this.library = library;
    this.path = path;
    this.identifier = path.getIdentifier();
    this.name = path.getSegments()[path.getSegments().length - 1];
  }

  @Override
  public IPath getPath() {
    return this.path;
  }

  @Override
  public String getIdentifier() {
    return this.identifier;
  }

  @Override
  public String getName() {
    return this.path.toString();
  }

  public void add(final IType type) {
    if (!this.path.equals(type.getPath().getParent())) {
      throw new IllegalArgumentException();
    }
    if (!this.types.containsKey(type.getPath())) {
      this.types.put(type.getPath(), new ArrayList<IType>());
    }
    final List<IType> list = this.types.get(type.getPath());
    list.add(type);
    this.list.add(type);
  }

  @Override
  public List<IType> getType(final IPath path) {
    if (this.types.containsKey(path)) {
      return Collections.unmodifiableList(this.types.get(path));
    }
    return Collections.unmodifiableList(new ArrayList<IType>());
  }

  @Override
  public List<IType> getTypes() {
    return Collections.unmodifiableList(this.list);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof IPackage) {
      final IPackage other = (IPackage) obj;
      return ObjectUtilities.equals(this.getIdentifier(), other.getIdentifier())
          && ObjectUtilities.equals(this.library, other.getLibrary());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return ObjectUtilities.hashCode(this.getIdentifier(), this.library);
  }

  @Override
  public boolean containts(final IType type) {
    return this.list.contains(type);
  }

  @Override
  public ILibrary getLibrary() {
    return this.library;
  }

  @Override
  public boolean isParent(final IPackage pakkage) {
    return Objects.equals(this.library, pakkage.getLibrary()) && pakkage.getPath().isParent(this.path);
  }
}
