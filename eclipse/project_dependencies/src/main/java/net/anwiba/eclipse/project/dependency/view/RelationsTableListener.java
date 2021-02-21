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
package net.anwiba.eclipse.project.dependency.view;

import net.anwiba.commons.eclipse.logging.ILogger;
import net.anwiba.commons.internal.eclipse.logging.Level;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.eclipse.project.dependency.java.IImport;
import net.anwiba.eclipse.project.dependency.java.IItem;
import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IPackage;
import net.anwiba.eclipse.project.dependency.java.IPath;
import net.anwiba.eclipse.project.dependency.java.IType;
import net.anwiba.eclipse.project.dependency.java.ITypeContainer;
import net.anwiba.eclipse.project.dependency.java.IWorkspace;
import net.anwiba.eclipse.project.dependency.model.IDependenciesModel;
import net.anwiba.eclipse.project.dependency.object.DependencyRelation;
import net.anwiba.eclipse.project.dependency.object.RelationType;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.progress.IProgressService;

public class RelationsTableListener implements ISelectionChangedListener {

  private final WritableList<IType> types;
  private final IDependenciesModel dependenciesModel;
  private boolean isEnabled;
  private final IObjectModel<IItem> selectedItemModel;
  private final IProgressService progressService;
  private final IRunnableContext context;
  private final ILogger logger;

  public RelationsTableListener(
    final ILogger logger,
    final IRunnableContext context,
    final IProgressService progressService,
    final WritableList<IType> types,
    final IDependenciesModel dependenciesModel,
    final IObjectModel<IItem> selectedItemModel) {
    this.logger = logger;
    this.context = context;
    this.progressService = progressService;
    this.types = types;
    this.dependenciesModel = dependenciesModel;
    this.selectedItemModel = selectedItemModel;
  }

  @Override
  public void selectionChanged(final SelectionChangedEvent event) {
    this.types.clear();
    if (this.dependenciesModel.get() == null || this.selectedItemModel.get() == null) {
      return;
    }
    final ISelection selection = event.getSelection();
    if (selection.isEmpty()
        || !(selection instanceof StructuredSelection || ((StructuredSelection) selection).size() != 1)) {
      return;
    }
    final IWorkspace workspace = this.dependenciesModel.get();
    final IItem selectedItem = this.selectedItemModel.get();

    try {
      final RelationUpdateRunner runner = new RelationUpdateRunner(workspace, selectedItem, selection, this.types);
      this.progressService.runInUI(this.context, runner, null);
    } catch (final InvocationTargetException e) {
      this.logger.log(Level.ERROR, e);
    } catch (final InterruptedException e) {
      //
    }
  }

  public static class RelationUpdateRunner implements IRunnableWithProgress {

    private final IWorkspace workspace;
    private final IItem selectedItem;
    private final ISelection selection;
    private final WritableList<IType> types;

    public RelationUpdateRunner(
      final IWorkspace workspace,
      final IItem selectedItem,
      final ISelection selection,
      final WritableList<IType> types) {
      this.workspace = workspace;
      this.selectedItem = selectedItem;
      this.selection = selection;
      this.types = types;
    }

    @Override
    public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
      if (this.selectedItem instanceof IType) {
        addTypeDependencies(this.workspace, this.selectedItem, this.selection);
        return;
      }
      if (this.selectedItem instanceof ILibrary) {
        addLibraryDependencies(this.workspace, this.selectedItem, this.selection);
        return;
      }
      if (this.selectedItem instanceof IPackage) {
        addPackageDependencies(this.workspace, this.selectedItem, this.selection);
        return;
      }
    }

    private void addPackageDependencies(
        final IWorkspace workspace,
        final IItem selectedItem,
        final ISelection selection) {
      final IPackage selectedType = (IPackage) selectedItem;
      final StructuredSelection structuredSelection = (StructuredSelection) selection;
      final Object firstElement = structuredSelection.getFirstElement();
      if (!(firstElement instanceof DependencyRelation)) {
        return;
      }
      final DependencyRelation dependencyRelation = (DependencyRelation) firstElement;
      final IItem item = dependencyRelation.getItem();
      if (!(item instanceof ITypeContainer)) {
        return;
      }
      final RelationType relationType = dependencyRelation.getRelationType();
      final ITypeContainer typeContainer = (ITypeContainer) item;
      final Set<IType> relatedTypes = new HashSet<>();
      switch (relationType) {
        case IMPLEMENTED_BY: {
          for (final IType type : typeContainer.getTypes()) {
            for (final IType usedType : workspace.getImplemented(type)) {
              if (usedType.getPackage().isParent(selectedType)) {
                relatedTypes.add(type);
              }
            }
          }
        }
        case IMPLEMETS: {
          for (final IType type : typeContainer.getTypes()) {
            for (final IType usedType : type.getImplementedBy()) {
              if (usedType.getPackage().equals(selectedType)) {
                relatedTypes.add(type);
              }
            }
          }
        }
        case USE: {
          for (final IType type : typeContainer.getTypes()) {
            for (final IType usedType : type.getUsedBy()) {
              if (usedType.getPackage().equals(selectedType)) {
                relatedTypes.add(type);
              }
            }
          }
        }
        case USED_BY: {
          for (final IType type : typeContainer.getTypes()) {
            for (final IType usedType : workspace.getUsed(type)) {
              if (usedType.getPackage().equals(selectedType)) {
                relatedTypes.add(type);
              }
            }
          }
        }
      }
      this.types.addAll(relatedTypes);
    }

