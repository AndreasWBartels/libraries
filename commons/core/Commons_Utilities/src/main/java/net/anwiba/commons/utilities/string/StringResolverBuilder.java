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

package net.anwiba.commons.utilities.string;

import java.util.HashMap;
import java.util.regex.Pattern;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.utilities.provider.INamedValueProvider;

public class StringResolverBuilder {

  final private HashMap<String, String> map = new HashMap<>();
  private IStringAppender errorHandler = new StringAppender();
  private Pattern pattern = IStringResolver.PLACEHOLDER_PATTERN;

  public StringResolverBuilder errorHandler(@SuppressWarnings("hiding") final IStringAppender errorHandler) {
    this.errorHandler = errorHandler;
    return this;
  }

  @SuppressWarnings("hiding")
  public StringResolverBuilder pattern(final String pattern) {
    return pattern(Pattern.compile(pattern));
  }

  @SuppressWarnings("hiding")
  public StringResolverBuilder pattern(final Pattern pattern) {
    this.pattern = pattern;
    return this;
  }

  public StringResolverBuilder optional(final String name, final String value) {
    this.map.put(name, value);
    return this;
  }

  public StringResolverBuilder add(final String name, final String value) {
    Ensure.ensureArgumentNotNull(name);
    Ensure.ensureArgumentNotNull(value);
    return optional(name, value);
  }

  public IStringResolver build() {
    return new StringResolver(this.errorHandler, new INamedValueProvider<String, RuntimeException>() {

      @Override
      public String getValue(final String name) throws RuntimeException {
        return StringResolverBuilder.this.map.get(name);
      }

      @Override
      public Iterable<String> getNames() {
        return StringResolverBuilder.this.map.keySet();
      }
    }, this.pattern);
  }

}
