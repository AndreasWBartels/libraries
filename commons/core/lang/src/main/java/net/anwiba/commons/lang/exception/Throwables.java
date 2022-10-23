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

import net.anwiba.commons.lang.optional.Optional;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Throwables {

  private static IThrowableMessageExtractor extractor = new IThrowableMessageExtractor() {

    @Override
    public String extract(final Throwable throwable) {
      return throwable.getMessage();
    }
  };

  public static void setThrowableMessageExtractor(final IThrowableMessageExtractor extractor) {
    Throwables.extractor = extractor;
  }

  private Throwables() {
  }

  public static String getExtendedMessage(final Throwable throwable) {
    return extractor.extract(throwable);
  }

  public static String toStackTraceString(final Throwable throwable) {
    return Optional.of(throwable)
        .convert(t -> {
          try (final StringWriter stringWriter = new StringWriter()) {
            throwable.printStackTrace(new PrintWriter(stringWriter));
            return stringWriter.getBuffer().toString();
          } catch (IOException exception) {
            throw new UnreachableCodeReachedException(exception);
          }
        })
        .get();
  }

  public static <E extends Throwable> Function<Throwable, E> toException(final Class<E> throwableClass) {
    try {
      Constructor<E> constructor = throwableClass.getConstructor(String.class, Throwable.class);
      return t -> {
        try {
          return constructor.newInstance(t.getMessage(), t);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException exception) {
          throw new UnsupportedOperationException(exception.getMessage(), exception);
        }
      };
    } catch (SecurityException | NoSuchMethodException exception) {
      throw new UnsupportedOperationException(exception.getMessage(), exception);
    }
  }

  public static <E extends Throwable> E concat(final Function<Throwable, E> toException,
      final Throwable... throwables) {
    return concat(toException, Arrays.asList(throwables));
  }

  public static <E extends Throwable> E concat(final Function<Throwable, E> toException,
      final List<Throwable> throwables) {
    E first = null;
    for (Throwable throwable : throwables) {
      if (throwable == null) {
        continue;
      }
      if (first == null) {
        first = toException.apply(throwable);
        continue;
      }
      first.addSuppressed(throwable);
    }
    return first;
  }

  public static <E extends Throwable> void throwIfNotEmpty(final Function<Throwable, E> toException,
      final List<Throwable> throwables) throws E {
    throwIfNotNull(toException, concat(toException, throwables));
  }

  public static <E extends Throwable> void throwIfNotNull(final Function<Throwable, E> toException,
      final Throwable throwable) throws E {
    if (throwable == null) {
      return;
    }
    throw toException.apply(throwable);
  }

}
