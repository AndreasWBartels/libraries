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

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import net.anwiba.commons.lang.functional.IConverter;

public class StringToSystemPropertiesSubstituter implements IStringSubstituter {

  private final List<IConverter<String, String, RuntimeException>> converters = new LinkedList<>();

  public StringToSystemPropertiesSubstituter(final List<String> propertyNames) {
    new LinkedHashSet<>(propertyNames).forEach(property -> this.converters.add(string -> {
      final String value = System.getProperty(property);
      if (StringUtilities.isNullOrTrimmedEmpty(value) || !string.startsWith(value)) {
        return string;
      }
      return "$SYSTEM{" + property + "}" + string.substring(value.length(), string.length()); //$NON-NLS-1$ //$NON-NLS-2$
    }));
  }

  @Override
  public String substitute(final String string) {
    String value = string;
    for (final IConverter<String, String, RuntimeException> converter : this.converters) {
      value = converter.convert(value);
    }
    return value;
  }
}
