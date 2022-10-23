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
import net.anwiba.eclipse.project.dependency.runner.INameHitMaps;
import net.anwiba.eclipse.project.name.NameHitMapWriter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class SaveNameHitMapsAction extends Action {

  private final INameHitMaps nameHitMaps;
  private final IShellProvider shellProvider;
  private final ILogger logger;

  public SaveNameHitMapsAction(
    final IShellProvider shellProvider,
    final ILogger logger,
    final INameHitMaps nameHitMaps) {
    this.shellProvider = shellProvider;
    this.logger = logger;
    this.nameHitMaps = nameHitMaps;
    setToolTipText("Save name hit maps");
    setImageDescriptor(PlatformUI
        .getWorkbench()
        .getSharedImages()
        .getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
  }

  @Override
  public void run() {
    if (this.nameHitMaps.isEmpty()) {
      return;
    }
    final DirectoryDialog dialog = new DirectoryDialog(this.shellProvider.getShell(), SWT.SAVE);
    final String filePath = dialog.open();
    if (filePath == null) {
      return;
    }
    final File folder = new File(filePath);
    final ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(this.shellProvider.getShell());
    try {
      progressMonitorDialog.run(true, false, new IRunnableWithProgress() {

        @Override
        public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
          try {
            write(folder);
          } catch (final IOException exception) {
            throw new InvocationTargetException(exception);
          }
        }
      });
    } catch (final InvocationTargetException e) {
      this.logger.log(Level.ERROR, e);
    } catch (final InterruptedException e) {
      // nothing to do
    }
  }

  private void write(final File folder) throws IOException {
    final NameHitMapWriter writer = new NameHitMapWriter();

    writer.write(SaveNameHitMapsAction.this.nameHitMaps.getNameParts(), new File(folder, "NameParts.csv")); //$NON-NLS-1$
    writer.write(SaveNameHitMapsAction.this.nameHitMaps.getNamePrefixes(), new File(folder, "NamePrefixes.csv"));
    writer.write(SaveNameHitMapsAction.this.nameHitMaps.getNamePostfixes(), new File(folder, "NamePostfixes.csv"));
    writer.write(SaveNameHitMapsAction.this.nameHitMaps.getOneWordNames(), new File(folder, "OneWordNames.csv"));
    writer.write(SaveNameHitMapsAction.this.nameHitMaps.getUnmatchedNames(), new File(folder, "UnmatchedNames.csv"));
    writer.write(SaveNameHitMapsAction.this.nameHitMaps.getNames(), new File(folder, "Names.csv")); //$NON-NLS-1$
  }
}
