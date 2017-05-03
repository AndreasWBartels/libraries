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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;

import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.collection.ObjectList;
import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IAccumulator;
import net.anwiba.commons.lang.functional.IConsumer;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IIterable;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

class SequencedStream<T, E extends Exception> implements IStream<T, E> {

  private final IIterable<T, E> iterable;

  SequencedStream(final IIterable<T, E> iterable) {
    this.iterable = iterable;
  }

  @Override
  public <O> IStream<O, E> convert(final IConverter<T, O, E> converter) {
    return new SequencedStream<>(new ConvertingIterableIterable<>(this.iterable, i -> i != null, converter));
  }

  @Override
  public IStream<T, E> distinct() {
    final Set<T> set = new HashSet<>();
    return filter(i -> !set.add(i));
  }

  @Override
  public IStream<T, E> filter(final IAcceptor<T> acceptor) {
    return new SequencedStream<>(new FilteringIterableIterable<>(this.iterable, acceptor));
  }

  @Override
  public void foreach(final IConsumer<T, E> consumer) throws E {
    this.iterable.foreach(consumer);
  }

  @Override
  public IOptional<T, E> first() throws E {
    return Optional.create(this.iterable.first(v -> true));
  }

  @Override
  public IOptional<T, E> first(final IAcceptor<T> acceptor) throws E {
    return Optional.create(this.iterable.first(acceptor));
  }

  @Override
  public Iterable<T> asIterable() throws E {
    return this.iterable.foreach(new LinkedList<T>(), (l, t) -> {
      l.add(t);
      return l;
    });
  }

  @Override
  public Collection<T> asCollection() throws E {
    return asList();
  }

  @Override
  public <O> List<O> asList() throws E {
    return this.iterable.foreach(new ArrayList<O>(), (l, t) -> {
      l.add((O) t);
      return l;
    });
  }

  @Override
  public IObjectList<T> asObjectList() throws E {
    return new ObjectList<>(asList());
  }

  @Override
  public <O> IOptional<O, E> foreach(final O identity, final IAccumulator<T, O, E> adder) throws E {
    return Optional.create(this.iterable.foreach(identity, adder));
  }

  @Override
  public <O> O[] asArray(final IntFunction<O[]> factory) throws E {
    final List<O> list = asList();
    return list.toArray(factory.apply(list.size()));
  }

}
