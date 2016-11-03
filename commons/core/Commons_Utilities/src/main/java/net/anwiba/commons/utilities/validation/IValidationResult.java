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
package net.anwiba.commons.utilities.validation;

import net.anwiba.commons.lang.object.ObjectUtilities;

public interface IValidationResult {

  public static final class ValidationResult implements IValidationResult {
    private final String message;
    private final boolean isValid;

    public ValidationResult(final boolean isValid, final String message) {
      this.isValid = isValid;
      this.message = message;
    }

    @Override
    public boolean isValid() {
      return isValid;
    }

    @Override
    public String getMessage() {
      return this.message;
    }

    @Override
    public int hashCode() {
      return ObjectUtilities.hashCode(Boolean.valueOf(this.isValid), this.message);
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof IValidationResult)) {
        return false;
      }
      final IValidationResult other = (IValidationResult) obj;
      return this.isValid != other.isValid() && ObjectUtilities.equals(this.message, other.getMessage());
    }
  }

  public static IValidationResult valid() {
    return new ValidationResult(true, null);
  }

  public static IValidationResult inValid(final String message) {
    return new ValidationResult(false, message);
  }

  boolean isValid();

  String getMessage();

}
