/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2019 Andreas W. Bartels
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
// Copyright (c) 2016 by Andreas W. Bartels

package net.anwiba.commons.utilities.string;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.stream.Streams;

public class StringToSystemPropertiesSubstituter implements IStringSubstituter {

  private final List<IConverter<String, String, RuntimeException>> converters = new LinkedList<>();
  private final Supplier<Properties> propertiesSupplier;

  public StringToSystemPropertiesSubstituter(final List<String> propertyNames) {
    this(propertyNames, () -> System.getProperties());
  }

  public StringToSystemPropertiesSubstituter(final List<String> propertyNames,
      final Supplier<Properties> propertiesSupplier) {
    this.propertiesSupplier = propertiesSupplier;
    propertyNames.forEach(property -> this.converters.add(s -> {
      if (StringUtilities.isNullOrTrimmedEmpty(s)) {
        return s;
      }
      final String value = this.propertiesSupplier.get().getProperty(property);
      if (StringUtilities.isNullOrTrimmedEmpty(value)) {
        return s;
      }
      return replaceAllIn(s, value, "$SYSTEM{" + property + "}");
    }));
  }

  @Override
  public String substitute(final String string) {
    return Streams.of(this.converters)
        .aggregate(string, (value, converter) -> converter.convert(value))
        .get();
  }

  private String replaceAllIn(final String string, final String value, final String replacement) {
    int index = string.indexOf(value);
    if (index == -1) {
      return string;
    }
    String prefix = string.substring(0, index);
    String suffix = string.substring(index + value.length(), string.length());
    return prefix + replacement + replaceAllIn(suffix, value, replacement);
  }
}
