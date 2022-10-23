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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

package net.anwiba.commons.lang.stream;

import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.collection.ObjectList;
import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IAggregator;
import net.anwiba.commons.lang.functional.IAssimilator;
import net.anwiba.commons.lang.functional.ICloseable;
import net.anwiba.commons.lang.functional.IConsumer;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.lang.functional.IIntAssimilator;
import net.anwiba.commons.lang.functional.IIterable;
import net.anwiba.commons.lang.functional.ISupplier;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class SequencedStream<T, E extends Exception> implements IStream<T, E> {

  private final IIterable<T, E> iterable;
  private final Class<E> exceptionClass;
  private final ICloseable<E> closeable;

  //  SequencedStream(final Class<E> exceptionClass, final IIterable<T, E> iterable) {
  //    this(exceptionClass, iterable, () -> {});
  //  }

  SequencedStream(final Class<E> exceptionClass, final IIterable<T, E> iterable, final ICloseable<E> closeable) {
    this.exceptionClass = exceptionClass;
    this.iterable = iterable;
    this.closeable = closeable;
  }

  @Override
  public <O> IStream<O, E> convert(final IConverter<T, O, E> converter) {
    return new SequencedStream<>(
        this.exceptionClass,
        new IterableConvertingIterable<>(this.iterable, i -> i != null, converter),
        this.closeable);
  }

  @Override
  public <O> IStream<O, E> convert(final IAggregator<Integer, T, O, E> aggegator) {
    return new SequencedStream<>(
        this.exceptionClass,
        new IterableCountingIterable<>(this.iterable, i -> i != null, aggegator),
        this.closeable);
  }

  @Override
  public IStream<T, E> sort(final Comparator<T> comparator) {
    try {
      List<T> visited = new LinkedList<>();
      this.iterable.foreach(t -> {
        visited.add(t);
      });
      Collections.sort(visited, comparator);
      return Streams.of(this.exceptionClass, visited, this.closeable);
    } catch (final Exception exception) {
      return stream(this.exceptionClass, exception, this.closeable);
    }
  }

  @Override
  public IStream<T, E> distinct() {
    final Set<T> set = new LinkedHashSet<>();
    return filter(i -> set.add(i));
  }

  @Override
  public IStream<T, E> filter(final IAcceptor<T> acceptor) {
    return new SequencedStream<>(
        this.exceptionClass,
        new IterableFilteringIterable<>(this.iterable, acceptor),
        this.closeable);
  }

  @Override
  public IStream<T, E> notNull() {
    return filter(v -> v != null);
  }

  @Override
  public <O> IStream<O, E> instanceOf(final Class<O> clazz) {
    return filter(i -> clazz.isInstance(i)).convert(i -> clazz.cast(i));
  }

  @Override
  public IStream<T, E> foreach(final IConsumer<T, E> consumer) {
    try {
      List<T> visited = new LinkedList<>();
      this.iterable.foreach(t -> {
        consumer.consume(t);
        visited.add(t);
      });
      return Streams.of(this.exceptionClass, visited, this.closeable);
    } catch (final Exception exception) {
      return stream(this.exceptionClass, exception, this.closeable);
    }
  }

  @Override
  public IStream<T, E> foreachAsOptional(final IConsumer<IOptional<T, E>, E> consumer) {
    try {
      List<T> visited = new LinkedList<>();
      this.iterable.foreach(t -> {
        consumer.consume(Optional.of(this.exceptionClass, t));
        visited.add(t);
      });
      return Streams.of(this.exceptionClass, visited, this.closeable);
    } catch (final Exception exception) {
      return stream(this.exceptionClass, exception, this.closeable);
    }
  }

  @Override
  public IStream<T, E> foreach(final IAssimilator<Integer, T, E> assimilator) {
    try {
      List<T> visited = new LinkedList<>();
      this.iterable.foreach((i, t) -> {
        assimilator.assimilate(i, t);
        visited.add(t);
      });
      return Streams.of(this.exceptionClass, visited, this.closeable);
    } catch (final Exception exception) {
      return stream(this.exceptionClass, exception, this.closeable);
    }
  }

  @Override
  public IStream<T, E> foreach(final int initial, final IIntAssimilator<T, E> assimilator) {
    try {
      List<T> visited = new LinkedList<>();
      this.iterable.foreach(initial, (i, t) -> {
        assimilator.assimilate(i, t);
        visited.add(t);
      });
      return Streams.of(this.exceptionClass, visited, this.closeable);
    } catch (final Exception exception) {
      return stream(this.exceptionClass, exception, this.closeable);
    }
  }

  @Override
  public IStream<T, E> failed(final ISupplier<Iterable<T>, E> supplier) {
    try {
      return Streams.of(this.exceptionClass, supplier.supply(), this.closeable);
    } catch (final Exception exception) {
      return stream(this.exceptionClass, exception, this.closeable);
    }
  }

  @Override
  public IOptional<T, E> first() {
    try {
      return Optional.of(this.exceptionClass, this.iterable.first(v -> true));
    } catch (final Exception exception) {
      return optional(this.exceptionClass, exception);
    }
  }

  @Override
  public IOptional<T, E> first(final IAcceptor<T> acceptor) {
    try {
      return Optional.of(this.exceptionClass, this.iterable.first(acceptor));
    } catch (final Exception exception) {
      return optional(this.exceptionClass, exception);
    }
  }

  @Override
  public boolean foundAny() throws E {
    return this.iterable.iterator().hasNext();
  }

  @Override
  public boolean foundAny(final IAcceptor<T> acceptor) throws E {
    return first(acceptor).isAccepted();
  }

  @Override
  public <O> IOptional<O, E> aggregate(final O identity, final IAggregator<O, T, O, E> aggegator) {
    try {
      return Optional.of(this.exceptionClass, this.iterable.aggregate(identity, aggegator));
    } catch (final Exception exception) {
      return optional(this.exceptionClass, exception);
    }
  }

  @Override
  public <O> Collection<O> asCollection() throws E {
    return asList();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <O> List<O> asList() throws E {
    return this.iterable.aggregate(new ArrayList<O>(), (l, t) -> {
      l.add((O) t);
      return l;
    });
  }

  @Override
  public <O> Iterable<O> asIterable() throws E {
    return this.iterable.aggregate(new ArrayList<O>(), (l, t) -> {
      l.add((O) t);
      return l;
    });
  }

  @Override
  public <O> Iterator<O> asIterator() throws E {
    return this.iterable.aggregate(new ArrayList<O>(), (l, t) -> {
      l.add((O) t);
      return l;
    }).iterator();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <O> Set<O> asSet() throws E {
    return this.iterable.aggregate(new LinkedHashSet<O>(), (l, t) -> {
      l.add((O) t);
      return l;
    });
  }

  @Override
  public <O> IObjectList<O> asObjectList() throws E {
    return new ObjectList<>(asList());
  }

  @Override
  public <O> O[] asArray(final IntFunction<O[]> factory) throws E {
    return asList().toArray(factory.apply(asList().size()));
  }

  @Override
  public <K, V> Map<K, V> asMap(
      final IFactory<T, K, E> keyFactrory,
      final IFactory<T, V, E> valueFactrory)
      throws E {
    return this.iterable.aggregate(new LinkedHashMap<K, V>(), (l, t) -> {
      l.put(keyFactrory.create(t), valueFactrory.create(t));
      return l;
    });
  }

  @Override
  public IStream<T, E> revert() {
    try {
      final List<T> list = asList();
      Collections.reverse(list);
      return Streams.of(this.exceptionClass, list, this.closeable);
    } catch (final Exception exception) {
      return stream(this.exceptionClass, exception, this.closeable);
    }
  }

  @Override
  public <O> IStream<O, E> flat(final IConverter<T, Iterable<O>, E> converter) {
    return new SequencedStream<>(
        this.exceptionClass,
        new IterableFlattingIterable<>(this.iterable, i -> i != null, converter),
        this.closeable);
  }

  @SuppressWarnings("unchecked")
  private static <T, E extends Exception> IStream<T, E> stream(
      final Class<E> exceptionClass,
      final Exception exception,
      final ICloseable<E> closeable) {
    if (exceptionClass.isInstance(exception)) {
      return new FailedStream<>(exceptionClass, (E) exception, closeable);
    }
    if (exception instanceof RuntimeException) {
      throw (RuntimeException) exception;
    }
    throw new RuntimeException(exception.getMessage(), exception);
  }

  @SuppressWarnings("unchecked")
  private static <T, E extends Exception> IOptional<T, E> optional(
      final Class<E> exceptionClass,
      final Exception exception) {
    if (exceptionClass.isInstance(exception)) {
      return Optional.failed(exceptionClass, (E) exception);
    }
    if (exception instanceof RuntimeException) {
      throw (RuntimeException) exception;
    }
    throw new RuntimeException(exception.getMessage(), exception);
  }

  @Override
  public IStream<T, E> throwIfFailed() throws E {
    return this;
  }

  @Override
  public boolean isEmpty() throws E {
    return !this.iterable.iterator().hasNext();
  }

  @Override
  public boolean isSuccessful() {
    return true;
  }

  @Override
  public Iterable<T> toIterable() throws E {
    return this.iterable.aggregate(new LinkedList<T>(), (l, t) -> {
      l.add(t);
      return l;
    });
  }

  @Override
  public Iterator<T> iterator() {
    try {
      return toIterable().iterator();
    } catch (Exception exception) {
      throw new IllegalStateException(exception.getMessage(), exception);
    }
  }

  @Override
  public <O> Stream<O> asStream() throws E {
    return toStream().map(o -> (O) o);
  }

  @Override
  public Stream<T> toStream() throws E {
    final Stream<T> stream = StreamSupport.stream(this.iterable.spliterator(), false);
    stream.onClose(() -> {
      if (this.closeable == null) {
        return;
      }
      try {
        this.closeable.close();
      } catch (Exception exception) {
        throw new RuntimeException(exception.getMessage(), exception);
      }
    });
    return stream;
  }

  @Override
  public void close() throws E {
    if (this.closeable == null) {
      return;
    }
    this.closeable.close();
  }

}