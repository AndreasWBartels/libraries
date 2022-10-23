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
package net.anwiba.eclipse.project.dependency.view;

import net.anwiba.commons.eclipse.logging.ILogger;
import net.anwiba.commons.eclipse.utilities.JavaProjectUtilities;
import net.anwiba.commons.internal.eclipse.logging.Level;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IChangeableListListener;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectListModel;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectListModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.thread.cancel.Canceler;
import net.anwiba.eclipse.project.dependency.action.SaveGraphmlAction;
import net.anwiba.eclipse.project.dependency.action.SaveIntersectionGraphmlAction;
import net.anwiba.eclipse.project.dependency.action.SaveJarListAction;
import net.anwiba.eclipse.project.dependency.action.SaveNameHitMapsAction;
import net.anwiba.eclipse.project.dependency.action.ToggleEnabledAction;
import net.anwiba.eclipse.project.dependency.action.UpdateDependencyModelAction;
import net.anwiba.eclipse.project.dependency.java.IItem;
import net.anwiba.eclipse.project.dependency.java.IProject;
import net.anwiba.eclipse.project.dependency.java.IType;
import net.anwiba.eclipse.project.dependency.model.DependenciesModel;
import net.anwiba.eclipse.project.dependency.model.IDependenciesModel;
import net.anwiba.eclipse.project.dependency.object.IDependencyRelation;
import net.anwiba.eclipse.project.dependency.plugin.DependenciesPlugin;
import net.anwiba.eclipse.project.dependency.relation.RelationsTableViewerFactory;
import net.anwiba.eclipse.project.dependency.relation.ViewSiteRelationsListener;
import net.anwiba.eclipse.project.dependency.runner.INameHitMaps;
import net.anwiba.eclipse.project.dependency.runner.ListRunner;
import net.anwiba.eclipse.project.dependency.runner.NameHitMaps;
import net.anwiba.eclipse.project.dependency.type.TypesTableViewerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;

public class DependenciesView extends ViewPart {

  public static final String ID = "net.anwiba.eclipse.project.dependencies.view"; //$NON-NLS-1$
  private final WritableList<IDependencyRelation> descriptions =
      new WritableList<>(new ArrayList<IDependencyRelation>(), IDependencyRelation.class);
  private final WritableList<IType> types = new WritableList<>(new ArrayList<IType>(), IType.class);

