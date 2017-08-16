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
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.action.ConfigurableActionBuilder;
import net.anwiba.commons.swing.action.IActionProcedure;
import net.anwiba.commons.swing.icon.GuiIcons;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.commons.utilities.validation.AggregatingStringValidator;
import net.anwiba.commons.utilities.validation.AllwaysValidStringValidator;
import net.anwiba.commons.utilities.validation.IValidationResult;
import net.anwiba.commons.utilities.validation.IValidator;

public abstract class AbstractObjectFieldConfigurationBuilder<T, C extends AbstractObjectFieldConfigurationBuilder<T, C>> {

  private boolean isEditable = true;
  private int columns = 10;
  private final List<IValidator<String>> validators = new ArrayList<>();
  private IConverter<String, T, RuntimeException> toObjectConverter;
  private IConverter<T, String, RuntimeException> toStringConverter;
  private IToolTipFactory toolTipFactory = new IToolTipFactory() {

    @Override
    public String create(final IValidationResult validationResult, final String text) {
      if (!validationResult.isValid()) {
        return validationResult.getMessage();
      }
      if (StringUtilities.isNullOrEmpty(text)) {
        return null;
      }
      return text;
    }
  };
  private IObjectModel<IValidationResult> validStateModel = new ObjectModel<>(IValidationResult.valid());
  private IObjectModel<T> model = new ObjectModel<>();
  private final List<IActionFactory<T>> actionFactorys = new ArrayList<>();
  private Color background;
  private IKeyListenerFactory<T> keyListenerFactory;

  public AbstractObjectFieldConfigurationBuilder(
      final IValidator<String> validator,
      final IConverter<String, T, RuntimeException> toObjectConverter,
      final IConverter<T, String, RuntimeException> toStringConverter) {
    this.validators.add(validator);
    this.toObjectConverter = toObjectConverter;
    this.toStringConverter = toStringConverter;
  }

  public IObjectFieldConfiguration<T> build() {
    final IValidator<String> validator = this.validators.isEmpty()
        ? new AllwaysValidStringValidator()
        : this.validators.size() == 1 ? this.validators.get(0) : new AggregatingStringValidator(this.validators);
    return new DefaultObjectFieldConfiguration<>(
        this.model,
        this.validStateModel,
        validator,
        this.toObjectConverter,
        this.toStringConverter,
        this.toolTipFactory,
        this.isEditable,
        this.columns,
        this.actionFactorys,
        this.keyListenerFactory,
        this.background);
  }

  @SuppressWarnings("unchecked")
  public C setKeyListenerFactory(final IKeyListenerFactory<T> keyListenerFactory) {
    this.keyListenerFactory = keyListenerFactory;
    return (C) this;
  }

  @SuppressWarnings("unchecked")
  public C setToolTipFactory(final IToolTipFactory factory) {
    this.toolTipFactory = factory;
    return (C) this;
  }

  @SuppressWarnings("unchecked")
  public C setModel(final IObjectModel<T> model) {
    this.model = model;
    return (C) this;
  }

  @SuppressWarnings("unchecked")
  public C setValidStateModel(final IObjectModel<IValidationResult> validStateModel) {
    this.validStateModel = validStateModel;
    return (C) this;
  }

  @SuppressWarnings("unchecked")
  public C setValidator(final IValidator<String> validator) {
    this.validators.clear();;
    this.validators.add(validator);
    return (C) this;
  }

  @SuppressWarnings("unchecked")
  public C addValidator(final IValidator<String> validator) {
    this.validators.add(validator);
    return (C) this;
  }

  @SuppressWarnings("unchecked")
  public C setToObjectConverter(final IConverter<String, T, RuntimeException> toObjectConverter) {
    this.toObjectConverter = toObjectConverter;
    return (C) this;
  }

  @SuppressWarnings("unchecked")
  public C setToStringConverter(final IConverter<T, String, RuntimeException> toStringConverter) {
    this.toStringConverter = toStringConverter;
    return (C) this;
  }

  @SuppressWarnings("unchecked")
  public C setEditable(final boolean isEditable) {
    this.isEditable = isEditable;
    return (C) this;
  }

  @SuppressWarnings("unchecked")
  public C setColumns(final int columns) {
    this.columns = columns;
    return (C) this;
  }

  @SuppressWarnings("unchecked")
  public C addActionFactory(final IActionFactory<T> actionFactory) {
    this.actionFactorys.add(actionFactory);
    return (C) this;
  }

  @SuppressWarnings("unchecked")
  public C setBackgroundColor(final Color background) {
    this.background = background;
    return (C) this;
  }

  @SuppressWarnings("unchecked")
  public C addClearAction(final String tooltip) {
    addActionFactory(new IActionFactory<T>() {

      @Override
      public AbstractAction create(
          final IObjectModel<T> context,
          final Document document,
          final IBlock<RuntimeException> clearBlock) throws RuntimeException {
        final ConfigurableActionBuilder builder = new ConfigurableActionBuilder();
        final AbstractAction action = builder
            .setIcon(GuiIcons.EDIT_CLEAR_LOCATIONBAR_ICON)
            .setTooltip(tooltip)
            .setProcedure(new IActionProcedure() {

              @Override
              public void execute(final Component value) throws RuntimeException {
                clearBlock.execute();
              }
            })
            .build();
        action.setEnabled(document.getLength() != 0);
        document.addDocumentListener(new DocumentListener() {

          @Override
          public void removeUpdate(final DocumentEvent e) {
            action.setEnabled(document.getLength() != 0);
          }

          @Override
          public void insertUpdate(final DocumentEvent e) {
            action.setEnabled(document.getLength() != 0);
          }

          @Override
          public void changedUpdate(final DocumentEvent e) {
            action.setEnabled(document.getLength() != 0);
          }
        });
        return action;
      }
    });

    return (C) this;
  }
}
