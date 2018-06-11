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
import java.util.Collections;
import java.util.List;

import net.anwiba.commons.lang.functional.ICharFilter;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.object.IObjectContainer;
import net.anwiba.commons.lang.object.IObjectProvider;
import net.anwiba.commons.lang.object.IObjectReceiver;
import net.anwiba.commons.lang.object.ObjectContainer;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.utilities.validation.AllwaysValidStringValidator;
import net.anwiba.commons.utilities.validation.IValidationResult;
import net.anwiba.commons.utilities.validation.IValidator;

public class ObjectFieldConfiguration extends AbstractObjectTextFieldConfiguration<Object> {

  static final class ObjectToStringConverter implements IConverter<Object, String, RuntimeException> {

    private final IObjectReceiver<Object> receiver;

    public ObjectToStringConverter(final IObjectReceiver<Object> receiver) {
      this.receiver = receiver;
    }

    @Override
    public String convert(final Object input) {
      this.receiver.set(input);
      if (input == null) {
        return ""; //$NON-NLS-1$
      }
      return input.toString();
    }
  }

  static final class StringToObjectConverter implements IConverter<String, Object, RuntimeException> {

    private final IObjectProvider<Object> provider;

    public StringToObjectConverter(final IObjectProvider<Object> provider) {
      this.provider = provider;
    }

    @Override
    public Object convert(final String input) {
      if (this.provider.get() == null) {
        return input;
      }
      return this.provider.get();
    }
  }

  private final IConverter<String, Object, RuntimeException> stringToObjectConverter;
  private final IConverter<Object, String, RuntimeException> objectToStringConverter;
  private final ICharFilter characterFilter;

  public ObjectFieldConfiguration(
      final IObjectModel<Object> model,
      final IObjectModel<IValidationResult> validStateModel,
      final boolean isEditable,
      final int columns,
      final List<IActionFactory<Object>> actionFactorys,
      final Color backgroundColor) {
    super(
        model,
        validStateModel,
        null,
        new BooleanModel(true),
        isEditable,
        columns,
        actionFactorys,
        Collections.emptyList(),
        null,
        backgroundColor,
        false);
    final IObjectContainer<Object> broker = new ObjectContainer<>();
    this.characterFilter = c -> true;
    this.stringToObjectConverter = new StringToObjectConverter(broker);
    this.objectToStringConverter = new ObjectToStringConverter(broker);
  }

  @Override
  public IConverter<String, Object, RuntimeException> getToObjectConverter() {
    return this.stringToObjectConverter;
  }

  @Override
  public IConverter<Object, String, RuntimeException> getToStringConverter() {
    return this.objectToStringConverter;
  }

  @Override
  public IValidator<String> getValidator() {
    return new AllwaysValidStringValidator();
  }

  @Override
  public ICharFilter getCharacterFilter() {
    return this.characterFilter;
  }

}
