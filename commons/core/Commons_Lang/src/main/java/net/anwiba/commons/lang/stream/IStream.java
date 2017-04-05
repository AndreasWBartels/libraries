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

package net.anwiba.commons.lang.stream;

import java.util.Collection;
import java.util.List;

import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IAccumulator;
import net.anwiba.commons.lang.functional.IConsumer;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.optional.IOptional;

public interface IStream<T, E extends Exception> {

  public IStream<T, E> distinct();

  public IStream<T, E> filter(IAcceptor<T> funtion);

  public <O> IStream<O, E> convert(IConverter<T, O, E> funtion);

  public Iterable<T> asIterable() throws E;

  public Collection<T> asCollection() throws E;

  public <O> List<O> asList() throws E;

  public IObjectList<T> asObjectList() throws E;

  public IOptional<T, E> first() throws E;

  public IOptional<T, E> first(IAcceptor<T> acceptor) throws E;

  public <O> IOptional<O, E> foreach(O inital, IAccumulator<T, O, E> accumulator) throws E;

  public void foreach(IConsumer<T, E> consumer) throws E;

}
