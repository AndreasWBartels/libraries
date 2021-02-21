// Copyright (c) 2010 by Andreas W. Bartels (bartels@anwiba.de)
package net.anwiba.eclipse.project.dependency.relation;

import net.anwiba.commons.eclipse.logging.ILogger;
import net.anwiba.commons.eclipse.utilities.JavaProjectUtilities;
import net.anwiba.commons.internal.eclipse.logging.Level;
import net.anwiba.commons.model.IObjectListModel;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.thread.cancel.Canceler;
import net.anwiba.eclipse.project.dependency.java.IItem;
import net.anwiba.eclipse.project.dependency.model.IDependenciesModel;
import net.anwiba.eclipse.project.dependency.object.IDependencyRelation;
import net.anwiba.eclipse.project.dependency.runner.INameHitMaps;
import net.anwiba.eclipse.project.dependency.runner.ListRunner;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.progress.IProgressService;

final public class ViewSiteRelationsListener implements ISelectionChangedListener {

  private final ILogger logger;
  private final IProgressService progressService;
  private Canceler canceler;
  private boolean isEnabled;
  private final IDependenciesModel dependenciesModel;
  private final IJavaModel model;
  private final WritableList<IDependencyRelation> descriptions;
  private final Label label;
  private final INameHitMaps nameHitMaps;
  private final IObjectModel<IItem> selectedItemModel;
  private final IObjectListModel<IItem> selectedItemsModel;
  private final IRunnableContext runnableContext;

  public ViewSiteRelationsListener(
    final IRunnableContext runnableContext,
    final IProgressService progressService,
    final ILogger logger,
    final IJavaModel model,
    final IObjectModel<IItem> selectedItemModel,
    final IObjectListModel<IItem> selectedItemsModel,
    final Label label,
    final IDependenciesModel dependenciesModel,
    final WritableList<IDependencyRelation> descriptions,
    final INameHitMaps nameHitMaps) {
    this.runnableContext = runnableContext;
    this.logger = logger;
    this.progressService = progressService;
    this.model = model;
    this.selectedItemModel = selectedItemModel;
    this.selectedItemsModel = selectedItemsModel;
    this.label = label;
    this.dependenciesModel = dependenciesModel;
    this.descriptions = descriptions;
    this.nameHitMaps = nameHitMaps;
  }

  @Override
  public void selectionChanged(final SelectionChangedEvent event) {
    if (!this.isEnabled) {
      return;
    }
    try {
      final IJavaElement[] elements = JavaProjectUtilities.getJavaElements((ITreeSelection) event.getSelection());
      this.canceler = createCanceler();
      final ListRunner runnable = new ListRunner(
          this.canceler,
          this.logger,
          elements,
          this.dependenciesModel,
          this.selectedItemModel,
          this.selectedItemsModel,
          this.label,
          this.model,
          this.descriptions,
          this.nameHitMaps);
      this.progressService.runInUI(this.runnableContext, runnable, null);
    } catch (final InvocationTargetException exception) {
      final Throwable cause = exception.getCause();
      this.logger.log(Level.ERROR, cause.getLocalizedMessage(), cause);
    } catch (final InterruptedException exception) {
      this.logger.log(Level.CANCEL, exception.getLocalizedMessage(), exception);
    }
  }

  private Canceler createCanceler() {
    if (this.canceler != null) {
      this.canceler.cancel();
    }
    return new Canceler(true);
  }

  public boolean isEnabled() {
    return this.isEnabled;
  }

  public void setEnabled(final boolean isEnabled) {
    this.isEnabled = isEnabled;
  }

}