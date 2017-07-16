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

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.commons.utilities.validation.IValidationResult;
import net.anwiba.commons.utilities.validation.IValidator;

@SuppressWarnings("unchecked")
public abstract class AbstractObjectFieldBuilder<O, C extends AbstractObjectFieldConfigurationBuilder<O, C>, B extends AbstractObjectFieldBuilder<O, C, B>> {

  final private Color INVALID_COLOR = new Color(240, 240, 180);
  final private Color VALID_COLOR = Color.WHITE;
  C builder;

  public AbstractObjectFieldBuilder(final C builder) {
    this.builder = builder;
  }

  protected C getConfigurationBuilder() {
    return this.builder;
  }

  public IObjectField<O> build() {
    final IObjectFieldConfiguration<O> configuration = this.builder.build();
    final AbstractObjectTextField<O> field = create(configuration);
    final Color validColor = configuration.getBackgroundColor() == null
        ? this.VALID_COLOR
        : configuration.getBackgroundColor();
    final IObjectDistributor<IValidationResult> validationResultDistributor = field.getValidationResultDistributor();
    validationResultDistributor.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        GuiUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {
            final IValidationResult validationResult = validationResultDistributor.get();
            field.getColorReciever().setBackground(
                validationResult.isValid() ? validColor : AbstractObjectFieldBuilder.this.INVALID_COLOR);
          }
        });
      }
    });
    final IValidationResult validationResult = validationResultDistributor.get();
    if (validationResult != null) {
      field.getColorReciever().setBackground(validationResult.isValid() ? this.VALID_COLOR : this.INVALID_COLOR);
    }
    return field;
  }

  protected abstract AbstractObjectTextField<O> create(IObjectFieldConfiguration<O> configuration);

  public B setModel(final IObjectModel<O> model) {
    this.builder.setModel(model);
    return (B) this;
  }

  public B setColumns(final int columns) {
    this.builder.setColumns(columns);
    return (B) this;
  }

  public B setEditable(final boolean isEditable) {
    this.builder.setEditable(isEditable);
    return (B) this;
  }

  public B setToObjectConverter(final IConverter<String, O, RuntimeException> toObjectConverter) {
    this.builder.setToObjectConverter(toObjectConverter);
    return (B) this;
  }

  public B setToStringConverter(final IConverter<O, String, RuntimeException> toStringConverter) {
    this.builder.setToStringConverter(toStringConverter);
    return (B) this;
  }

  public B setValidator(final IValidator<String> validator) {
    this.builder.setValidator(validator);
    return (B) this;
  }

  public B addValidator(final IValidator<String> validator) {
    this.builder.addValidator(validator);
    return (B) this;
  }

  public B setNotEmptyValidator(final String message) {
    addValidator(new IValidator<String>() {

      @Override
      public IValidationResult validate(final String value) {
        if (StringUtilities.isNullOrTrimmedEmpty(value)) {
          return IValidationResult.inValid(message);
        }
        return IValidationResult.valid();
      }
    });
    return (B) this;
  }

  public B setValidStateModel(final IObjectModel<IValidationResult> validStateModel) {
    this.builder.setValidStateModel(validStateModel);
    return (B) this;
  }

  public B setToolTipFactory(final IToolTipFactory factory) {
    this.builder.setToolTipFactory(factory);
    return (B) this;
  }

  public B addClearAction(final String tooltip) {
    this.builder.addClearAction(tooltip);
    return (B) this;
  }

  public B addActionFactory(final IActionFactory<O> tooltip) {
    this.builder.addActionFactory(tooltip);
    return (B) this;
  }

  public B setBackgroundColor(final Color background) {
    this.builder.setBackgroundColor(background);
    return (B) this;
  }
}
