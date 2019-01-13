/*
 * #%L
 * anwiba commons core
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

package net.anwiba.commons.injection.impl;

import net.anwiba.commons.injection.IBinding;
import net.anwiba.commons.injection.IInjectingFactory;

public interface IInjektionAnalyserResult extends IInjektionObjectDescription {

  @SuppressWarnings("rawtypes")
  IInjektionAnalyserResult UNRESOLVEABLE = new IInjektionAnalyserResult() {

    @Override
    public boolean isFactory() {
      return false;
    }

    @Override
    public Class getType() {
      throw new UnsupportedOperationException();
    }

    @Override
    public IInjectingFactory getFactory() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isNullable(final IBinding binding) {
      return false;
    }

    @Override
    public boolean isIterable(final IBinding binding) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isIndependent() {
      return false;
    }

    @Override
    public boolean isEmptiable(final IBinding binding) {
      return false;
    }

    @Override
    public boolean hasNullable() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasIterable() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<IBinding> getBindings() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isImitable(final IBinding binding) {
      return false;
    }
  };

  @SuppressWarnings("rawtypes")
  Iterable<IBinding> getBindings();

  @SuppressWarnings("rawtypes")
  boolean isIterable(IBinding binding);

  boolean hasIterable();

  boolean hasNullable();

  @SuppressWarnings("rawtypes")
  boolean isNullable(IBinding binding);

  boolean isIndependent();

  @SuppressWarnings("rawtypes")
  boolean isEmptiable(IBinding binding);

  @SuppressWarnings("rawtypes")
  boolean isImitable(IBinding binding);

}