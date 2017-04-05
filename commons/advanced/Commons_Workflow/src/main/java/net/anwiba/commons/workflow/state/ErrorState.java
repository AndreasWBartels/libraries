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

package net.anwiba.commons.workflow.state;

public class ErrorState<T> implements IState<T> {

  private final Throwable throwable;
  private final String text;
  private final String title;
  private final IState<T> followingState;

  public ErrorState(final String title, final String text, final Throwable throwable, final IState<T> followingState) {
    this.title = title;
    this.text = text;
    this.throwable = throwable;
    this.followingState = followingState;
  }

  public String getTitle() {
    return this.title;
  }

  public String getText() {
    return this.text;
  }

  public Throwable getThrowable() {
    return this.throwable;
  }

  @Override
  public IStateType getStateType() {
    return StateType.ERROR;
  }

  @Override
  public T getContext() {
    return null;
  }

  public IState<T> followingState() {
    return this.followingState;
  }

}
