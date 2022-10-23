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

import java.util.regex.Pattern;

import net.anwiba.commons.lang.functional.ConversionException;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.ResolvingException;
import net.anwiba.commons.utilities.provider.IContextValueProvider;
import net.anwiba.commons.utilities.regex.DoNothingStringConverter;
import net.anwiba.commons.utilities.regex.tokenizer.IRegExpTokenConverter;
import net.anwiba.commons.utilities.regex.tokenizer.RegExpTokenizingConverter;

public class AbstractStringResolver implements IStringResolver {

  protected final IStringAppender errorHandler;
  private final IContextValueProvider<String, String, RuntimeException> provider;
  private final Pattern pattern;
  private final IConverter<String, String, ConversionException> valueConverter;

  public AbstractStringResolver(
      final IStringAppender errorHandler,
      final IConverter<String, String, ConversionException> valueConverter,
      final Pattern pattern,
      final IContextValueProvider<String, String, RuntimeException> contextValueProvider) {
    this.errorHandler = errorHandler;
    this.valueConverter = valueConverter;
    this.pattern = pattern;
    this.provider = contextValueProvider;
  }

  @Override
  public String resolve(final String value) throws ResolvingException {
    return resolve(value, this.errorHandler);
  }

  @SuppressWarnings("hiding")
  private String resolve(final String value, final IStringAppender errorHandler) throws ResolvingException {
    try {
      if (value == null) {
        return null;
      }
      final Pattern pattern = this.pattern;
      final RegExpTokenizingConverter converter = new RegExpTokenizingConverter(new IRegExpTokenConverter() {
        @Override
        public String convert(final String[] groups) {
          final String name = groups[1];
          final String defaultValue = groups.length > 2 ? groups[2] : null;
          return getValue(name, defaultValue, errorHandler);
        }

        @Override
        public Pattern getRegExpPattern() {
          return pattern;
        }
      }, new DoNothingStringConverter());
      return converter.convert(valueConverter.convert(value), new StringAppender());
    } catch (final ConversionException exception) {
      throw new ResolvingException(exception.getLocalizedMessage(), exception);
    }
  }

  @SuppressWarnings("hiding")
  String getValue(final String name, final String defaultValue, final IStringAppender errorHandler) {
    final String value = this.provider.getValue(name);
    if (defaultValue == null && value == null) {
      errorHandler.append(name);
      return ""; //$NON-NLS-1$
    }
    return value != null ? value : defaultValue;
  }

}