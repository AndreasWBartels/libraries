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
// Copyright (c) 2010 by Andreas W. Bartels (bartels@anwiba.de)
package net.anwiba.eclipse.project.dependency.runner;

import net.anwiba.commons.eclipse.logging.ILogger;
import net.anwiba.commons.model.IObjectListModel;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.thread.cancel.Canceler;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.eclipse.project.dependency.java.IImport;
import net.anwiba.eclipse.project.dependency.java.IItem;
import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IPackage;
import net.anwiba.eclipse.project.dependency.java.IPath;
import net.anwiba.eclipse.project.dependency.java.IProject;
import net.anwiba.eclipse.project.dependency.java.IType;
import net.anwiba.eclipse.project.dependency.java.IWorkspace;
import net.anwiba.eclipse.project.dependency.model.IDependenciesModel;
import net.anwiba.eclipse.project.dependency.object.DependencyRelation;
import net.anwiba.eclipse.project.dependency.object.IDependencyRelation;
import net.anwiba.eclipse.project.dependency.object.RelationType;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Label;

public final class ListRunner implements IRunnableWithProgress {

  private final ICanceler canceler;
  private final IDependenciesModel dependenciesModel;
  private final IJavaModel model;
  private final ILogger logger;
  private final IJavaElement[] selection;
  private final WritableList<IDependencyRelation> relations;
  private final INameHitMaps nameHitMaps;
  private final Label label;
  private final IObjectModel<IItem> selectedItemModel;
  private final IObjectListModel<IItem> selectedItemsModel;

  public ListRunner(
    final ICanceler canceler,
    final ILogger logger,
    final IJavaElement[] selection,
    final IDependenciesModel dependenciesModel,
    final IObjectModel<IItem> selectedItemModel,
    final IObjectListModel<IItem> selectedItemsModel,
    final Label label,
    final IJavaModel model,
    final WritableList<IDependencyRelation> relations,
    final INameHitMaps nameHitMaps) {
    this.logger = logger;
    this.selection = selection;
    this.dependenciesModel = dependenciesModel;
    this.canceler = canceler;
    this.selectedItemModel = selectedItemModel;
    this.selectedItemsModel = selectedItemsModel;
    this.label = label;
    this.model = model;
    this.relations = relations;
    this.nameHitMaps = nameHitMaps;
  }

  @Override
  public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
    try {
      monitor.beginTask("List dependencies", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
      final ICanceler canceler = new Canceler(true) {

        @Override
        public boolean isCanceled() {
          return ListRunner.this.canceler.isCanceled() || monitor.isCanceled();
        }
      };
      this.selectedItemModel.set(null);
      this.selectedItemsModel.removeAll();
      final IWorkspace workspace = getWorkspace(monitor, canceler);
      this.label.setText(" "); //$NON-NLS-1$
      this.relations.clear();
      this.label.setText(" "); //$NON-NLS-1$
      if (this.selection.length == 0) {
        return;
      }
      final List<IItem> items = new ArrayList<>();
      for (final IJavaElement element : this.selection) {
        items.addAll(createItem(workspace, element));
      }
      this.relations.addAll(addAll(workspace, items));
      this.selectedItemsModel.add(items);
      if (items.size() == 1) {
        final IItem item = items.get(0);
        this.selectedItemModel.set(item);
        updateLable(item);
      }
    } catch (final InterruptedException exception) {
      throw exception;
    } catch (final Exception exception) {
      throw new InvocationTargetException(exception);
    } finally {
      monitor.done();
    }
  }

  private void updateLable(final IItem item) {
    if (item instanceof IType) {
      final IType type = (IType) item;
      this.label.setText("Source: " + type.getQualifiedName()); //$NON-NLS-1$
    }
    if (item instanceof IPackage) {
      final IPackage pakkage = (IPackage) item;
      this.label.setText("Package: " + pakkage.getName()); //$NON-NLS-1$
    }
    if (item instanceof IProject) {
      final IProject project = (IProject) item;
      this.label.setText("Project: " + project.getName()); //$NON-NLS-1$
    }
  }

