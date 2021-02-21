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

import net.anwiba.commons.lang.functional.IBlock;

public class BlockVisitor<K, E extends Exception> implements IVisitor<K, IBlock<E>> {

  private final Map<K, IBlock<E>> functions = new HashMap<>();
  private IBlock<E> defaultFunction = null;

  public BlockVisitor() {
  }

  @Override
  public BlockVisitor<K, E> ifCase(final IBlock<E> function, @SuppressWarnings("unchecked") final K... keys) {
    for (final K key : keys) {
      this.functions.put(key, function);
    }
    return this;
  }

  @Override
  public BlockVisitor<K, E> defaultCase(final IBlock<E> function) {
    this.defaultFunction = function;
    return this;
  }

  public void accept(final K key) throws E {
    if (this.functions.containsKey(key)) {
      this.functions.get(key).execute();
    } else if (this.defaultFunction == null) {
      this.defaultFunction.execute();
    }
  }

}
