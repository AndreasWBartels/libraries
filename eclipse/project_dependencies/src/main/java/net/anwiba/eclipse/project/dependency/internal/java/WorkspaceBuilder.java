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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.anwiba.eclipse.project.dependency.java.IImport;
import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IPackage;
import net.anwiba.eclipse.project.dependency.java.IPath;
import net.anwiba.eclipse.project.dependency.java.IProject;
import net.anwiba.eclipse.project.dependency.java.IType;
import net.anwiba.eclipse.project.dependency.java.IWorkspace;

import java.util.Set;

public class WorkspaceBuilder {

  private final URI uri;
  private final Map<String, Project> projects = new HashMap<String, Project>();
  private final Map<String, Library> libraries = new HashMap<String, Library>();
  private final Map<String, Package> packages = new HashMap<String, Package>();
  private final Map<String, IType> types = new HashMap<String, IType>();
  private final Map<String, List<IType>> duplicates = new HashMap<String, List<IType>>();

  public WorkspaceBuilder(final URI uri) {
    this.uri = uri;
  }

  public void add(final Project project) {
    this.projects.put(project.getIdentifier(), project);
    this.libraries.put(project.getIdentifier(), project);
  }

  public void add(final Library library) {
    this.libraries.put(library.getIdentifier(), library);
  }

  public void add(final Type type) {
    if (!this.duplicates.containsKey(type.getPath().getIdentifier())) {
      this.duplicates.put(type.getPath().getIdentifier(), new ArrayList<IType>());
    }
    this.duplicates.get(type.getPath().getIdentifier()).add(type);
    this.types.put(type.getIdentifier(), type);
    final Library library = this.libraries.get(type.getLibrary().getIdentifier());
    library.add(type);
  }

  public void add(final Package pakkage) {
    this.packages.put(pakkage.getIdentifier(), pakkage);
  }

  public Project getProject(final String identifier) {
    return this.projects.get(identifier);
  }

  public Library getLibrary(final String identifier) {
    return this.libraries.get(identifier);
  }

  public Package getPackage(final String identifier) {
    return this.packages.get(identifier);
  }

  public IWorkspace build() {
    createUseRelations();
    createInstanceOfRelations();
    final Map<String, IProject> projects = convertToInterface(this.projects);
    final Map<String, ILibrary> libraries = convertToInterface(this.libraries);
    final Map<String, IPackage> packages = convertToInterface(this.packages);
    return new Workspace(this.uri, projects, libraries, packages, this.types, this.duplicates);
  }

  private void createUseRelations() {
    for (final Project project : this.projects.values()) {
      for (final IType type : project.getTypes()) {
        for (final IImport impcrt : type.getImports()) {
          final IPath importTypePath = impcrt.getName().endsWith("*") ? impcrt.getPath().getParent() : impcrt.getPath(); //$NON-NLS-1$
          addUsedType(project, type, importTypePath);
        }
        for (final IPath methodTypePath : type.getMethodTypes()) {
          addUsedType(project, type, methodTypePath);
        }
        for (final IPath annotationTypePath : type.getAnnotationTypes()) {
          addUsedType(project, type, annotationTypePath);
        }
      }
    }
  }

  private void addUsedType(final Project project, final IType type, final IPath usedTypePath) {
    if (type.getPath().equals(usedTypePath)) {
      return;
    }
    if (project.containts(usedTypePath)) {
      final IType importType = project.getType(usedTypePath);
      ((Type) importType).addUsedBy(type);
      return;
    }
    final List<IType> types = getTypes(usedTypePath);
    if (types == null) {
      final IType usedType = project.getDependencies().getType(usedTypePath);
      if (usedType != null) {
        ((Type) usedType).addUsedBy(type);
        final Library library = ((Type) usedType).getLibrary();
        library.addUsedBy(project);
        project.addUse(library);
      } else {
        addUsedTypes(project, type, getTypes(usedTypePath));
      }
      return;
    }
    addUsedTypes(project, type, types);
  }

  private void addUsedTypes(final Project project, final IType type, final List<IType> types) {
    if (types == null) {
      return;
    }
    for (final IType usedType : types) {
      ((Type) usedType).addUsedBy(type);
      final Library library = ((Type) usedType).getLibrary();
      library.addUsedBy(project);
      project.addUse(library);
    }
  }

  private List<IType> getTypes(final IPath path) {
    final List<IType> types = this.duplicates.get(path.getIdentifier());
    if (types == null) {
      return this.duplicates.get(path.getParent().getIdentifier());
    }
    return types;
  }

  private void createInstanceOfRelations() {
    for (final Project project : this.projects.values()) {
      for (final IType type : project.getTypes()) {
        for (final IPath superTypePath : type.getSuperTypes()) {
          if (project.containts(superTypePath)) {
            final IType superType = project.getType(superTypePath);
            ((Type) superType).addInstance(type);
            continue;
          }
          final IType superType = project.getDependencies().getType(superTypePath);
          if (superType != null) {
            ((Type) superType).addInstance(type);
            final Library library = ((Type) superType).getLibrary();
            library.addImplemetedFrom(project);
            project.addImplemets(library);
            continue;
          }
        }
      }
    }
  }

  private <I, C extends I> Map<String, I> convertToInterface(final Map<String, C> map) {
    final Set<Entry<String, C>> entrySet = map.entrySet();
    final Map<String, I> result = new HashMap<String, I>();
    for (final Entry<String, C> entry : entrySet) {
      result.put(entry.getKey(), entry.getValue());
    }
    return result;
  }
}
