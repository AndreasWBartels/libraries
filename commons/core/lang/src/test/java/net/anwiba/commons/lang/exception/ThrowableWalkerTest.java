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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ThrowableWalkerTest {

  private static class AdditionalExceptionHoldingException extends Exception {

    private Throwable additionalThrowable;

    public AdditionalExceptionHoldingException(String message) {
      super(message);
    }

    public void setAdditionalException(Throwable throwable) {
      additionalThrowable = throwable;
    }

    public Throwable getAdditionalThrowable() {
      return additionalThrowable;
    }
  }

  private final List<IAdditionalThrowableIterable> additionalThrowableIterables = List.of(new IAdditionalThrowableIterable() {
    @Override
    public Iterable<Throwable> iterable(Throwable throwable) {
      return Arrays.asList(((AdditionalExceptionHoldingException) throwable).getAdditionalThrowable());
    }

    @Override
    public boolean isApplicable(Throwable throwable) {
      return throwable instanceof AdditionalExceptionHoldingException;
    }
  });

  @Test
  public void visitNone() {
    assertNumberOfVisited(null, 1, 0);
  }

  @Test
  public void visitOne() {
    assertNumberOfVisited(new Exception("exception"), 1, 1);
  }

  @Test
  public void visitCause() {
    assertNumberOfVisited(new Exception("exception", new Exception("cause")), 2);
  }

  @Test
  public void preVisitCauseLoop() {
    Exception cause = new Exception("cause");
    Exception exception = new Exception("exception", cause);
    cause.initCause(exception);
    List<Throwable> visited = assertNumberOfVisited(exception, 3, 2);
    Assertions.assertEquals(List.of(exception, cause), visited);
  }

  @Test
  public void visitSuppressed() {
    Exception suppressed1 = new Exception("Suppressed1");
    Exception suppressed2 = new Exception("Suppressed2");
    Exception throwable = new Exception("exception");
    throwable.addSuppressed(suppressed1);
    throwable.addSuppressed(suppressed2);
    List<Throwable> visited = assertNumberOfVisited(throwable, 3);
    Assertions.assertEquals(List.of(throwable, suppressed1, suppressed2), visited);
  }

  @Test
  public void unvisitedAdditional() {
    AdditionalExceptionHoldingException throwable
        = new AdditionalExceptionHoldingException("exception");
    throwable.setAdditionalException(new Exception("additional"));
    assertNumberOfVisited(throwable, 1);
  }

  @Test
  public void visitWithAdditional() {
    AdditionalExceptionHoldingException additional2
        = new AdditionalExceptionHoldingException("additional2");
    AdditionalExceptionHoldingException additional1
        = new AdditionalExceptionHoldingException("additional1");
    additional1.setAdditionalException(additional2);
    AdditionalExceptionHoldingException throwable
        = new AdditionalExceptionHoldingException("exception");
    throwable.setAdditionalException(additional1);
    List<Throwable> visited = assertNumberOfVisitedWithAdditionalThrowableIterables(
        throwable,
        3,
        3);
    Assertions.assertEquals(List.of(throwable, additional1, additional2), visited);
  }

  @Test
  public void visitEmptyWithAdditional() {
    AdditionalExceptionHoldingException throwable =
        new AdditionalExceptionHoldingException("exception");
    assertNumberOfVisitedWithAdditionalThrowableIterables(throwable, 1, 1);
  }

  @Test
  public void visitCauseAndSuppressed() {
    Exception throwable = new Exception("exception", new Exception("cause"));
    throwable.addSuppressed(new Exception("Suppressed1"));
    throwable.addSuppressed(new Exception("Suppressed2"));
    assertNumberOfVisited(throwable, 4);
  }

  @Test
  public void visitCauseAndSuppressedAndAdditional() {

    Exception additional2Cause = new Exception("additional2Cause");
    AdditionalExceptionHoldingException additional2
        = new AdditionalExceptionHoldingException("additional2");
    additional2.initCause(additional2Cause);

    Exception additional1Cause = new Exception("additional1Cause");
    AdditionalExceptionHoldingException additional1
        = new AdditionalExceptionHoldingException("additional1");
    additional1.initCause(additional1Cause);
    additional1.setAdditionalException(additional2);

    AdditionalExceptionHoldingException throwable
        = new AdditionalExceptionHoldingException("exception");
    Exception cause = new Exception("cause");
    Exception suppressed1 = new Exception("Suppressed1");
    Exception suppressed2 = new Exception("Suppressed2");

    throwable.initCause(cause);
    throwable.setAdditionalException(additional1);
    throwable.addSuppressed(suppressed1);
    throwable.addSuppressed(suppressed2);
    List<Throwable> visited
        = assertNumberOfVisitedWithAdditionalThrowableIterables(throwable, 8, 8);
    Assertions.assertEquals(List.of(
        throwable,
        additional1,
        additional2,
        additional2Cause,
        additional1Cause,
        cause,
        suppressed1,
        suppressed2), visited);
  }

  private List<Throwable> assertNumberOfVisited(Throwable throwable, int visitsExpected) {
    return assertNumberOfVisited(throwable, visitsExpected, visitsExpected);
  }

  private List<Throwable> assertNumberOfVisited(
      Throwable throwable,
      int preVisitsExpected,
      int visitsExpected) {
    return assertNumberOfVisited(
        throwable,
        List.of(),
        preVisitsExpected,
        visitsExpected);
  }

  private List<Throwable> assertNumberOfVisitedWithAdditionalThrowableIterables(
      Throwable throwable,
      int preVisitsExpected,
      int visitsExpected) {
    return assertNumberOfVisited(
        throwable,
        additionalThrowableIterables,
        preVisitsExpected,
        visitsExpected);
  }

  private List<Throwable> assertNumberOfVisited(
      Throwable throwable,
      List<IAdditionalThrowableIterable> additionalThrowableIterables,
      int preVisitsExpected,
      int visitsExpected) {
    final Set<Throwable> visitedThrowables = new LinkedHashSet<>();
    final Set<Throwable> preVisitedThrowables = new LinkedHashSet<>();
    final AtomicInteger preVisitedCounter = new AtomicInteger();
    final AtomicInteger visitedCounter = new AtomicInteger();

    ThrowableWalker.of(
            additionalThrowableIterables,
            new IThrowableVisitor() {

              @Override
              public ThrowableVisitResult preVisit(Throwable throwable1) {
                preVisitedCounter.incrementAndGet();
                return IThrowableVisitor.preVisit(preVisitedThrowables, throwable1);
              }

              @Override
              public ThrowableVisitResult visit(Throwable throwable1) {
                visitedCounter.incrementAndGet();
                visitedThrowables.add(throwable1);
                return ThrowableVisitResult.CONTINUE;
              }
            })
        .walk(throwable);
    Assertions.assertEquals(
        preVisitsExpected,
        preVisitedCounter.get(),
        "Unexpected number of pre visit calls");
    Assertions.assertEquals(
        visitsExpected,
        visitedCounter.get(),
        "Unexpected number of visit calls");
    return List.copyOf(visitedThrowables);
  }
}
