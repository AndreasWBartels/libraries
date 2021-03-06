/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2016 Andreas W. Bartels
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
package net.anwiba.commons.model;

public final class UnchangableBooleanModel implements IBooleanModel {

  boolean value = false;

  public UnchangableBooleanModel(final boolean value) {
    this.value = value;
  }

  @Override
  public void removeChangeListener(final IChangeableObjectListener listener) {
    // nothing to do
  }

  @Override
  public void addChangeListener(final IChangeableObjectListener listener) {
    // nothing to do
  }

  @Override
  public void removeChangeListeners() {
    // nothing to do
  }

  @Override
  public boolean isTrue() {
    return this.value;
  }

  @Override
  public void set(final boolean value) {
    // nothing to do
  }
}
