/*
 * #%L
 * anwiba commons advanced
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
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
package net.anwiba.commons.workflow;

import net.anwiba.commons.lang.object.SingleUseObjectReceiver;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.workflow.state.IState;

public final class Workflow<T> implements IWorkflow<T> {

  private final IExecuterFactory<T> procedureDelegatorFactory;
  private final IWorkflowController<T> controller;

  public Workflow(final IExecuterFactory<T> closureDelegatorFactory, final IWorkflowController<T> workflowController) {
    this.procedureDelegatorFactory = closureDelegatorFactory;
    this.controller = workflowController;
  }

  @Override
  public void execute(final IState<T> state) {
    final IExecutable<T> executable = this.controller.next(state);
    if (executable == null) {
      return;
    }
    final ObjectModel<IState<T>> stateModel = new ObjectModel<>();
    final IChangeableObjectListener listener = createChangeListener(stateModel);
    stateModel.addChangeListener(listener);
    launch(executable, state, stateModel);
  }

  private IChangeableObjectListener createChangeListener(final ObjectModel<IState<T>> stateModel) {
    return new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        stateModel.removeChangeListener(this);
        execute(stateModel.get());
      }
    };
  }

  private void launch(
      final IExecutable<T> executable,
      @SuppressWarnings("unused") final IState<T> state,
      final ObjectModel<IState<T>> stateModel) {
    final IExecutable<T> delegatingClosure = this.procedureDelegatorFactory.create(executable);
    delegatingClosure.execute(new SingleUseObjectReceiver<>(stateModel));
  }
}