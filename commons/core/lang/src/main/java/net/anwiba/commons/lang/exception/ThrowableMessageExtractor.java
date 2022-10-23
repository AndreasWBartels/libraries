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

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.registry.HierarchicalClassKeyRegistry;
import net.anwiba.commons.lang.stream.Streams;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class ThrowableMessageExtractor implements IThrowableMessageExtractor {

  private final class ThrowableVisitor implements IThrowableVisitor {
    private final Set<String> messages;
    private final Set<Throwable> visited;
    private final IConverter<Throwable, ThrowableConverterResult, RuntimeException> toStringConverter;

    private ThrowableVisitor(final Set<String> messages,
        final Set<Throwable> visited,
        final IConverter<Throwable, ThrowableConverterResult, RuntimeException> toStringConverter) {
      this.messages = messages;
      this.visited = visited;
      this.toStringConverter = toStringConverter;
    }

    @Override
    public ThrowableVisitResult preVisit(final Throwable throwable) {
      return IThrowableVisitor.preVisit(this.visited, throwable);
    }

    @Override
    public ThrowableVisitResult visit(final Throwable throwable) {
      return Optional.of(throwable)
          .convert(this.toStringConverter)
          .consume(result -> Optional.of(result)
              .convert(ThrowableConverterResult::string)
              .accept(IAcceptor.not(String::isBlank))
              .consume(this::add))
          .convert(ThrowableConverterResult::result)
          .getOr(() -> ThrowableVisitResult.CONTINUE);
    }

    private void add(final String string) {
      if (this.messages.contains(string)) {
        return;
      }
      if (!this.messages.isEmpty()
          && Streams.of(this.messages)
              .filter(message -> message.contains(string))
              .foundAny()) {
        return;
      }
      Streams.of(this.messages)
          .filter(string::contains)
          .foreach(this.messages::remove);
      this.messages.add(string);
    }
  }

  private final Function<Throwable, IThrowableToStringConverter> toStringConverterSelector;
  private final Collection<IAdditionalThrowableIterable> additionalThrowableIterables;

  public static ThrowableMessageExtractor of(final Collection<IThrowableToStringConverter> toStringConverters,
      final Collection<IAdditionalThrowableIterable> additionalThrowableIterables) {
    final Collection<IThrowableToStringConverter> converters = Collections.unmodifiableCollection(toStringConverters);
    final HierarchicalClassKeyRegistry<IThrowableToStringConverter> toStringConverterRegistry =
        new HierarchicalClassKeyRegistry<>();
    Streams.of(toStringConverters)
        .filter(converter -> converter.getThrowableClass().isAccepted())
        .forEach(converter -> toStringConverterRegistry.add(converter.getThrowableClass().get(), converter));
    final Function<Throwable, IThrowableToStringConverter> toStringConverterSelector =
        throwable -> {
          final IThrowableToStringConverter converter = toStringConverterRegistry.get(throwable.getClass());
          return converter != null ? converter
              : Streams.of(converters)
                  .first(c -> c.isApplicable(throwable))
                  .get();
        };
    return new ThrowableMessageExtractor(toStringConverterSelector, additionalThrowableIterables);
  }

  public ThrowableMessageExtractor(final Function<Throwable, IThrowableToStringConverter> toStringConverterSelector,
      final Collection<IAdditionalThrowableIterable> additionalThrowableIterables) {
    this.toStringConverterSelector = toStringConverterSelector;
    this.additionalThrowableIterables = Collections.unmodifiableCollection(additionalThrowableIterables);
  }

  @Override
  public String extract(final Throwable throwable) {
    return extract(
        throwable instanceof WrappedException wrapped ? wrapped.getCause() : throwable,
        this.toStringConverterSelector,
        t -> t.getCause() != null
            && !Objects.equals(t, t.getCause())
            && equalMessages(t, t.getCause())
                ? null
                    : ThrowableConverterResult.of(t.getMessage() == null ? null : t.getMessage().trim()));
  }

  private String extract(
      final Throwable throwable,
      final Function<Throwable, IThrowableToStringConverter> converterSelector,
      final IConverter<Throwable, ThrowableConverterResult, RuntimeException> defaultConverter) {
    return extract(
        throwable,
        t -> Optional.of(converterSelector.apply(t))
            .convert(converter -> converter.convert(t))
            .getOr(() -> defaultConverter.convert(t)));
  }

  private String extract(
      final Throwable throwable,
      final IConverter<Throwable, ThrowableConverterResult, RuntimeException> toStringConverter) {
    final Set<String> messages = new LinkedHashSet<>();
    final Set<Throwable> visited = new HashSet<>();
    ThrowableWalker.of(
        this.additionalThrowableIterables,
        new ThrowableVisitor(messages, visited, toStringConverter))
        .walk(throwable);
    return messages.isEmpty()
        ? (throwable == null ? null : throwable.toString())
        : String.join("\n", messages);
  }


  private boolean equalMessages(final Throwable throwable, final Throwable cause) {
    String message = throwable.getMessage() == null ? null : throwable.getMessage().trim();
    String causeMessage = cause.getMessage() == null ? null : cause.getMessage().trim();
    String prefix = cause.getClass().getName() + ": ";
    if (message != null && message.startsWith(prefix)) {
      return Objects.equals(message.substring(prefix.length()), causeMessage);
    }
    return Objects.equals(message, causeMessage);
  }
}
