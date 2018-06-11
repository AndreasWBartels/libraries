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

import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.workflow.state.IState;
import net.anwiba.commons.workflow.transition.ITransition;
import net.anwiba.commons.workflow.transition.Transition;

public class WorkflowBuilder<T> implements IWorkflowBuilder<T> {

  private IExecuterFactory<T> closureDelegatorFactory = new DefaultDelegatingExecuterFactory<>();
  private final List<ITransition<T>> transitions = new ArrayList<>();
  private IWorkflowController<T> workflowController = null;

  @Override
  public IWorkflowBuilder<T> add(final IApplicable<IState<T>> applicable, final IExecutable<T> executable) {
    add(new Transition<>(applicable, s -> executable));
    return this;
  }

  @Override
  public IWorkflowBuilder<T> add(final ITransition<T> transition) {
    if (this.workflowController != null) {
      throw new IllegalArgumentException();
    }
    this.transitions.add(transition);
    return this;
  }

  @Override
  public IWorkflowBuilder<T> add(
      final IApplicable<IState<T>> applicable,
      final IFunction<IState<T>, IExecutable<T>, RuntimeException> factory) {
    add(new Transition<>(applicable, factory));
    return this;
  }

  @Override
  public IWorkflowBuilder<T> setClosureDelegatorFactory(final IExecuterFactory<T> closureDelegatorFactory) {
    this.closureDelegatorFactory = closureDelegatorFactory;
    return this;
  }

  @Override
  public IWorkflowBuilder<T> setWorkflowController(final IWorkflowController<T> workflowController) {
    if (!this.transitions.isEmpty()) {
      throw new IllegalArgumentException();
    }
    this.workflowController = workflowController;
    return this;
  }

  @Override
  public IWorkflowBuilder<T> setEventDispatchThreadExecuter() {
    this.closureDelegatorFactory = new EventDispatchThreadUsingDelegatingExecuterFactory<>();
    return this;
  }

  @Override
  public IWorkflow<T> build() {
    this.workflowController = new WorkflowController<>(this.transitions);
    return new Workflow<>(this.closureDelegatorFactory, this.workflowController);
  }

}