    private void addLibraryDependencies(
        final IWorkspace workspace,
        final IItem selectedItem,
        final ISelection selection) {
      final ILibrary selectedType = (ILibrary) selectedItem;
      final StructuredSelection structuredSelection = (StructuredSelection) selection;
      final Object firstElement = structuredSelection.getFirstElement();
      if (!(firstElement instanceof DependencyRelation)) {
        return;
      }
      final DependencyRelation dependencyRelation = (DependencyRelation) firstElement;
      final ILibrary item = (ILibrary) dependencyRelation.getItem();
      final RelationType relationType = dependencyRelation.getRelationType();
      final Set<IType> relatedTypes = new HashSet<>();
      switch (relationType) {
        case IMPLEMENTED_BY: {
          for (final IType type : item.getTypes()) {
            for (final IType usedType : workspace.getImplemented(type)) {
              if (usedType.getLibrary().equals(selectedType)) {
                relatedTypes.add(type);
              }
            }
          }
        }
        case IMPLEMETS: {
          for (final IType type : item.getTypes()) {
            for (final IType usedType : type.getImplementedBy()) {
              if (usedType.getLibrary().equals(selectedType)) {
                relatedTypes.add(type);
              }
            }
          }
        }
        case USE: {
          for (final IType type : item.getTypes()) {
            for (final IType usedType : type.getUsedBy()) {
              if (usedType.getLibrary().equals(selectedType)) {
                relatedTypes.add(type);
              }
            }
          }
        }
        case USED_BY: {
          for (final IType type : item.getTypes()) {
            for (final IType usedType : workspace.getUsed(type)) {
              if (usedType.getLibrary().equals(selectedType)) {
                relatedTypes.add(type);
              }
            }
          }
        }
      }
      this.types.addAll(relatedTypes);
    }

    private void addTypeDependencies(final IWorkspace workspace, final IItem selectedItem, final ISelection selection) {
      final IType selectedType = (IType) selectedItem;
      final StructuredSelection structuredSelection = (StructuredSelection) selection;
      final Object firstElement = structuredSelection.getFirstElement();
      if (!(firstElement instanceof DependencyRelation)) {
        return;
      }
      final DependencyRelation dependencyRelation = (DependencyRelation) firstElement;
      final IItem item = dependencyRelation.getItem();
      final RelationType relationType = dependencyRelation.getRelationType();
      if (!(item instanceof ITypeContainer)) {
        return;
      }
      final ITypeContainer typeContainer = (ITypeContainer) item;
      final List<IType> relatedTypes = new ArrayList<>();
      switch (relationType) {
        case IMPLEMENTED_BY: {
          for (final IType type : selectedType.getImplementedBy()) {
            relatedTypes.add(type);
          }
        }
        case IMPLEMETS: {
          for (final IPath path : selectedType.getSuperTypes()) {
            relatedTypes.addAll(Stream.of(workspace.getTypes(path)).collect(Collectors.toList()));
          }
        }
        case USE: {
          for (final IImport impcrt : selectedType.getImports()) {
            relatedTypes.addAll(Stream.of(workspace.getTypes(impcrt.getPath())).collect(Collectors.toList()));
          }
          for (final IPath path : selectedType.getMethodTypes()) {
            relatedTypes.addAll(Stream.of(workspace.getTypes(path)).collect(Collectors.toList()));
          }
        }
        case USED_BY: {
          for (final IType type : selectedType.getUsedBy()) {
            relatedTypes.add(type);
          }
        }
      }
      this.types.addAll(relatedTypes.stream().filter(t -> typeContainer.containts(t)).collect(Collectors.toSet()));
    }
  }

  public boolean isEnabled() {
    return this.isEnabled;
  }

  public void setEnabled(final boolean isEnabled) {
    this.isEnabled = isEnabled;
  }
}
