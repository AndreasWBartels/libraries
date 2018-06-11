/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.commons.utilities.name;

import org.apache.commons.lang.CharSet;

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.utilities.string.StringUtilities;

public class UpperCaseNameConverter implements IConverter<String, String, RuntimeException> {

  private static final String N = "n";//$NON-NLS-1$

  interface ISupplier<I, O> {

    O supply(I input);

  }

  ISupplier<String, String> prefixSupplier = new ISupplier<String, String>() {

    @Override
    public String supply(final String input) {
      return N;
    }
  };

  @Override
  public String convert(final String input) {
    if (StringUtilities.isNullOrTrimmedEmpty(input)) {
      return N;
    }
    final StringBuilder builder = new StringBuilder();
    boolean whitespaceFlag = false;
    for (int i = 0; i < input.length(); ++i) {
      final char c = substitute(input.charAt(i));
      if ('_' == c) {
        whitespaceFlag = true;
        continue;
      }
      if (builder.length() == 0 && CharSet.ASCII_NUMERIC.contains(c)) {
        builder.append(this.prefixSupplier.supply(input));
      }
      if (builder.length() > 0 && whitespaceFlag) {
        builder.append('_');
      }
      whitespaceFlag = false;
      builder.append(c);
    }
    final String string = builder.toString();
    return StringUtilities.isNullOrTrimmedEmpty(string) ? N : string;
  }

  private char substitute(final char c) {
    if (CharSet.ASCII_NUMERIC.contains(c)) {
      return c;
    }
    if (CharSet.ASCII_ALPHA.contains(c)) {
      return Character.toUpperCase(c);
    }
    return '_';
  }
}
