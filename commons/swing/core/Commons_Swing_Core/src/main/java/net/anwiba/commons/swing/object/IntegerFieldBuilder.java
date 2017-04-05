/*
 * #%L
 * anwiba commons swing
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

package net.anwiba.commons.swing.object;

import java.util.regex.Pattern;

import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.commons.utilities.validation.IValidationResult;
import net.anwiba.commons.utilities.validation.IValidator;

public class IntegerFieldBuilder
    extends
    AbstractObjectFieldBuilder<Integer, IntegerObjectFieldConfigurationBuilder, IntegerFieldBuilder> {

  public IntegerFieldBuilder() {
    super(new IntegerObjectFieldConfigurationBuilder());
  }

  @Override
  protected AbstractObjectTextField<Integer> create(final IObjectFieldConfiguration<Integer> configuration) {
    return new IntegerField(configuration);
  }

  public AbstractObjectFieldBuilder<Integer, IntegerObjectFieldConfigurationBuilder, IntegerFieldBuilder> setRegularExpressionValidator(
      final String patternString,
      final String message) {
    final Pattern pattern = Pattern.compile(patternString);

    getConfigurationBuilder().setValidator(new IValidator<String>() {

      @Override
      public IValidationResult validate(final String value) {
        if (StringUtilities.isNullOrTrimmedEmpty(value)) {
          return IValidationResult.inValid(message);
        }
        pattern.matcher(value).matches();
        if (!value.matches(patternString)) {
          return IValidationResult.inValid(message);
        }
        return IValidationResult.valid();
      }
    });
    return this;
  }

  public IntegerFieldBuilder setToolTip(final String tooltipText) {
    getConfigurationBuilder().setToolTipFactory((validationResult, text) -> {
      if (!validationResult.isValid()) {
        return validationResult.getMessage();
      }
      return tooltipText;
    });
    return this;
  }

}
