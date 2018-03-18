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

package net.anwiba.commons.lang.visitor;

import java.util.HashMap;
import java.util.Map;

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.ISupplier;

public class EnumSwitch<I, O, E extends Exception> {

  private final Map<I, ISupplier<O, E>> functions = new HashMap<>();
  private ISupplier<O, E> defaultFunction = null;

  private final IConverter<I, I, E> inputToKeyConverter;

  public EnumSwitch(final IConverter<I, I, E> iuputToKeyConverter) {
    this.inputToKeyConverter = iuputToKeyConverter;
  }

  public EnumSwitch<I, O, E> ifCase(final ISupplier<O, E> function, @SuppressWarnings("unchecked") final I... keys) {
    for (final I key : keys) {
      this.functions.put(key, function);
    }
    return this;
  };

  public EnumSwitch<I, O, E> defaultCase(final ISupplier<O, E> function) {
    this.defaultFunction = function;
    return this;
  };

  private O accept(final I key) throws E {
    if (this.functions.containsKey(key)) {
      return this.functions.get(key).supply();
    }
    if (this.defaultFunction != null) {
      return this.defaultFunction.supply();
    }
    throw new IllegalStateException();
  }

  public O switchTo(final I value) throws E {
    return accept(this.inputToKeyConverter.convert(value));
  }
}
