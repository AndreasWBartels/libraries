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

import net.anwiba.eclipse.project.dependency.java.IImport;
import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IPackage;
import net.anwiba.eclipse.project.dependency.java.IPath;
import net.anwiba.eclipse.project.dependency.java.IProject;
import net.anwiba.eclipse.project.dependency.java.IType;
import net.anwiba.eclipse.project.dependency.java.IWorkspace;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Workspace implements IWorkspace {

  private final Map<String, IProject> projects;
  private final Map<String, IPackage> packages;
  private final Map<String, List<IType>> duplicates;
  private final Map<String, ILibrary> libraries;
  private final Map<String, IType> types;
  private final URI uri;

  public Workspace(
    final URI uri,
    final Map<String, IProject> projects,
    final Map<String, ILibrary> libraries,
    final Map<String, IPackage> packages,
    final Map<String, IType> types,
    final Map<String, List<IType>> duplicates) {
    this.uri = uri;
    this.types = Collections.unmodifiableMap(types);
    this.projects = Collections.unmodifiableMap(projects);
    this.libraries = Collections.unmodifiableMap(libraries);
    this.packages = Collections.unmodifiableMap(packages);
    this.duplicates = Collections.unmodifiableMap(duplicates);
  }

  @Override
  public IType[] getTypes(final IPath path) {
    final List<IType> types = new ArrayList<>();
    for (final ILibrary library : this.libraries.values()) {
      if (library.containts(path)) {
        types.add(library.getType(path));
      }
    }
    for (final IProject project : this.projects.values()) {
      if (project.containts(path)) {
        types.add(project.getType(path));
      }
    }
    return types.toArray(new IType[types.size()]);
  }

  @Override
  public Map<String, IType> getTypes() {
    return this.types;
  }

  @Override
  public Map<String, IPackage> getPackages() {
    return this.packages;
  }

  @Override
  public Map<String, IProject> getProjects() {
    return this.projects;
  }

  @Override
  public Map<String, ILibrary> getLibraries() {
    return this.libraries;
  }

  @Override
  public Map<String, List<IType>> getDuplicates() {
    return this.duplicates;
  }

  @Override
  public URI getUri() {
    return this.uri;
  }

  @Override
  public IProject getProject(final String name) {
    return this.projects.get("/" + name);
  }

  @Override
  public IPackage getPackage(final String name) {
    return this.packages.get(name);
  }

  @Override
  public List<IType> getUsed(final IType type) {
    final Set<IType> result = new HashSet<>();
    for (final IImport impcrt : type.getImports()) {
      result.addAll(Arrays.asList(getTypes(impcrt.getPath())));
    }
    for (final IPath path : type.getMethodTypes()) {
      result.addAll(Arrays.asList(getTypes(path)));
    }
    for (final IPath path : type.getAnnotationTypes()) {
      result.addAll(Arrays.asList(getTypes(path)));
    }
    return new ArrayList<>(result);
  }

  @Override
  public List<IType> getImplemented(final IType type) {
    final List<IType> result = new ArrayList<>();
    for (final IPath path : type.getSuperTypes()) {
      result.addAll(Arrays.asList(getTypes(path)));
    }
    return result;
  }

}
