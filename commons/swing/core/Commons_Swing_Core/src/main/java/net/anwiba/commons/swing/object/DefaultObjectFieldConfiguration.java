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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.lang.functional.ICharFilter;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.utilities.validation.IValidationResult;
import net.anwiba.commons.utilities.validation.IValidator;

public class DefaultObjectFieldConfiguration<T> extends AbstractObjectTextFieldConfiguration<T> {

  private final IValidator<String> validator;
  private final IConverter<String, T, RuntimeException> toObjectConverter;
  private final IConverter<T, String, RuntimeException> toStringConverter;
  private ICharFilter characterFilter;

  public DefaultObjectFieldConfiguration(
      final IObjectModel<T> model,
      final IObjectModel<IValidationResult> validStateModel,
      final IValidator<String> validator,
      final ICharFilter characterFilter,
      final IConverter<String, T, RuntimeException> toObjectConverter,
      final IConverter<T, String, RuntimeException> toStringConverter) {
    this(
        model,
        validStateModel,
        validator,
        characterFilter,
        toObjectConverter,
        toStringConverter,
        null,
        new BooleanModel(true),
        true,
        10,
        new ArrayList<>(),
        new ArrayList<>(),
        null,
        null,
        false);
  }

  public DefaultObjectFieldConfiguration(
      final IObjectModel<T> model,
      final IObjectModel<IValidationResult> validStateModel,
      final IValidator<String> validator,
      final ICharFilter characterFilter,
      final IConverter<String, T, RuntimeException> toObjectConverter,
      final IConverter<T, String, RuntimeException> toStringConverter,
      final boolean isEditable) {
    this(
        model,
        validStateModel,
        validator,
        characterFilter,
        toObjectConverter,
        toStringConverter,
        null,
        new BooleanModel(true),
        isEditable,
        10,
        new ArrayList<>(),
        new ArrayList<>(),
        null,
        null,
        false);
  }

  public DefaultObjectFieldConfiguration(
      final IObjectModel<T> model,
      final IObjectModel<IValidationResult> validStateModel,
      final IValidator<String> validator,
      final ICharFilter characterFilter,
      final IConverter<String, T, RuntimeException> toObjectConverter,
      final IConverter<T, String, RuntimeException> toStringConverter,
      final IToolTipFactory toolTipFactory,
      final IBooleanModel enabledModel,
      final boolean isEditable,
      final int columns,
      final List<IActionFactory<T>> actionFactorys,
      final List<IButtonFactory<T>> buttonFactorys,
      final IKeyListenerFactory<T> keyListenerFactory,
      final Color backgroundColor,
      final boolean isDisguise) {
    super(
        model,
        validStateModel,
        toolTipFactory,
        enabledModel,
        isEditable,
        columns,
        actionFactorys,
        buttonFactorys,
        keyListenerFactory,
        backgroundColor,
        isDisguise);
    this.validator = validator;
    this.characterFilter = characterFilter;
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

  @Override
  public ICharFilter getCharacterFilter() {
    return this.characterFilter;
  }
}