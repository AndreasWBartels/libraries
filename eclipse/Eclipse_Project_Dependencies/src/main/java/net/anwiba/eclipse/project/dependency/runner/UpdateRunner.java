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
// Copyright (c) 2010 by Andreas W. Bartels
package net.anwiba.eclipse.project.dependency.runner;

import net.anwiba.commons.eclipse.logging.ILogger;
import net.anwiba.commons.thread.cancel.Canceler;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.eclipse.project.dependency.java.IWorkspace;
import net.anwiba.eclipse.project.dependency.model.IDependenciesModel;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jface.operation.IRunnableWithProgress;

public final class UpdateRunner implements IRunnableWithProgress {

  private final ICanceler canceler;
  private final IDependenciesModel dependenciesModel;
  private final IJavaModel model;
  private final ILogger logger;
  private final INameHitMaps nameHitMaps;

  public UpdateRunner(
    final ICanceler canceler,
    final ILogger logger,
    final IDependenciesModel dependenciesModel,
    final IJavaModel model,
    final INameHitMaps nameHitMaps) {
    this.logger = logger;
    this.dependenciesModel = dependenciesModel;
    this.canceler = canceler;
    this.model = model;
    this.nameHitMaps = nameHitMaps;
  }

  @Override
  public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
    try {
      monitor.beginTask("Gathering project dependencies", IProgressMonitor.UNKNOWN);
      final ICanceler canceler = new Canceler(true) {

        @Override
        public boolean isCanceled() {
          boolean canceled = monitor.isCanceled();
          boolean externalCanceled = UpdateRunner.this.canceler.isCanceled();
          return externalCanceled || canceled;
        }
      };
      final WorkspaceDependenciesInvestigator investigator =
          new WorkspaceDependenciesInvestigator(this.dependenciesModel.get(), this.logger, this.model, this.nameHitMaps);
      final IWorkspace workspace = investigator.investigate(monitor, canceler);
      this.dependenciesModel.set(workspace);
//      final DependenciesPathFileWriter writer = new DependenciesPathFileWriter(this.logger, this.model);
//      writer.write(canceler, workspace);
    } catch (final InterruptedException exception) {
      throw exception;
    } catch (final Exception exception) {
      throw new InvocationTargetException(exception);
    } finally {
      monitor.done();
    }
  }
}