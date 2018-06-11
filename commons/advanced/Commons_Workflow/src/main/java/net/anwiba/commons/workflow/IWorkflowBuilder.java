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

import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.workflow.state.IState;
import net.anwiba.commons.workflow.transition.ITransition;

public interface IWorkflowBuilder<T> {

  IWorkflowBuilder<T> add(ITransition<T> transition);

  IWorkflowBuilder<T> add(IApplicable<IState<T>> applicable, IExecutable<T> executable);

  IWorkflowBuilder<T> add(
      IApplicable<IState<T>> applicable,
      IFunction<IState<T>, IExecutable<T>, RuntimeException> factory);

  IWorkflowBuilder<T> setEventDispatchThreadExecuter();

  IWorkflowBuilder<T> setClosureDelegatorFactory(IExecuterFactory<T> closureDelegatorFactory);

  IWorkflowBuilder<T> setWorkflowController(IWorkflowController<T> workflowController);

  IWorkflow<T> build();

}
