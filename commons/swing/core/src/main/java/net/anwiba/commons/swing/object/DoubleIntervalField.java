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

import java.text.MessageFormat;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.optional.If;
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
            ? (Double.isNaN(this.maximum) ? true : value <= this.maximum)
            : Double.isNaN(this.maximum) ? this.minimum <= value : this.minimum <= value && value <= this.maximum;
        if (isInBoundery) {
          return IValidationResult.valid();
        }
        return IValidationResult.inValid(
            MessageFormat
                .format(ObjectFieldMessages.ValueIsOutOfBounderies_0_1, toString(this.minimum), toString(this.maximum)));
      } catch (final NumberFormatException exception) {
        return IValidationResult.inValid(exception.getLocalizedMessage());
      }
    }

    private String toString(final double value) {
      if (Double.isNaN(value)) {
        return ObjectFieldMessages.none;
      }
      return String.valueOf(value);
    }
  }

  private final IObjectModel<IValidationResult> validationResultModel;
  private final IObjectModel<IValidationResult> minimumValidationModel = new ObjectModel<>(IValidationResult.valid());
  private final IObjectModel<IValidationResult> maximumValidationModel = new ObjectModel<>(IValidationResult.valid());
  private final IObjectModel<Double> minimumValueModel = new ObjectModel<>();
  private final IObjectModel<Double> maximumValueModel = new ObjectModel<>();
  private final IObjectModel<DoubleInterval> model;

  private JPanel component;
  private final double maximum;
  private final double minimum;
  private final double stepSize;
  private final IConverter<Double, String, RuntimeException> toStringFormater;

  public DoubleIntervalField(
      final IConverter<Double, String, RuntimeException> toStringFormater,
      final double minimum,
      final double maximum,
      final double stepSize,
      final IObjectModel<DoubleInterval> model,
      final IObjectModel<IValidationResult> validationModel) {
    this.stepSize = stepSize;
    this.model = model;
    this.minimum = minimum;
    this.maximum = maximum;
    this.toStringFormater = toStringFormater;
    this.validationResultModel = validationModel;
    this.minimumValueModel.set(model.get() == null ? null : Double.valueOf(model.get().getMinimum()));
    this.maximumValueModel.set(model.get() == null ? null : Double.valueOf(model.get().getMaximum()));
    validationModel.set(checkValid());
    final IChangeableObjectListener validationListener = new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        logger.log(ILevel.DEBUG, "validation state changed"); //$NON-NLS-1$
        logger.log(
            ILevel.DEBUG,
            MessageFormat.format(
                "minimum value: {0}", //$NON-NLS-1$
                (DoubleIntervalField.this.minimumValidationModel.get().isValid()
                    ? "valid" //$NON-NLS-1$
                    : MessageFormat
                        .format("invalid: {0}", DoubleIntervalField.this.minimumValidationModel.get().getMessage())))); //$NON-NLS-1$
        logger.log(
            ILevel.DEBUG,
            "maximum value: " //$NON-NLS-1$
                + (DoubleIntervalField.this.maximumValidationModel.get().isValid()
                    ? "valid" //$NON-NLS-1$
                    : MessageFormat
                        .format("invalid: {0}", DoubleIntervalField.this.maximumValidationModel.get().getMessage()))); //$NON-NLS-1$
        if (!DoubleIntervalField.this.minimumValidationModel.get().isValid()) {
          validationModel.set(
              IValidationResult.inValid(
                  ObjectFieldMessages.IllegalMinimumValue + DoubleIntervalField.this.minimumValidationModel.get().getMessage()));
          return;
        }
        if (!DoubleIntervalField.this.maximumValidationModel.get().isValid()) {
          validationModel.set(
              IValidationResult.inValid(
                  ObjectFieldMessages.IllegalMaximumValue + DoubleIntervalField.this.maximumValidationModel.get().getMessage()));
          return;
        }
        final IValidationResult validationResult = checkValid();
        if (validationResult.isValid()) {
          model.set(
              DoubleIntervalField.this.minimumValueModel.get() == null
                  && DoubleIntervalField.this.maximumValueModel.get() == null
                      ? null
                      : new DoubleInterval(
                          DoubleIntervalField.this.minimumValueModel.get().doubleValue(),
                          DoubleIntervalField.this.maximumValueModel.get().doubleValue()));
        }
        validationModel.set(validationResult);
      }
    };
    this.minimumValidationModel.addChangeListener(validationListener);
    this.maximumValidationModel.addChangeListener(validationListener);
    model.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        logger.log(ILevel.DEBUG, MessageFormat.format("model changed: {0}", model.get())); //$NON-NLS-1$
        DoubleIntervalField.this.minimumValueModel
            .set(model.get() == null ? null : Double.valueOf(model.get().getMinimum()));
        DoubleIntervalField.this.maximumValueModel
            .set(model.get() == null ? null : Double.valueOf(model.get().getMaximum()));
      }
    });
    this.minimumValueModel.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        logger.log(
            ILevel.DEBUG,
            MessageFormat.format("minimum model changed: {0}", DoubleIntervalField.this.minimumValueModel.get())); //$NON-NLS-1$
        final IValidationResult validationResult = checkValid();
        if (validationResult.isValid()) {
          model.set(
              DoubleIntervalField.this.minimumValueModel.get() == null
                  && DoubleIntervalField.this.maximumValueModel.get() == null
                      ? null
                      : new DoubleInterval(
                          DoubleIntervalField.this.minimumValueModel.get().doubleValue(),
                          DoubleIntervalField.this.maximumValueModel.get().doubleValue()));
        }
        validationModel.set(validationResult);
      }
    });
    this.maximumValueModel.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        logger.log(
            ILevel.DEBUG,
            MessageFormat.format("maximum model changed: {0}", DoubleIntervalField.this.maximumValueModel.get())); //$NON-NLS-1$
        final IValidationResult validationResult = checkValid();
        if (validationResult.isValid()) {
          model.set(
              DoubleIntervalField.this.minimumValueModel.get() == null
                  && DoubleIntervalField.this.maximumValueModel.get() == null
                      ? null
                      : new DoubleInterval(
                          DoubleIntervalField.this.minimumValueModel.get().doubleValue(),
                          DoubleIntervalField.this.maximumValueModel.get().doubleValue()));
        }
        validationModel.set(validationResult);
      }
    });
  }

  protected IValidationResult checkValid() {
    if (((this.minimumValueModel.get() == null && this.maximumValueModel.get() == null)
        || ((this.minimumValueModel.get() != null && this.maximumValueModel.get() != null)
            && (this.minimumValueModel.get().doubleValue() < this.maximumValueModel.get().doubleValue())))) {
      return IValidationResult.valid();
    }
    if (this.minimumValueModel.get() == null) {
      return IValidationResult.inValid(ObjectFieldMessages.MissingMinimumValue);
    }
    if (this.maximumValueModel.get() == null) {
      return IValidationResult.inValid(ObjectFieldMessages.MissingMaximumValue);
    }
    if (this.maximumValueModel.get().doubleValue() == this.minimumValueModel.get().doubleValue()) {
      return IValidationResult.inValid(ObjectFieldMessages.MinimumValueEqualsMaximumValue);
    }
    return IValidationResult.inValid(ObjectFieldMessages.MinimumValueIsLargerThanMaximumValue);
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
    final IObjectField<Double> minimumField = createDoubleField(
        this.minimumValueModel,
        this.minimumValidationModel,
        validator);
    final IObjectField<Double> maximumField = createDoubleField(
        this.maximumValueModel,
        this.maximumValidationModel,
        validator);
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

  private IObjectField<Double> createDoubleField(
      final IObjectModel<Double> valueModel,
      final IObjectModel<IValidationResult> validationModel,
      final ValueValidator validator) {
    final DoubleFieldBuilder builder = new DoubleFieldBuilder()
        .setModel(valueModel)
        .setValidStateModel(validationModel)
        .setValidator(validator)
        .setToStringConverter(this.toStringFormater);
    If.isTrue(Double.isFinite(this.stepSize)).excecute(
        () -> builder.addSpinnerActions(
            Double.isFinite(this.minimum) ? this.minimum : Double.NEGATIVE_INFINITY,
            Double.isFinite(this.maximum) ? this.maximum : Double.POSITIVE_INFINITY,
            this.stepSize));
    return builder.build();
  }

  @Override
  public IObjectModel<DoubleInterval> getModel() {
    return this.model;
  }

  @Override
  public IObjectDistributor<IValidationResult> getValidationResultDistributor() {
    return this.validationResultModel;
  }
}