/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.commons.ensure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Conditions {

  public static <T> ICondition<T> equalTo(final T value) {
    return new EqualToCondition<>(value);
  }

  public static <T> ICondition<T> notNull() {
    return new NotCondition<>(new IsNullCondition<T>());
  }

  public static <T> ICondition<T> isNull() {
    return new IsNullCondition<>();
  }

  public static ICondition<Boolean> isTrue() {
    return new IsTrueCondition();
  }

  public static ICondition<Boolean> isFalse() {
    return new NotCondition<>(new IsTrueCondition());
  }

  @SafeVarargs
  public static <T> ICondition<T> in(final T... values) {
    return in(Arrays.asList(values));
  }

  public static <T> ICondition<T> in(final Collection<T> collection) {
    return new InCondition<>(collection == null ? new ArrayList<T>() : collection);
  }

  public static <T> ICondition<T> not(final ICondition<T> validation) {
    Ensure.ensureArgumentNotNull(validation);
    return new NotCondition<>(validation);
  }

  public static ICondition<Object> instanceOf(final Class<?> value) {
    Ensure.ensureArgumentNotNull(value);
    return new InstanceOfCondition(value);
  }

  public static ICondition<String> contains(final String value) {
    Ensure.ensureArgumentNotNull(value);
    return new StringContainsCondition(value);
  }

  public static ICondition<String> startsWith(final String value) {
    Ensure.ensureArgumentNotNull(value);
    return new StringStartsWithCondition(value);
  }

  public static <T extends Comparable<T>> ICondition<T> between(final T minimum, final T maximum) {
    Ensure.ensureArgumentNotNull(minimum);
    Ensure.ensureArgumentNotNull(maximum);
    return new BetweenCondition<>(minimum, maximum);
  }

  public static <T extends Comparable<T>> ICondition<T> greaterThan(final T value) {
    Ensure.ensureArgumentNotNull(value);
    return new GreaterThanCondition<>(value);
  }

  public static <T extends Comparable<T>> ICondition<T> lowerThan(final T value) {
    Ensure.ensureArgumentNotNull(value);
    return new LowerThanCondition<>(value);
  }

  public static ICondition<String> endsWith(final String value) {
    Ensure.ensureArgumentNotNull(value);
    return new StringEndsWithCondition(value);
  }

  @SafeVarargs
  public static <T> ICondition<T> anyOf(final ICondition<? super T>... conditions) {
    return new AnyOfCondition<>(Arrays.asList(conditions));
  }

  @SafeVarargs
  public static <T> ICondition<T> allOf(final ICondition<? super T>... conditions) {
    return new AllOfCondition<>(Arrays.asList(conditions));
  }

  public static <T extends Collection<?>> ICondition<T> isEmpty() {
    return new IsEmptyCollectionCondition<>();
  }

  @SafeVarargs
  public static <V, T extends Collection<V>> ICondition<T> containsAll(final V... values) {
    return containsAll(Arrays.asList(values));
  }

  public static <V, T extends Collection<V>> ICondition<T> containsAll(final Collection<V> values) {
    Ensure.ensureArgumentNotNull(values);
    return new CollectionContaintsCondition<>(values);
  }

  public static <V, T extends Collection<V>> ICondition<T> containsNull() {
    return new CollectionContaintsNullCondition<>();
  }

  @SuppressWarnings("rawtypes")
  public static <T extends Collection> ICondition<T> size(final int i) {
    return new CollectionSizeCondition<>(i);
  }
}
