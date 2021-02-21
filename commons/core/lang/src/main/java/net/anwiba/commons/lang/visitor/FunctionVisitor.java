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

import net.anwiba.commons.lang.functional.IFunction;

public class FunctionVisitor<K, I, O, E extends Exception> implements IVisitor<K, IFunction<I, O, E>> {

  private final Map<K, IFunction<I, O, E>> functions = new HashMap<>();
  private IFunction<I, O, E> defaultFunction = null;

  public FunctionVisitor() {
  }

  @Override
  public FunctionVisitor<K, I, O, E> ifCase(
      final IFunction<I, O, E> function,
      @SuppressWarnings("unchecked") final K... keys) {
    for (final K key : keys) {
      this.functions.put(key, function);
    }
    return this;
  }

  @Override
  public FunctionVisitor<K, I, O, E> defaultCase(final IFunction<I, O, E> function) {
    this.defaultFunction = function;
    return this;
  }

  public O accept(final K key, final I value) throws E {
    if (this.functions.containsKey(key)) {
      return this.functions.get(key).execute(value);
    }
    if (this.defaultFunction != null) {
      return this.defaultFunction.execute(value);
    }
    throw new IllegalStateException();
    // return Optional
    // .<IFunction<I, O, E>, E> create(this.defaultFunction)
    // .ifNullThrow()
    // .convert(f -> f.execute(value))
    // .get();
  }
}
