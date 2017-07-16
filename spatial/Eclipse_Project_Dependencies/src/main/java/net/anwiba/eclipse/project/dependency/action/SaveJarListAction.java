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
import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IWorkspace;
import net.anwiba.eclipse.project.dependency.model.IDependenciesModel;
import net.anwiba.eclipse.project.name.NameHitMap;
import net.anwiba.eclipse.project.name.NameHitMapWriter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class SaveJarListAction extends Action {

  private final IShellProvider shellProvider;
  private final ILogger logger;
  private final IDependenciesModel dependenciesModel;

  public SaveJarListAction(
    final IShellProvider shellProvider,
    final ILogger logger,
    final IDependenciesModel dependenciesModel) {
    this.shellProvider = shellProvider;
    this.logger = logger;
    this.dependenciesModel = dependenciesModel;
    setToolTipText("Save used libraries list");
    setImageDescriptor(PlatformUI
        .getWorkbench()
        .getSharedImages()
        .getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
  }

  @Override
  public void run() {
    final IWorkspace workspace = this.dependenciesModel.get();
    if (workspace == null || workspace.getLibraries().isEmpty()) {
      return;
    }
    final FileDialog dialog = new FileDialog(this.shellProvider.getShell(), SWT.SAVE);
    dialog.setFilterExtensions(new String[] { "*.cvs" });
    dialog.setFileName("libraries.cvs");
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
          final Map<String, ILibrary> libraries = workspace.getLibraries();
          final NameHitMap nameHitMap = new NameHitMap();
          for (final ILibrary library : libraries.values()) {
            final String name = getName(library.getName());
            for (@SuppressWarnings("unused")
            final ILibrary usedByLibrary : library.getUsedByLibraries()) {
              nameHitMap.add(name);
            }
          }
          try {
            write(file, nameHitMap);
          } catch (final IOException exception) {
            throw new InvocationTargetException(exception);
          }
        }

        private String getName(final String name) {
          final String substring = name.substring(name.lastIndexOf("/") + 1);
          return substring;
        }
      });
    } catch (final InvocationTargetException e) {
      this.logger.log(Level.ERROR, e);
    } catch (final InterruptedException e) {
      // nothing to do
    }
  }

  private void write(final File file, final NameHitMap nameHitMap) throws IOException {
    final NameHitMapWriter writer = new NameHitMapWriter();
    writer.write(nameHitMap, file);
  }
}
