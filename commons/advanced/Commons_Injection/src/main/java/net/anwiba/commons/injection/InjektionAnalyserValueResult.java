/*
 * #%L anwiba commons core %% Copyright (C) 2007 - 2016 Andreas Bartels %% This program is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 2.1 of the License,
 * or (at your option) any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Lesser Public License for more details. You should
 * have received a copy of the GNU General Lesser Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/lgpl-2.1.html>. #L%
 */

package net.anwiba.commons.injection;

public final class InjektionAnalyserValueResult implements IInjektionAnalyserValueResult {
  @SuppressWarnings("rawtypes")
  private final IBinding binding;
  private final boolean isNullable;
  private final boolean isIterable;
  private final boolean isEmptiable;

  public InjektionAnalyserValueResult(
      @SuppressWarnings("rawtypes") final IBinding type,
      final boolean isNullable,
      final boolean isIterable,
      final boolean isEmptiable) {
    this.binding = type;
    this.isNullable = isNullable;
    this.isIterable = isIterable;
    this.isEmptiable = isEmptiable;
  }

  @Override
  public boolean isNullable() {
    return this.isNullable;
  }

  @Override
  public boolean isEmptiable() {
    return this.isEmptiable;
  }

  @Override
  public boolean isIterable() {
    return this.isIterable;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public IBinding getBinding() {
    return this.binding;
  }
}
