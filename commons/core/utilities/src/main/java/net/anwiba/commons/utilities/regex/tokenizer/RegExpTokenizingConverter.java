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
package net.anwiba.commons.utilities.regex.tokenizer;

import static net.anwiba.commons.ensure.Conditions.notNull;
import static net.anwiba.commons.ensure.Ensure.ensureThatArgument;

import java.util.regex.Matcher;

import net.anwiba.commons.lang.functional.ConversionException;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.utilities.string.IStringAppender;

public class RegExpTokenizingConverter {

  private final IConverter<String, String, RuntimeException> converter;
  private final IRegExpTokenConverter[] converters;

  public RegExpTokenizingConverter(
    final IConverter<String, String, RuntimeException> converter,
    final IRegExpTokenConverter... converters) {
    ensureThatArgument(converters, notNull());
    ensureThatArgument(converter, notNull());
    this.converters = converters;
    this.converter = converter;
  }

  public RegExpTokenizingConverter(
    final IRegExpTokenConverter converter,
    final IConverter<String, String, RuntimeException> remainingPartsConverter) {
    this(remainingPartsConverter, new IRegExpTokenConverter[] { converter });
  }

  public String convert(final String text, final IStringAppender appender) throws ConversionException {
    convert(0, text, appender);
    return appender.toString();
  }

  private void convert(final int converterIndex, final String text, final IStringAppender appender)
      throws ConversionException {
    if (converterIndex >= this.converters.length) {
      if (text.length() > 0) {
        appender.append(this.converter.convert(text));
      }
      return;
    }
    int position = 0;
    final IRegExpTokenConverter matchingConverter = this.converters[converterIndex];
    Matcher matcher = matchingConverter.getRegExpPattern().matcher(text);
    while (position < text.length() && matcher.find(position)) {
      if (matcher.start() > position) {
        convert(converterIndex + 1, text.substring(position, matcher.start()), appender);
      }
      final String[] groups = RegExpUtilities.getGroups(matcher);
      appender.append(matchingConverter.convert(groups));
      position = matcher.end();
      matcher = matchingConverter.getRegExpPattern().matcher(text);
    }
    if (position < text.length()) {
      convert(converterIndex + 1, text.substring(position), appender);
    }
  }
}