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
import java.util.Collection;

import net.anwiba.commons.lang.functional.ICharFilter;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.utilities.validation.IValidationResult;
import net.anwiba.commons.utilities.validation.IValidator;

public interface IObjectFieldConfiguration<T> {

  IValidator<String> getValidator();

  IConverter<String, T, RuntimeException> getToObjectConverter();

  IConverter<T, String, RuntimeException> getToStringConverter();

  int getColumns();

  boolean isEditable();

  IToolTipFactory getToolTipFactory();

  IObjectModel<T> getModel();

  IObjectModel<IValidationResult> getValidationResultModel();

  Collection<IActionFactory<T>> getActionFactorys();

  Color getBackgroundColor();

  IKeyListenerFactory<T> getKeyListenerFactory();

  ICharFilter getCharacterFilter();

  IBooleanModel getEnabledModel();

  Collection<IButtonFactory<T>> getButtonFactorys();

  boolean isDisguise();

}
