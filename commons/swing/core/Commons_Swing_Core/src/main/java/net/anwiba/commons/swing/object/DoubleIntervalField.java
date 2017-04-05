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

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.utilities.SpringLayoutUtilities;
import net.anwiba.commons.utilities.interval.DoubleInterval;
import net.anwiba.commons.utilities.number.StringToDoubleConverter;
import net.anwiba.commons.utilities.validation.IValidationResult;
import net.anwiba.commons.utilities.validation.IValidator;

import java.text.MessageFormat;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class DoubleIntervalField implements IObjectField<DoubleInterval> {

  private static ILogger logger = Logging.getLogger(DoubleIntervalField.class.getName());

  public static final class ValueValidator implements IValidator<String> {
    private final double minimum;
    private final double maximum;
    StringToDoubleConverter converter = new StringToDoubleConverter();

    public ValueValidator(final double minimum, final double maximum) {
      this.minimum = minimum;
      this.maximum = maximum;
    }

    @Override
    public IValidationResult validate(final String string) {
      try {
        final Double valueObject = this.converter.convert(string);
        if (valueObject == null) {
          return IValidationResult.valid();
        }
        final double value = valueObject.doubleValue();
        final boolean isInBoundery = Double.isNaN(this.minimum)
            ? (Double.isNaN(this.maximum)
                ? true
                : value <= this.maximum)
            : Double.isNaN(this.maximum)
                ? this.minimum <= value
                : this.minimum <= value && value <= this.maximum;
        if (isInBoundery) {
          return IValidationResult.valid();
        }
        return IValidationResult.inValid(MessageFormat.format(
            "Value is out of bounderies ({0}, {1})",
            toString(this.minimum),
            toString(this.maximum)));
      } catch (final NumberFormatException exception) {
        return IValidationResult.inValid(exception.getLocalizedMessage());
      }
    }

    private String toString(final double value) {
      if (Double.isNaN(value)) {
        return "none";
      }
      return String.valueOf(value);
    }
  }

  private final IObjectModel<IValidationResult> validationModel;
  private final IObjectModel<IValidationResult> minimumValidationModel = new ObjectModel<>(IValidationResult.valid());
  private final IObjectModel<IValidationResult> maximumValidationModel = new ObjectModel<>(IValidationResult.valid());
  private final IObjectModel<Double> minimumValueModel = new ObjectModel<>();
  private final IObjectModel<Double> maximumValueModel = new ObjectModel<>();
  private final IObjectModel<DoubleInterval> model;

  private JPanel component;
  private final double maximum;
  private final double minimum;
  private final IConverter<Double, String, RuntimeException> toStringFormater;

  public DoubleIntervalField(
    final IConverter<Double, String, RuntimeException> toStringFormater,
    final double minimum,
    final double maximum,
    final IObjectModel<DoubleInterval> model,
    final IObjectModel<IValidationResult> validationModel) {
    this.model = model;
    this.minimum = minimum;
    this.maximum = maximum;
    this.toStringFormater = toStringFormater;
    this.validationModel = validationModel;
    final IObjectModel<Double> minimumValueModel = this.minimumValueModel;
    final IObjectModel<Double> maximumValueModel = this.maximumValueModel;
    minimumValueModel.set(model.get() == null
        ? null
        : Double.valueOf(model.get().getMinimum()));
    maximumValueModel.set(model.get() == null
        ? null
        : Double.valueOf(model.get().getMaximum()));
    final IObjectModel<IValidationResult> minimumValidationModel = this.minimumValidationModel;
    final IObjectModel<IValidationResult> maximumValidationModel = this.maximumValidationModel;
    validationModel.set(checkValid());
    final IChangeableObjectListener validationListener = new IChangeableObjectListener() {

      @SuppressWarnings("synthetic-access")
      @Override
      public void objectChanged() {
        logger.log(ILevel.DEBUG, "validation state changed"); //$NON-NLS-1$
        logger.log(ILevel.DEBUG, MessageFormat.format("minimum value: {0}", //$NON-NLS-1$
            (minimumValidationModel.get().isValid()
                ? "valid" //$NON-NLS-1$
                : MessageFormat.format("invalid: {0}", minimumValidationModel.get().getMessage())))); //$NON-NLS-1$
        logger.log(ILevel.DEBUG, "maximum value: " //$NON-NLS-1$
            + (maximumValidationModel.get().isValid()
                ? "valid" //$NON-NLS-1$
                : MessageFormat.format("invalid: {0}", maximumValidationModel.get().getMessage()))); //$NON-NLS-1$
        if (!minimumValidationModel.get().isValid()) {
          validationModel.set(IValidationResult.inValid("Illegal minimum value. "
              + minimumValidationModel.get().getMessage()));
          return;
        }
        if (!maximumValidationModel.get().isValid()) {
          validationModel.set(IValidationResult.inValid("Illegal maximum value. "
              + maximumValidationModel.get().getMessage()));
          return;
        }
        final IValidationResult validationResult = checkValid();
        if (validationResult.isValid()) {
          model.set(minimumValueModel.get() == null && maximumValueModel.get() == null
              ? null
              : new DoubleInterval(minimumValueModel.get().doubleValue(), maximumValueModel.get().doubleValue()));
        }
        validationModel.set(validationResult);
      }
    };
    minimumValidationModel.addChangeListener(validationListener);
    maximumValidationModel.addChangeListener(validationListener);
    model.addChangeListener(new IChangeableObjectListener() {

      @SuppressWarnings("synthetic-access")
      @Override
      public void objectChanged() {
        logger.log(ILevel.DEBUG, MessageFormat.format("model changed: {0}", model.get())); //$NON-NLS-1$
        minimumValueModel.set(model.get() == null
            ? null
            : Double.valueOf(model.get().getMinimum()));
        maximumValueModel.set(model.get() == null
            ? null
            : Double.valueOf(model.get().getMaximum()));
      }
    });
    minimumValueModel.addChangeListener(new IChangeableObjectListener() {

      @SuppressWarnings("synthetic-access")
      @Override
      public void objectChanged() {
        logger.log(ILevel.DEBUG, MessageFormat.format("minimum model changed: {0}", minimumValueModel.get())); //$NON-NLS-1$
        final IValidationResult validationResult = checkValid();
        if (validationResult.isValid()) {
          model.set(minimumValueModel.get() == null && maximumValueModel.get() == null
              ? null
              : new DoubleInterval(minimumValueModel.get().doubleValue(), maximumValueModel.get().doubleValue()));
        }
        validationModel.set(validationResult);
      }
    });
    maximumValueModel.addChangeListener(new IChangeableObjectListener() {

      @SuppressWarnings("synthetic-access")
      @Override
      public void objectChanged() {
        logger.log(ILevel.DEBUG, MessageFormat.format("maximum model changed: {0}", maximumValueModel.get())); //$NON-NLS-1$
        final IValidationResult validationResult = checkValid();
        if (validationResult.isValid()) {
          model.set(minimumValueModel.get() == null && maximumValueModel.get() == null
              ? null
              : new DoubleInterval(minimumValueModel.get().doubleValue(), maximumValueModel.get().doubleValue()));
        }
        validationModel.set(validationResult);
      }
    });
  }

  protected IValidationResult checkValid() {
    if (((this.minimumValueModel.get() == null && this.maximumValueModel.get() == null) || ((this.minimumValueModel
        .get() != null && this.maximumValueModel.get() != null) && (this.minimumValueModel.get().doubleValue() < this.maximumValueModel
        .get()
        .doubleValue())))) {
      return IValidationResult.valid();
    }
    if (this.minimumValueModel.get() == null) {
      return IValidationResult.inValid("Missing minimum value.");
    }
    if (this.maximumValueModel.get() == null) {
      return IValidationResult.inValid("Missing maximum value.");
    }
    if (this.maximumValueModel.get().doubleValue() == this.minimumValueModel.get().doubleValue()) {
      return IValidationResult.inValid("Minimum value equals maximum value.");
    }
    return IValidationResult.inValid("Minimum value is larger than maximum value.");
  }

  @Override
  public JComponent getComponent() {
    if (this.component == null) {
      this.component = createComponent();
    }
    return this.component;
  }

  private JPanel createComponent() {
    final ValueValidator validator = new ValueValidator(this.minimum, this.maximum);
    final DoubleField minimumField = createDoubleField(this.minimumValueModel, this.minimumValidationModel, validator);
    final DoubleField maximumField = createDoubleField(this.maximumValueModel, this.maximumValidationModel, validator);
    @SuppressWarnings("serial")
    final JPanel panel = new JPanel(new SpringLayout()) {
      @Override
      public void requestFocus() {
        minimumField.getComponent().requestFocus();
      }
    };
    panel.add(minimumField.getComponent());
    panel.add(maximumField.getComponent());
    SpringLayoutUtilities.makeCompactGrid(panel, 2, 1, 0, 0, 6, 0);
    return panel;
  }

  private DoubleField createDoubleField(
      final IObjectModel<Double> valueModel,
      final IObjectModel<IValidationResult> validationModel,
      final ValueValidator validator) {
    final DoubleObjectFieldConfigurationBuilder builder = new DoubleObjectFieldConfigurationBuilder();
    builder.setModel(valueModel);
    builder.setValidStateModel(validationModel);
    builder.setValidator(validator);
    builder.setToStringConverter(this.toStringFormater);
    return new DoubleField(builder.build());
  }

  @Override
  public IObjectModel<DoubleInterval> getModel() {
    return this.model;
  }

  @Override
  public IObjectDistributor<IValidationResult> getValidationResultDistributor() {
    return this.validationModel;
  }
}