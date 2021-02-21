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
package net.anwiba.commons.workflow.transition;

import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.workflow.IExecutable;
import net.anwiba.commons.workflow.state.IState;

public class Transition<T> implements ITransition<T> {

  private final IApplicable<IState<T>> applicable;
  private final IFunction<IState<T>, IExecutable<T>, RuntimeException> factory;

  public Transition(
      final IApplicable<IState<T>> applicable,
      final IFunction<IState<T>, IExecutable<T>, RuntimeException> factory) {
    this.applicable = applicable;
    this.factory = factory;
  }

  @Override
  public boolean isApplicable(final IState<T> state) {
    return this.applicable.isApplicable(state);
  }

  @Override
  public IExecutable<T> getExecutable(final IState<T> state) {
    return this.factory.execute(state);
  }

}