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
package net.anwiba.commons.utilities.visitor;

import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.lang.functional.IClosure;
import net.anwiba.commons.lang.functional.IFunction;

public class EnumVisitors {

  public static <K extends Enum<K>, I, O, E extends Exception> FunctionVisitor<K, I, O, E> ifCase(
      final IFunction<I, O, E> function,
      @SuppressWarnings("unchecked") final K... keys) {
    return new FunctionVisitor<K, I, O, E>().ifCase(function, keys);
  };

  public static <K extends Enum<K>, O, E extends Exception> ClosurVisitor<K, O, E> ifCase(
      final IClosure<O, E> function,
      @SuppressWarnings("unchecked") final K... keys) {
    return new ClosurVisitor<K, O, E>().ifCase(function, keys);
  };

  public static <K extends Enum<K>, E extends Exception> BlockVisitor<K, E> ifCase(
      final IBlock<E> function,
      @SuppressWarnings("unchecked") final K... keys) {
    return new BlockVisitor<K, E>().ifCase(function, keys);
  };
}
