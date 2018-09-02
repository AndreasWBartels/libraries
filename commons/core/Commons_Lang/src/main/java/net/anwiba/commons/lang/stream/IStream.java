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
import java.util.Map;
import java.util.function.IntFunction;

import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IAggregator;
import net.anwiba.commons.lang.functional.IAssimilator;
import net.anwiba.commons.lang.functional.IConsumer;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.lang.functional.ISupplier;
import net.anwiba.commons.lang.optional.IOptional;

public interface IStream<T, E extends Exception> {

  IStream<T, E> distinct();

  IStream<T, E> filter(IAcceptor<T> funtion);

  <O> IStream<O, E> convert(IConverter<T, O, E> funtion);

  <O> IStream<O, E> flat(IConverter<T, Iterable<O>, E> funtion);

  <O> IStream<O, E> convert(IAggregator<Integer, T, O, E> aggregator);

  <O> IStream<O, E> instanceOf(Class<O> clazz);

  IStream<T, E> foreach(IConsumer<T, E> consumer);

  IStream<T, E> foreach(IAssimilator<Integer, T, E> assimilator);

  IStream<T, E> notNull();

  IStream<T, E> revert();

  IStream<T, E> failed(ISupplier<Iterable<T>, E> supplier);

  Iterable<T> asIterable() throws E;

  Collection<T> asCollection() throws E;

  <O> List<O> asList() throws E;

  <K, V> Map<K, V> asMap(IFactory<T, K, E> keyFactrory, IFactory<T, V, E> valueFactrory) throws E;

  <O> O[] asArray(IntFunction<O[]> function) throws E;

  IObjectList<T> asObjectList() throws E;

  IOptional<T, E> first();

  IOptional<T, E> first(IAcceptor<T> acceptor);

  <O> IOptional<O, E> aggregate(O inital, IAggregator<O, T, O, E> aggregator);

}
