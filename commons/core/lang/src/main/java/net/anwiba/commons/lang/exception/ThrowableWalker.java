/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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
package net.anwiba.commons.lang.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class ThrowableWalker {

  private final List<IAdditionalThrowableIterable> additionalThrowableIterables = new ArrayList<>();
  private final IThrowableVisitor visitor;

  public static ThrowableWalker of(
      final Collection<IAdditionalThrowableIterable> additionalThrowableIterables,
      final IThrowableVisitor visitor) {
    return new ThrowableWalker(additionalThrowableIterables, visitor);
  }

  private ThrowableWalker(
      final Collection<IAdditionalThrowableIterable> additionalThrowableIterables,
      final IThrowableVisitor visitor) {
    this.additionalThrowableIterables.addAll(Objects.requireNonNull(additionalThrowableIterables));
    this.visitor = Objects.requireNonNull(visitor);
  }

  public void walk(final Throwable throwable) {
    walkTo(throwable);
  }

  private ThrowableVisitResult walkTo(final Throwable throwable) {
    Optional<ThrowableVisitResult> preVisitResult = getReturnResult(this.visitor.preVisit(throwable));
    if (preVisitResult.isPresent()) {
      return preVisitResult.get();
    }
    Optional<ThrowableVisitResult> result = getReturnResult(this.visitor.visit(throwable));
    if (result.isPresent()) {
      return result.get();
    }
    for (Throwable child : createContainingThrowableIterable(throwable)) {
      Optional<ThrowableVisitResult> walkToResult = getReturnResult(walkTo(child));
      if (walkToResult.isPresent()) {
        return walkToResult.get();
      }
    }
    return ThrowableVisitResult.CONTINUE;
  }

  private Optional<ThrowableVisitResult> getReturnResult(final ThrowableVisitResult visitResult) {
    if (Objects.equals(visitResult, ThrowableVisitResult.TERMINATE)) {
      return Optional.of(ThrowableVisitResult.TERMINATE);
    }
    if (Objects.equals(visitResult, ThrowableVisitResult.SKIP)) {
      return Optional.of(ThrowableVisitResult.CONTINUE);
    }
    return Optional.empty();
  }

  private Iterable<Throwable> createContainingThrowableIterable(final Throwable throwable) {
    List<Throwable> throwables = new LinkedList<>();
    this.additionalThrowableIterables.stream()
        .filter(i -> i.isApplicable(throwable))
        .flatMap(i -> StreamSupport.stream(i.iterable(throwable).spliterator(), false))
        .filter(Objects::nonNull)
        .forEach(throwables::add);
    Optional.ofNullable(throwable.getCause())
        .ifPresent(throwables::add);
    Arrays.stream(throwable.getSuppressed())
        .filter(Objects::nonNull)
        .forEach(throwables::add);
    return throwables;
  }
}
