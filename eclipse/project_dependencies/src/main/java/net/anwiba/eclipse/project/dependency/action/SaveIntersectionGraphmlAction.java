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
package net.anwiba.eclipse.project.dependency.action;

import net.anwiba.commons.eclipse.logging.ILogger;
import net.anwiba.commons.internal.eclipse.logging.Level;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IObjectListModel;
import net.anwiba.eclipse.project.dependency.graph.GraphmlUtilities;
import net.anwiba.eclipse.project.dependency.graph.ProjectGraphBuilder;
import net.anwiba.eclipse.project.dependency.java.IItem;
import net.anwiba.eclipse.project.dependency.java.IProject;
import net.anwiba.eclipse.project.dependency.java.IWorkspace;
import net.anwiba.eclipse.project.dependency.model.IDependenciesModel;
import net.anwiba.tools.simple.graphml.generated.Graph;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class SaveIntersectionGraphmlAction extends Action {

  private final IShellProvider shellProvider;
  private final ILogger logger;
  private final IDependenciesModel dependenciesModel;
  private final IObjectListModel<IItem> selectedItems;
  private final IBooleanModel enableNormalizeGraphModel;

  public SaveIntersectionGraphmlAction(
    final IShellProvider shellProvider,
    final ILogger logger,
    final IObjectListModel<IItem> selectedItems,
    final IDependenciesModel dependenciesModel,
    final IBooleanModel enableNormalizeGraphModel) {
    this.shellProvider = shellProvider;
    this.logger = logger;
    this.selectedItems = selectedItems;
    this.dependenciesModel = dependenciesModel;
    this.enableNormalizeGraphModel = enableNormalizeGraphModel;
    setToolTipText("Save projects intersection dependencies as graphml-file");
    setImageDescriptor(PlatformUI
        .getWorkbench()
        .getSharedImages()
        .getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
  }

  @Override
  public void run() {
    final IWorkspace workspace = this.dependenciesModel.get();
    if (workspace == null) {
      return;
    }
    final FileDialog dialog = new FileDialog(this.shellProvider.getShell(), SWT.SAVE);
    dialog.setFilterExtensions(new String[] { "*.graphml" });
    dialog.setFileName("projectIntersection.graphml");
    final String filePath = dialog.open();
    if (filePath == null) {
      return;
    }
    final File file = new File(filePath);
    final ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(this.shellProvider.getShell());
    try {
      progressMonitorDialog.run(true, false, new IRunnableWithProgress() {

        @Override
        public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
          try {
            final ProjectGraphBuilder builder = new ProjectGraphBuilder(workspace);
            boolean flag = false;
            for (final IItem item : SaveIntersectionGraphmlAction.this.selectedItems.values()) {

              if (!(item instanceof IProject)) {
                continue;
              }
              final IProject project = (IProject) item;
              if (flag) {
                builder.intersect(project);
                continue;
              }
              builder.add(project);
              flag = true;
            }
            builder.setNormalize(SaveIntersectionGraphmlAction.this.enableNormalizeGraphModel.isTrue());
            final Graph graph = builder.build();
            GraphmlUtilities.saveAndLoad(file, graph);
          } catch (final IOException e) {
            throw new InvocationTargetException(e);
          }
        }
      });
    } catch (final InvocationTargetException e) {
      this.logger.log(Level.ERROR, e);
    } catch (final InterruptedException e) {
      // nothing to do
    }
  }
}
