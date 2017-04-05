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

import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.utilities.validation.IValidationResult;
import net.anwiba.commons.utilities.validation.IValidator;

public class DefaultObjectFieldConfiguration<T> extends AbstractObjectTextFieldConfiguration<T> {

  private final IValidator<String> validator;
  private final IConverter<String, T, RuntimeException> toObjectConverter;
  private final IConverter<T, String, RuntimeException> toStringConverter;

  public DefaultObjectFieldConfiguration(
      final IObjectModel<T> model,
      final IObjectModel<IValidationResult> validStateModel,
      final IValidator<String> validator,
      final IConverter<String, T, RuntimeException> toObjectConverter,
      final IConverter<T, String, RuntimeException> toStringConverter) {
    this(model, validStateModel, validator, toObjectConverter, toStringConverter, null, true, 10, new ArrayList<>());
  }

  public DefaultObjectFieldConfiguration(
      final IObjectModel<T> model,
      final IObjectModel<IValidationResult> validStateModel,
      final IValidator<String> validator,
      final IConverter<String, T, RuntimeException> toObjectConverter,
      final IConverter<T, String, RuntimeException> toStringConverter,
      final boolean isEditable) {
    this(
        model,
        validStateModel,
        validator,
        toObjectConverter,
        toStringConverter,
        null,
        isEditable,
        10,
        new ArrayList<>());
  }

  public DefaultObjectFieldConfiguration(
      final IObjectModel<T> model,
      final IObjectModel<IValidationResult> validStateModel,
      final IValidator<String> validator,
      final IConverter<String, T, RuntimeException> toObjectConverter,
      final IConverter<T, String, RuntimeException> toStringConverter,
      final IToolTipFactory toolTipFactory,
      final boolean isEditable,
      final int columns,
      final List<IActionFactory<T>> actionFactorys) {
    super(model, validStateModel, toolTipFactory, isEditable, columns, actionFactorys);
    this.validator = validator;
    this.toObjectConverter = toObjectConverter;
    this.toStringConverter = toStringConverter;
  }

  @Override
  public IConverter<String, T, RuntimeException> getToObjectConverter() {
    return this.toObjectConverter;
  }

  @Override
  public IConverter<T, String, RuntimeException> getToStringConverter() {
    return this.toStringConverter;
  }

  @Override
  public IValidator<String> getValidator() {
    return this.validator;
  }
}