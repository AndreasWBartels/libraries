// Copyright (c) 2010 by Andreas W. Bartels (bartels@anwiba.de)
package net.anwiba.eclipse.icons.view.listener;

import net.anwiba.commons.eclipse.utilities.JavaProjectUtilities;
import net.anwiba.commons.thread.cancel.Canceler;
import net.anwiba.eclipse.icons.GuiIconsViewPlugin;
import net.anwiba.eclipse.icons.description.IGuiIconDescription;
import net.anwiba.eclipse.icons.runner.UpdateRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Device;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.progress.IProgressService;

final public class ViewSiteListener implements ISelectionListener {

  private final WritableList<IGuiIconDescription> descriptions;
  private final Device device;
  private final IProgressService progressService;
  private Canceler canceler;
  private boolean isEnabled;
  private IJavaProject[] projects = new IJavaProject[0];

  public ViewSiteListener(final Device device, final IProgressService progressService, final WritableList<IGuiIconDescription> input) {
    this.device = device;
    this.progressService = progressService;
    this.descriptions = input;
  }

  @Override
  public synchronized void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if (!this.isEnabled) {
      projects = new IJavaProject[]{};
      return;
    }
    try {
      final IJavaProject[] projects = JavaProjectUtilities.getJavaProjects(part, selection);
      if (Arrays.equals(this.projects, projects)) {
        return;
      }
      this.projects = projects;
      this.canceler = createCanceler();
      final UpdateRunner runnable = new UpdateRunner(this.canceler, this.device, this.descriptions, projects);
      final IRunnableContext context = part.getSite().getWorkbenchWindow();
      this.progressService.runInUI(context, runnable, null);
    } catch (final InvocationTargetException exception) {
      GuiIconsViewPlugin.log(IStatus.ERROR, IStatus.ERROR, exception.getCause());
    } catch (final InterruptedException exception) {
      GuiIconsViewPlugin.log(IStatus.ERROR, IStatus.CANCEL, exception);
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