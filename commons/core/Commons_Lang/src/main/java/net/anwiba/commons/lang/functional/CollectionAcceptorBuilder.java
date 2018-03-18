package net.anwiba.commons.lang.functional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.anwiba.commons.lang.stream.Streams;

public class CollectionAcceptorBuilder<T> {

  private final List<IAcceptor<T>> acceptors = new ArrayList<>();
  private IAcceptor<T> otherwise = v -> false;

  public static class CollectionAcceptor<T> implements IAcceptor<Collection<T>> {

    private final List<IAcceptor<T>> acceptors;
    private final IAcceptor<T> otherwise;

    public CollectionAcceptor(final Collection<IAcceptor<T>> acceptors, final IAcceptor<T> otherwise) {
      this.acceptors = new ArrayList<>(acceptors);
      this.otherwise = otherwise;
    }

    @Override
    public boolean accept(final Collection<T> values) {
      final Iterator<IAcceptor<T>> iterator = this.acceptors.iterator();
      return values.size() < this.acceptors.size() //
          ? false
          : Streams
              .of(values)
              .aggregate(true, (i, v) -> i && iterator.hasNext() ? iterator.next().accept(v) : this.otherwise.accept(v))
              .getOr(() -> false);
    }
  }

  public CollectionAcceptorBuilder<T> accept(final IAcceptor<T> acceptor) {
    this.acceptors.add(acceptor);
    return this;
  }

  public CollectionAcceptorBuilder<T> otherwise(final IAcceptor<T> otherwise) {
    this.otherwise = otherwise;
    return this;
  }

  public IAcceptor<Collection<T>> build() {
    return new CollectionAcceptor<>(this.acceptors, this.otherwise);
  }

}