  private TableViewer relationsViewer;
  private TableViewer typesViewer;
  private final IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot());
  private final IDependenciesModel dependenciesModel = new DependenciesModel();
  private final IObjectModel<IItem> selectedItemModel = new ObjectModel<>();
  private final IObjectListModel<IItem> selectedItemsModel = new ObjectListModel<>();
  private final IBooleanModel enableDependeciesTableModel = new BooleanModel(false);
  private final IBooleanModel enableNormalizeGraphModel = new BooleanModel(true);
  private final Canceler canceler = new Canceler(true);
  private final ILogger logger = DependenciesPlugin.getLogger();
  private final INameHitMaps nameHitMaps = new NameHitMaps();
  private ViewSiteRelationsListener relationsListener;
  private RelationsTableListener typesListener;
  private IDoubleClickListener doubleClickTableListener;

  private Action updateAction;
  // private Action showAction;
  private Action enableDependeciesTableToggleAction;
  private Action enableNormalizeGraphToggleAction;
  private Action saveNameHitMapsAction;
  private Action saveJarListAction;
  private Action saveGraphmlAction;
  private Action saveIntersectionGraphmlAction;

  private IPackagesViewPart packageExplorerView;

  public DependenciesView() {
    super();
  }

  @Override
  public void createPartControl(final Composite parent) {
    parent.setLayout(createLayout(1));
    parent.setLayoutData(new GridData(GridData.FILL_BOTH));
    final Label label = new Label(parent, SWT.HORIZONTAL);
    label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    // final Composite composite = createComposite(parent);
    final SashForm composite = new SashForm(parent, SWT.FILL);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    final IViewSite viewSite = getViewSite();
    final IWorkbenchWindow workbenchWindow = viewSite.getWorkbenchWindow();
    final IWorkbenchPage page = viewSite.getPage();
    final IWorkbenchPage activePage = workbenchWindow.getActivePage();
    final IProgressService progressService = workbenchWindow.getWorkbench().getProgressService();
    this.relationsViewer = new RelationsTableViewerFactory().createTable(composite, this.descriptions);
    if (this.relationsListener == null) {
      this.relationsListener = new ViewSiteRelationsListener(
          workbenchWindow,
          progressService,
          this.logger,
          this.model,
          this.selectedItemModel,
          this.selectedItemsModel,
          label,
          this.dependenciesModel,
          this.descriptions,
          this.nameHitMaps);
    }
    this.typesViewer = new TypesTableViewerFactory().createTable(composite, this.types);
    if (this.typesListener == null) {
      this.typesListener = new RelationsTableListener(
          this.logger,
          workbenchWindow,
          progressService,
          this.types,
          this.dependenciesModel,
          this.selectedItemModel);
      this.relationsViewer.addSelectionChangedListener(this.typesListener);
    }
    if (this.doubleClickTableListener == null) {
      initPackageExplorer(activePage.findView("org.eclipse.jdt.ui.PackageExplorer"));
    }
    this.enableDependeciesTableModel.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        final boolean isEnabled = DependenciesView.this.enableDependeciesTableModel.isTrue();
        DependenciesView.this.relationsListener.setEnabled(isEnabled);
        if (isEnabled) {
          fillTable(workbenchWindow, progressService, label);
        }
      }
    });
    activePage.addPartListener(new IPartListener() {

      @Override
      public void partActivated(final IWorkbenchPart part) {
        //
      }

      @Override
      public void partBroughtToTop(final IWorkbenchPart part) {
        //
      }

      @Override
      public void partClosed(final IWorkbenchPart part) {
        if (DependenciesView.this.packageExplorerView != null && DependenciesView.this.packageExplorerView == part) {
          disposePackageExplorerDependencies();
        }
      }

      @Override
      public void partDeactivated(final IWorkbenchPart part) {
        //
      }

      @Override
      public void partOpened(final IWorkbenchPart part) {
        if (part instanceof org.eclipse.jdt.ui.IPackagesViewPart) {
          initPackageExplorer(part);
        }
      }

    });

    // Create the help context id for the viewer's control
    PlatformUI.getWorkbench().getHelpSystem().setHelp(this.relationsViewer.getControl(), "Dependencies.viewer"); //$NON-NLS-1$
    PlatformUI.getWorkbench().getHelpSystem().setHelp(this.typesViewer.getControl(), "Types.viewer"); //$NON-NLS-1$
    makeActions();
    // hookContextMenu();
    hookDoubleClickAction();
    contributeToActionBars();

  }

  private GridLayout createLayout(final int n) {
    final GridLayout gridLayout = new GridLayout(n, true);
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    return gridLayout;
  }

  // private void hookContextMenu() {
  // final MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
  // manager.setRemoveAllWhenShown(true);
  // final Menu relationsMenu = manager.createContextMenu(this.relationsViewer.getControl());
  // this.relationsViewer.getControl().setMenu(relationsMenu);
  // getSite().registerContextMenu(manager, this.relationsViewer);
  // final Menu typesViewerMenu = manager.createContextMenu(this.typesViewer.getControl());
  // this.typesViewer.getControl().setMenu(typesViewerMenu);
  // getSite().registerContextMenu(manager, this.typesViewer);
  // }

  private void contributeToActionBars() {
    final IActionBars bars = getViewSite().getActionBars();
    fillLocalToolBar(bars.getToolBarManager());
  }

  private void fillLocalToolBar(final IToolBarManager manager) {
    manager.add(this.updateAction);
    manager.add(this.saveGraphmlAction);
    manager.add(this.saveIntersectionGraphmlAction);
    manager.add(this.enableNormalizeGraphToggleAction);
    manager.add(this.saveJarListAction);
    manager.add(this.saveNameHitMapsAction);
    manager.add(this.enableDependeciesTableToggleAction);
  }

  private void makeActions() {
    this.updateAction = new UpdateDependencyModelAction(
        getSite(),
        this.canceler,
        this.logger,
        this.model,
        this.dependenciesModel,
        this.nameHitMaps);
    this.enableDependeciesTableToggleAction = new ToggleEnabledAction(
        ISharedImages.IMG_ELCL_SYNCED,
        "switch enable/disable",
        this.enableDependeciesTableModel);
    this.enableNormalizeGraphToggleAction = new ToggleEnabledAction(
        ISharedImages.IMG_ELCL_COLLAPSEALL,
        "normalize graph enable/disable",
        this.enableNormalizeGraphModel);
    this.saveNameHitMapsAction = new SaveNameHitMapsAction(getSite(), this.logger, this.nameHitMaps);
    this.saveNameHitMapsAction.setEnabled(this.dependenciesModel.get() != null);
    this.saveJarListAction = new SaveJarListAction(getSite(), this.logger, this.dependenciesModel);
    this.saveJarListAction.setEnabled(this.dependenciesModel.get() != null);
    this.saveIntersectionGraphmlAction = new SaveIntersectionGraphmlAction(
        getSite(),
        this.logger,
        this.selectedItemsModel,
        this.dependenciesModel,
        this.enableDependeciesTableModel);
    this.saveIntersectionGraphmlAction.setEnabled(isSaveIntersectionEnabled());
    this.saveGraphmlAction =
        new SaveGraphmlAction(getSite(), this.logger, this.dependenciesModel, this.enableNormalizeGraphModel);
    this.saveGraphmlAction.setEnabled(this.dependenciesModel.get() != null);
    this.dependenciesModel.addChangeListener(() -> {
      this.saveNameHitMapsAction.setEnabled(this.dependenciesModel.get() != null);
      this.saveJarListAction.setEnabled(this.dependenciesModel.get() != null);
      this.saveGraphmlAction.setEnabled(this.dependenciesModel.get() != null);
      this.saveIntersectionGraphmlAction.setEnabled(isSaveIntersectionEnabled());
    });
    this.selectedItemsModel.addListModelListener(new IChangeableListListener<IItem>() {

      @Override
      public void objectsUpdated(final Iterable<Integer> arg0, final Iterable<IItem> arg1, final Iterable<IItem> arg2) {
        DependenciesView.this.saveIntersectionGraphmlAction.setEnabled(isSaveIntersectionEnabled());
      }

      @Override
      public void objectsRemoved(final Iterable<Integer> arg0, final Iterable<IItem> arg1) {
        DependenciesView.this.saveIntersectionGraphmlAction.setEnabled(isSaveIntersectionEnabled());
      }

      @Override
      public void objectsChanged(final Iterable<IItem> arg0, final Iterable<IItem> arg1) {
        DependenciesView.this.saveIntersectionGraphmlAction.setEnabled(isSaveIntersectionEnabled());
      }

      @Override
      public void objectsAdded(final Iterable<Integer> arg0, final Iterable<IItem> arg1) {
        DependenciesView.this.saveIntersectionGraphmlAction.setEnabled(isSaveIntersectionEnabled());
      }
    });
  }

  private boolean isSaveIntersectionEnabled() {
    return this.dependenciesModel.get() != null && !this.selectedItemsModel.isEmpty()
        && Streams.of(this.selectedItemsModel.values()).first(i -> i instanceof IProject).isAccepted();
  }

  private void hookDoubleClickAction() {
    // nothing to do
  }

  @Override
  public void setFocus() {
    this.relationsViewer.getControl().setFocus();
  }

  @Override
  public void dispose() {
    disposePackageExplorerDependencies();
    if (this.relationsListener != null) {
      this.relationsListener = null;
    }
    super.dispose();
  }

  private void disposePackageExplorerDependencies() {
    if (this.doubleClickTableListener != null && this.packageExplorerView != null) {
      this.relationsViewer.removeDoubleClickListener(this.doubleClickTableListener);
      this.typesViewer.removeDoubleClickListener(this.doubleClickTableListener);
      this.doubleClickTableListener = null;
      this.packageExplorerView.getTreeViewer().removeSelectionChangedListener(this.relationsListener);
      this.packageExplorerView = null;
    }
  }

  private void initPackageExplorer(final IWorkbenchPart part) {
    this.packageExplorerView = (IPackagesViewPart) part;
    if (this.packageExplorerView != null) {
      this.doubleClickTableListener = new ElementSelectingDoubleClickListener(this.packageExplorerView);
      this.packageExplorerView.getTreeViewer().addSelectionChangedListener(this.relationsListener);
      this.relationsViewer.addDoubleClickListener(this.doubleClickTableListener);
      this.typesViewer.addDoubleClickListener(this.doubleClickTableListener);
    }
  }

  private void fillTable(final IWorkbenchWindow context, final IProgressService progressService, final Label label) {
    try {
      final ISelection selection = this.packageExplorerView.getTreeViewer().getSelection();
      final IJavaElement[] elements = JavaProjectUtilities.getJavaElements(this.packageExplorerView, selection);
      final ListRunner runnable = new ListRunner(
          this.canceler,
          this.logger,
          elements,
          this.dependenciesModel,
          this.selectedItemModel,
          this.selectedItemsModel,
          label,
          this.model,
          this.descriptions,
          this.nameHitMaps);
      progressService.runInUI(context, runnable, null);
    } catch (final InvocationTargetException e) {
      this.logger.log(Level.WARNING, e);
    } catch (final InterruptedException e) {
      //
    }
  }
}
