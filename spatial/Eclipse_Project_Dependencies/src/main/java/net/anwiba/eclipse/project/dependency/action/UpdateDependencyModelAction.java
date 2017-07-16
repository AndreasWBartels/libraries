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
package net.anwiba.eclipse.project.dependency.action;

import net.anwiba.commons.eclipse.logging.ILogger;
import net.anwiba.commons.internal.eclipse.logging.Level;
import net.anwiba.commons.process.cancel.Canceler;
import net.anwiba.eclipse.project.dependency.java.IWorkspace;
import net.anwiba.eclipse.project.dependency.model.IDependenciesModel;
import net.anwiba.eclipse.project.dependency.plugin.DependenciesPlugin;
import net.anwiba.eclipse.project.dependency.runner.INameHitMaps;
import net.anwiba.eclipse.project.dependency.runner.UpdateRunner;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class UpdateDependencyModelAction extends Action {

  private static final String PROJECT_DEPENDENCIES = "Project Dependencies";
  private static final String TOOLTIP_TEXT = "Run";
  private static final String ERROR_MESSAGE = "Error gathering project dependencies";
  private final IShellProvider shellProvider;
  private final IDependenciesModel dependenciesModel;
  private final IJavaModel model;
  private final Canceler canceler;
  private final ILogger logger;
  private final INameHitMaps nameHitMaps;

  public UpdateDependencyModelAction(
    final IShellProvider shellProvider,
    final Canceler canceler,
    final ILogger logger,
    final IJavaModel model,
    final IDependenciesModel dependenciesModel,
    final INameHitMaps nameHitMaps) {
    this.logger = logger;
    this.nameHitMaps = nameHitMaps;
    setToolTipText(TOOLTIP_TEXT);
    setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
    this.shellProvider = shellProvider;
    this.canceler = canceler;
    this.model = model;
    this.dependenciesModel = dependenciesModel;
  }

  @Override
  public void run() {
    final ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(this.shellProvider.getShell());
    try {
      final UpdateRunner updateRunner =
          new UpdateRunner(this.canceler, this.logger, this.dependenciesModel, this.model, this.nameHitMaps);
      progressMonitorDialog.run(true, true, updateRunner);
      final IWorkspace workspace = this.dependenciesModel.get();
      if (workspace == null) {
        return;
      }
      final StringBuilder builder = new StringBuilder();
      builder.append("Scanned\n");
      builder.append("\n");
      builder.append(MessageFormat.format("\t{0,number,########0} Libraries\n", workspace.getLibraries().size()
          - workspace.getProjects().size()));
      builder.append(MessageFormat.format("\t{0,number,########0} Projects\n", workspace.getProjects().size()));
      builder.append(MessageFormat.format("\t{0,number,########0} Packages\n", workspace.getPackages().size()));
      builder.append(MessageFormat.format("\t{0,number,########0} Types\n", workspace.getTypes().size()));
      MessageDialog.openInformation(this.shellProvider.getShell(), PROJECT_DEPENDENCIES, builder.toString());
    } catch (final InvocationTargetException e) {
      this.logger.log(Level.ERROR, ERROR_MESSAGE, e.getCause());
      final Status status = new Status(IStatus.ERROR, PROJECT_DEPENDENCIES, IStatus.ERROR, ERROR_MESSAGE, e.getCause());
      DependenciesPlugin.getDefault().getLog().log(status);
      ErrorDialog.openError(this.shellProvider.getShell(), PROJECT_DEPENDENCIES, ERROR_MESSAGE, status);
    } catch (final RuntimeException e) {
      this.logger.log(Level.ERROR, ERROR_MESSAGE, e);
      final Status status = new Status(IStatus.ERROR, PROJECT_DEPENDENCIES, IStatus.ERROR, ERROR_MESSAGE, e);
      DependenciesPlugin.getDefault().getLog().log(status);
      ErrorDialog.openError(this.shellProvider.getShell(), PROJECT_DEPENDENCIES, ERROR_MESSAGE, status);
    } catch (final InterruptedException e) {
      // nothing to do
    }
  }

}
