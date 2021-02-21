// Copyright (c) 2010 by Andreas W. Bartels (bartels@anwiba.de)
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