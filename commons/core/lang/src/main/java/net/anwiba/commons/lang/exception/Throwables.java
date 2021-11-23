/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.primitive.BooleanContainer;
import net.anwiba.commons.lang.stream.Streams;

public class Throwables {

  public final static List<IThrowableToStringConverter> toStringConverters = new ArrayList<>();

  public static void register(final IThrowableToStringConverter toStringConverter) {
    toStringConverters.add(toStringConverter);
  }

  public static  <E extends Throwable> void register(final Class<E> throwableClass, Function<Throwable, String> toStringConverter) {
    toStringConverters.add(new IThrowableToStringConverter() {

      @Override
      public boolean isApplicable(Throwable throwable) {
        return throwableClass.isInstance(throwable);
      }

      @Override
      public String toString(Throwable throwable) {
        return toStringConverter.apply(throwable);
      }
    });
  }

  public final static List<IAdditionalThrowableIterable> additionalThrowableIterables = new ArrayList<>();
  
  public static void register(final IAdditionalThrowableIterable additionalThrowableIterable) {
    additionalThrowableIterables.add(additionalThrowableIterable);
  }

  public static enum SuppressedThrowableVisitorResult {

    CONTINUE,
    SKIP,
    TERMINATE;

  }

  public static enum ThrowableVisitorResult {

    CONTINUE,
    TERMINATE,;

    boolean terminated() {
      return this == TERMINATE;
    }

  }

  public static interface ThrowableVisitor {

    default ThrowableVisitorResult preVisitCause(Throwable throwable) {
      return ThrowableVisitorResult.CONTINUE;
    }

    default SuppressedThrowableVisitorResult preVisitSuppressed(Throwable throwable) {
      return SuppressedThrowableVisitorResult.CONTINUE;
    }

    ThrowableVisitorResult visitThrowable(Throwable throwable);

  }

  static public void walk(Throwable throwable, ThrowableVisitor visitor) {
    walkThrowable(Objects.requireNonNull(throwable), Objects.requireNonNull(visitor), new LinkedHashSet<>());
  }

  static private ThrowableVisitorResult walkThrowable(Throwable throwable,
      ThrowableVisitor visitor,
      Set<Throwable> visited) {
    if (visited.contains(throwable)) {
      return ThrowableVisitorResult.CONTINUE;
    }
    visited.add(throwable);
    visitor.visitThrowable(throwable);
    switch (visitor.preVisitCause(throwable)) {
      case CONTINUE: {
        if (visitor.visitThrowable(throwable).terminated()) {
          return ThrowableVisitorResult.TERMINATE;
        }
        break;
      }
      case TERMINATE: {
        return ThrowableVisitorResult.TERMINATE;
      }
    }

    IOptional<IAdditionalThrowableIterable, RuntimeException> optionalIterable = 
        Streams.of(additionalThrowableIterables).first(i -> i.isApplicable(throwable));
    
    if (optionalIterable.isAccepted()) {
      for (Throwable additionalThrowable : optionalIterable.get().iterable(throwable)) {
        if (visited.contains(additionalThrowable)) {
          continue;
        }
        switch (visitor.preVisitSuppressed(throwable)) {
          case CONTINUE: {
            if (walkThrowable(additionalThrowable, visitor, visited).terminated()) {
              return ThrowableVisitorResult.TERMINATE;
            }
            break;
          }
          case SKIP: {
            continue;
          }
          case TERMINATE: {
            return ThrowableVisitorResult.TERMINATE;
          }
        }
      }
    }
    
    Throwable cause = throwable.getCause();
    if (cause != null && !visited.contains(cause)) {
      switch (visitor.preVisitCause(throwable)) {
        case CONTINUE: {
          if (walkThrowable(cause, visitor, visited).terminated()) {
            return ThrowableVisitorResult.TERMINATE;
          }
          break;
        }
        case TERMINATE: {
          return ThrowableVisitorResult.TERMINATE;
        }
      }
    }

    for (Throwable suppressed : throwable.getSuppressed()) {
      if (visited.contains(suppressed)) {
        continue;
      }
      switch (visitor.preVisitSuppressed(throwable)) {
        case CONTINUE: {
          if (walkThrowable(suppressed, visitor, visited).terminated()) {
            return ThrowableVisitorResult.TERMINATE;
          }
          break;
        }
        case SKIP: {
          continue;
        }
        case TERMINATE: {
          return ThrowableVisitorResult.TERMINATE;
        }
      }
    }
    return ThrowableVisitorResult.CONTINUE;
  }

  public static boolean isApplicable(final Throwable throwable) {
    return hasIterator(throwable) || hasConverter(throwable);
  }

    public static boolean hasIterator(final Throwable throwable) {
    if (throwable == null || additionalThrowableIterables.isEmpty()) {
      return false;
    }
    BooleanContainer isApplicable = new BooleanContainer(false);
    walk(throwable, throwable1 -> {
      if (Streams.of(additionalThrowableIterables).first(resolver -> resolver.isApplicable(throwable1)).isAccepted()) {
        isApplicable.set(true);
        return ThrowableVisitorResult.TERMINATE;
      }
      return ThrowableVisitorResult.CONTINUE;
    });
    return isApplicable.isTrue();
  }

  public static boolean hasConverter(final Throwable throwable) {
    if (throwable == null || toStringConverters.isEmpty()) {
      return false;
    }
    BooleanContainer isApplicable = new BooleanContainer(false);
    walk(throwable, throwable1 -> {
      if (Streams.of(toStringConverters).first(resolver -> resolver.isApplicable(throwable1)).isAccepted()) {
        isApplicable.set(true);
        return ThrowableVisitorResult.TERMINATE;
      }
      return ThrowableVisitorResult.CONTINUE;
    });
    return isApplicable.isTrue();
  }

  public static String toString(final Throwable throwable) {
    return toString(throwable, toStringConverters, t -> {
      if (t.getCause() != null 
          && !Objects.equals(t, t.getCause())
          && Objects.equals(t.getMessage(), t.getCause().getMessage())) {
        return null;
      }
      return t.getMessage();
    });
  }

  public static String toString(final Throwable throwable,
      List<IThrowableToStringConverter> toStringConverters,
      IConverter<Throwable, String, RuntimeException> defaultToStringConverter) {
    return toString(throwable,
        (Throwable t) -> Streams.of(toStringConverters)
            .first(resolver -> resolver.isApplicable(t))
            .convert(resolver -> resolver.toString(t))
            .getOr(() -> defaultToStringConverter.convert(t)));
  }

  public static String toString(final Throwable throwable,
      IConverter<Throwable, String, RuntimeException> toStringConverter) {
    Set<String> messages = new LinkedHashSet<>();
    walk(throwable, new ThrowableVisitor() {

      @Override
      public ThrowableVisitorResult visitThrowable(Throwable throwable) {
        Optional.of(toStringConverter.convert(throwable))
            .consume(m -> messages.add(m));
        return ThrowableVisitorResult.CONTINUE;
      }
    });
    return String.join("\n", messages);
  }

  public static String toStackTraceString(final Throwable throwable) {
    Objects.requireNonNull(throwable);
    try (final StringWriter stringWriter = new StringWriter()) {
      throwable.printStackTrace(new PrintWriter(stringWriter));
      return stringWriter.getBuffer().toString();
    } catch (IOException exception) {
      throw new UnreachableCodeReachedException(exception);
    }
  }
}