  private List<IItem> createItem(final IWorkspace workspace, final IJavaElement element) throws JavaModelException {
    if (element instanceof ICompilationUnit) {
      final ICompilationUnit compilationUnit = (ICompilationUnit) element;
      final org.eclipse.jdt.core.IType[] types = compilationUnit.getTypes();
      final List<IItem> items = new ArrayList<>();
      for (final org.eclipse.jdt.core.IType eclipseType : types) {
        final String qualifiedName = eclipseType.getFullyQualifiedName();
        final String root = compilationUnit.getPath().segment(0);
        final IType type = workspace.getTypes().get(MessageFormat.format("/{0}.{1}", root, qualifiedName)); //$NON-NLS-1$
        items.add(type);
      }
      return items;
    } else if (element instanceof IClassFile) {
      final IClassFile classFile = (IClassFile) element;
      final org.eclipse.jdt.core.IType eclipseType = classFile.findPrimaryType();
      if (eclipseType != null) {
        final String qualifiedName = eclipseType.getFullyQualifiedName();
        final String root = classFile.getPath().toString();
        final IType type = workspace.getTypes().get(MessageFormat.format("{0}.{1}", root, qualifiedName)); //$NON-NLS-1$
        return Arrays.asList(type);
      }
      return new ArrayList<>();
    } else if (element instanceof IPackageFragmentRoot) {
      final IPackageFragmentRoot packageFragment = (IPackageFragmentRoot) element;
      final String elementName = packageFragment.getPath().toString();
      // final String elementName = packageFragment.getElementName();
      final ILibrary library = workspace.getLibraries().get(elementName);
      return Arrays.asList(library);
    } else if (element instanceof IPackageFragment) {
      final IPackageFragment packageFragment = (IPackageFragment) element;
      final String elementName = packageFragment.getElementName();
      final IPackage pakkage = workspace.getPackage(elementName);
      return Arrays.asList(pakkage);
    } else if (element instanceof IJavaProject) {
      final IJavaProject javaProject = (IJavaProject) element;
      final String elementName = javaProject.getElementName();
      final IProject project = workspace.getProject(elementName);
      return Arrays.asList(project);
    }
    return new ArrayList<>();
  }

  private Collection<IDependencyRelation> addAll(final IWorkspace workspace, final List<IItem> items) {
    final Map<DependencyRelationKey, IDependencyRelation> relations = new HashMap<>();
    for (final IItem item : items) {
      addTo(relations, workspace, item);
    }
    return relations.values();
  }

  private void addTo(
      final Map<DependencyRelationKey, IDependencyRelation> relations,
      final IWorkspace workspace,
      final IItem item) {
    if (item instanceof IType) {
      final IType type = (IType) item;
      collectImplementsRelations(relations, workspace, type, type.getSuperTypes());
      collectUseRelations(relations, workspace, type, type.getImports());
      collectPathRelations(relations, workspace, type, type.getMethodTypes(), RelationType.USE);
      collectRelations(relations, workspace, type, type.getImplementedBy(), RelationType.IMPLEMENTED_BY);
      collectRelations(relations, workspace, type, type.getUsedBy(), RelationType.USED_BY);
      return;
    }
    if (item instanceof IPackage) {
      final IPackage pakkage = (IPackage) item;
      for (final IType type : pakkage.getTypes()) {
        for (final IType relationType : type.getImplementedBy()) {
          collect(relations, pakkage, relationType, RelationType.IMPLEMENTED_BY);
        }
        for (final IType relationType : type.getUsedBy()) {
          collect(relations, pakkage, relationType, RelationType.USED_BY);
        }
        for (final IPath path : type.getSuperTypes()) {
          collect(workspace, relations, pakkage, path, RelationType.IMPLEMETS);
        }
        for (final IPath path : type.getMethodTypes()) {
          collect(workspace, relations, pakkage, path, RelationType.USE);
        }
        for (final IImport impcrt : type.getImports()) {
          collect(workspace, relations, pakkage, impcrt.getPath(), RelationType.USE);
        }
      }
      return;
    }
    if (item instanceof IProject) {
      final IProject project = (IProject) item;
      addRelation(relations, RelationType.IMPLEMETS, project.getImplementedLibraries());
      addRelation(relations, RelationType.IMPLEMENTED_BY, project.getImplementedFromLibraries());
      addRelation(relations, RelationType.USE, project.getUsedLibraries());
      addRelation(relations, RelationType.USED_BY, project.getUsedByLibraries());
      return;
    }
    if (item instanceof ILibrary) {
      final ILibrary library = (ILibrary) item;
      addRelation(relations, RelationType.IMPLEMETS, library.getImplementedLibraries());
      addRelation(relations, RelationType.IMPLEMENTED_BY, library.getImplementedFromLibraries());
      addRelation(relations, RelationType.USE, library.getUsedLibraries());
      addRelation(relations, RelationType.USED_BY, library.getUsedByLibraries());
      return;
    }
  }

