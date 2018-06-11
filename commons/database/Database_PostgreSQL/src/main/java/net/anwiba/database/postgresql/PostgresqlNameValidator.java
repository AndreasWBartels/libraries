/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.database.postgresql;

import net.anwiba.commons.jdbc.name.IDatabaseNamesConstants;
import net.anwiba.commons.jdbc.name.ValidationResultMessages;
import net.anwiba.commons.nls.NLS;
import net.anwiba.commons.utilities.name.INameValidator;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.commons.utilities.validation.IValidationResult;

public final class PostgresqlNameValidator implements INameValidator {
  @Override
  public IValidationResult validate(final String name) {
    if (name == null) {
      return IValidationResult.inValid(ValidationResultMessages.null_value);
    }
    if (StringUtilities.isNullOrEmpty(name)) {
      return IValidationResult.inValid(ValidationResultMessages.empty_string_value);
    }
    final int maximumNameLength = 31;
    if (name.length() > maximumNameLength) {
      return IValidationResult.inValid(NLS.bind(ValidationResultMessages.more_than_i0_characters, name));
    }
    if (IDatabaseNamesConstants.RESERVED_NAMES.contains(name.toLowerCase())) {
      return IValidationResult.inValid(ValidationResultMessages.reserved_name);
    }
    final char[] charArray = name.toCharArray();
    for (int i = 0; i < charArray.length; i++) {
      final char c = charArray[i];
      if (Character.isWhitespace(c)) {
        return IValidationResult.inValid(ValidationResultMessages.whitespace_at_character + "_" + i); //$NON-NLS-1$
      }
      if (!(Character.isLowerCase(c)) && i == 0) {
        return IValidationResult.inValid(ValidationResultMessages.unsupported_character_at_index + "_" + i); //$NON-NLS-1$
      }
      if (Character.isUpperCase(c) || Character.isLowerCase(c) || Character.isDigit(c) || c == '_') {
        continue;
      }
      return IValidationResult.inValid(ValidationResultMessages.unsupported_character_at_index + "_" + i); //$NON-NLS-1$
    }
    return IValidationResult.valid();
  }
}
