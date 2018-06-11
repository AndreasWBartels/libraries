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
package net.anwiba.commons.jdbc.name;

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.utilities.name.INamesConverter;
import net.anwiba.commons.utilities.name.LowerCaseNameConverter;
import net.anwiba.commons.utilities.name.NameConverter;
import net.anwiba.commons.utilities.name.UniqueNameBuilder;
import net.anwiba.commons.utilities.name.UpperCaseNameConverter;

public class DatabaseNamesConverter implements IDatabaseNamesConverter {

  private final UniqueNameBuilder nameBuilder;

  public static INamesConverter createLowerCaseFactory(final int maximumLength) {
    return new DatabaseNamesConverter(new LowerCaseNameConverter(), maximumLength);
  }

  public static INamesConverter createUpperCaseFactory(final int maximumLength) {
    return new DatabaseNamesConverter(new UpperCaseNameConverter(), maximumLength);
  }

  public static INamesConverter create(final int maximumLength) {
    return new DatabaseNamesConverter(new NameConverter(), maximumLength);
  }

  private DatabaseNamesConverter(
      final IConverter<String, String, RuntimeException> nameConverter,
      final int maximumLength) {
    this.nameBuilder = new UniqueNameBuilder(maximumLength, nameConverter, IDatabaseNamesConstants.RESERVED_NAMES);
  }

  @Override
  public String convert(final String columnName) {
    if (columnName == null) {
      return null;
    }
    return this.nameBuilder.build(columnName);
  }

}
