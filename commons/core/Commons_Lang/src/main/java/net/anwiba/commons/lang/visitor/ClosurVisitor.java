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

import net.anwiba.commons.lang.functional.IClosure;

public class ClosurVisitor<K, O, E extends Exception> implements IVisitor<K, IClosure<O, E>> {

  private final Map<K, IClosure<O, E>> closure = new HashMap<>();
  private IClosure<O, E> defaultClosure = null;

  public ClosurVisitor() {
  }

  @Override
  public ClosurVisitor<K, O, E> ifCase(final IClosure<O, E> function, @SuppressWarnings("unchecked") final K... keys) {
    for (final K key : keys) {
      this.closure.put(key, function);
    }
    return this;
  };

  @Override
  public ClosurVisitor<K, O, E> defaultCase(final IClosure<O, E> function) {
    this.defaultClosure = function;
    return this;
  };

  public O accept(final K key) throws E {
    if (this.closure.containsKey(key)) {
      return this.closure.get(key).execute();
    }
    return this.defaultClosure == null ? null : this.defaultClosure.execute();
  }

}
