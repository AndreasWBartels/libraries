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

import java.util.ArrayList;

import net.anwiba.commons.lang.functional.IConverter;

public class NamesConverter implements INamesConverter {

  public static INamesConverter createLowerCaseFactory(final int maximumLength) {
    return new NamesConverter(new LowerCaseNameConverter(), maximumLength);
  }

  public static INamesConverter createUpperCaseFactory(final int maximumLength) {
    return new NamesConverter(new UpperCaseNameConverter(), maximumLength);
  }

  public static INamesConverter create(final int maximumLength) {
    return new NamesConverter(new NameConverter(), maximumLength);
  }

  private NamesConverter(final IConverter<String, String, RuntimeException> nameConverter, final int maximumLength) {
    this.nameBuilder = new UniqueNameBuilder(maximumLength, nameConverter, new ArrayList<>());
  }

  private final UniqueNameBuilder nameBuilder;

  @Override
  public String convert(final String columnName) {
    if (columnName == null) {
      return null;
    }
    return this.nameBuilder.build(columnName);
  }

}