  private void collect(
      final IWorkspace workspace,
      final Map<DependencyRelationKey, IDependencyRelation> relations,
      final IPackage pakkage,
      final IPath path,
      final RelationType relationType) {

    final IType[] types = workspace.getTypes(path);
    for (final IType type : types) {
      collect(relations, pakkage, type, relationType);
    }
  }

  private void collect(
      final Map<DependencyRelationKey, IDependencyRelation> relations,
      final IPackage pakkage,
      final IType type,
      final RelationType relationType) {
    if (type.getPackage().equals(pakkage)) {
      return;
    }
    if (type.getLibrary().equals(pakkage.getLibrary())) {
      relations.put(new DependencyRelationKey(type.getPackage().getIdentifier(), relationType), new DependencyRelation(
          type.getPackage(),
          relationType));
      return;
    }
    relations.put(new DependencyRelationKey(type.getLibrary().getIdentifier(), relationType), new DependencyRelation(
        type.getLibrary(),
        relationType));
  }

  private void addRelation(
      final Map<DependencyRelationKey, IDependencyRelation> relations,
      final RelationType relation,
      final Iterable<ILibrary> libraries) {
    for (final ILibrary library : libraries) {
      relations
          .put(new DependencyRelationKey(library.getIdentifier(), relation), new DependencyRelation(library, relation));
    }
  }

  private void collectPathRelations(
      final Map<DependencyRelationKey, IDependencyRelation> relations,
      final IWorkspace workspace,
      final IType type,
      final Iterable<IPath> methodTypes,
      final RelationType use) {
    for (final IPath path : methodTypes) {
      if (type.getLibrary().containts(path)) {
        collectRelations(relations, workspace, type, Arrays.asList(type.getLibrary().getType(path)), use);
      } else {
        for (final ILibrary library : type.getLibrary().getUsedLibraries()) {
          if (library.containts(path)) {
            collectRelations(relations, workspace, type, Arrays.asList(library.getType(path)), use);
          }
        }
        for (final ILibrary library : type.getLibrary().getImplementedLibraries()) {
          if (library.containts(path)) {
            collectRelations(relations, workspace, type, Arrays.asList(library.getType(path)), use);
          }
        }
      }
    }
  }

  private void collectRelations(
      final Map<DependencyRelationKey, IDependencyRelation> relations,
      final IWorkspace workspace,
      final IType type,
      final Iterable<IType> types,
      final RelationType relation) {
    for (final IType iType : types) {
      if (!type.getLibrary().equals(iType.getLibrary())) {
        relations.put(new DependencyRelationKey(iType.getLibrary().getIdentifier(), relation), new DependencyRelation(
            iType.getLibrary(),
            relation));
        continue;
      }
      if (!type.getPath().getParent().equals(iType.getPath().getParent())) {
        final IPackage pakkage = workspace.getPackages().get(iType.getPath().getParent().getIdentifier());
        relations.put(new DependencyRelationKey(pakkage.getIdentifier(), relation), new DependencyRelation(
            pakkage,
            relation));
        continue;
      }
      relations
          .put(new DependencyRelationKey(iType.getIdentifier(), relation), new DependencyRelation(iType, relation));
    }
  }

  private void collectUseRelations(
      final Map<DependencyRelationKey, IDependencyRelation> relations,
      final IWorkspace workspace,
      final IType type,
      final Iterable<IImport> imports) {
    for (final IImport impcrt : imports) {
      final List<IType> list = workspace.getDuplicates().get(impcrt.getPath().getIdentifier());
      collectRelations(relations, workspace, type, list == null
          ? new ArrayList<IType>()
          : list, RelationType.USE);
    }
  }

  private void collectImplementsRelations(
      final Map<DependencyRelationKey, IDependencyRelation> relations,
      final IWorkspace workspace,
      final IType type,
      final Iterable<IPath> superTypes) {
    for (final IPath path : superTypes) {
      final List<IType> list = workspace.getDuplicates().get(path.getIdentifier());
      collectRelations(relations, workspace, type, list == null
          ? new ArrayList<IType>()
          : list, RelationType.IMPLEMETS);
    }
  }

  private IWorkspace getWorkspace(final IProgressMonitor monitor, final ICanceler canceler)
      throws JavaModelException,
        InterruptedException {
    if (this.dependenciesModel.get() != null) {
      return this.dependenciesModel.get();
    }
    final WorkspaceDependenciesInvestigator investigator =
        new WorkspaceDependenciesInvestigator(this.dependenciesModel.get(), this.logger, this.model, this.nameHitMaps);
    final IWorkspace workspace = investigator.investigate(monitor, canceler);
    this.dependenciesModel.set(workspace);
    return workspace;
  }
}
