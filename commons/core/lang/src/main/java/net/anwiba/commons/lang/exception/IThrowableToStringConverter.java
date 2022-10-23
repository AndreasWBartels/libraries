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

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

import java.util.List;
import java.util.function.Function;

public interface IThrowableToStringConverter {

  default boolean isApplicable(final Throwable throwable) {
    return getThrowableClass()
        .convert(throwableClass -> throwableClass.isInstance(throwable))
        .getOr(() -> Boolean.FALSE)
        .booleanValue();
  }

  ThrowableConverterResult convert(final Throwable throwable);

  default IOptional<Class<? extends Throwable>, RuntimeException> getThrowableClass() {
    return Optional.empty();
  }

  static <E extends Throwable> IThrowableToStringConverter of(final Class<E> throwableClass,
      final Function<Throwable, String> toStringConverter) {
    return new IThrowableToStringConverter() {

      @Override
      public IOptional<Class<? extends Throwable>, RuntimeException> getThrowableClass() {
        return Optional.of(throwableClass);
      }

      @Override
      public ThrowableConverterResult convert(final Throwable throwable) {
        return ThrowableConverterResult.of(toStringConverter.apply(throwable));
      }
    };
  }

  default void addTo(final List<String> messages, final String title, final int value) {
    addTo(messages, title, String.valueOf(value));
  }

  default void addTo(final List<String> messages, final String title, final boolean value) {
    addTo(messages, title, String.valueOf(value));
  }

  default void addTo(final List<String> messages, final String title, final String value) {
    if (value == null || value.isBlank()) {
      return;
    }
    StringBuilder builder = new StringBuilder();
    builder.append(title).append("=").append(value);
    messages.add(builder.toString());
  }
}
