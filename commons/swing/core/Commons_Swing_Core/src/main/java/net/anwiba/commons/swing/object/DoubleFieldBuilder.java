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

import java.util.function.Function;

import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons;
import net.anwiba.commons.utilities.validation.IValidationResult;

public class DoubleFieldBuilder
    extends
    AbstractAlgebraicObjectFieldBuilder<Double, DoubleObjectFieldConfigurationBuilder, DoubleFieldBuilder> {

  public DoubleFieldBuilder() {
    super(new DoubleObjectFieldConfigurationBuilder());
  }

  @Override
  protected AbstractObjectTextField<Double> create(final IObjectFieldConfiguration<Double> configuration) {
    return new DoubleField(configuration);
  }

  public DoubleFieldBuilder addModuloSpinnerActions(final int m, final double step) {
    return addModuloSpinnerActions(m, step, 250, 100);
  }

  public DoubleFieldBuilder addModuloSpinnerActions(
      final int m,
      final double step,
      final int initialDelay,
      final int delay) {

    final Function<Double, Double> minus = input -> Optional
        .of(input)
        .convert(v1 -> (v1 - step) % m)
        .convert(v2 -> v2 < 0 ? v2 + m : v2)
        .convert(v2 -> v2 >= m ? v2 - m : v2)
        .getOr(() -> m - step);

    final Function<Double, Boolean> minusEnabler = input -> true;

    addButtonFactory(createButton(ContrastHightIcons.MINUS, minus, minusEnabler, initialDelay, delay));

    final Function<Double, Double> add = input -> Optional
        .of(input)
        .convert(v1 -> (v1 + step) % m)
        .convert(v2 -> v2 < 0 ? v2 + m : v2)
        .convert(v2 -> v2 >= m ? v2 - m : v2)
        .getOr(() -> 0.);

    final Function<Double, Boolean> addEnabler = input -> true;

    addButtonFactory(createButton(ContrastHightIcons.ADD, add, addEnabler, initialDelay, delay));

    return this;
  }

  public DoubleFieldBuilder addSpinnerActions(final double minimum, final double maximum, final double step) {
    return addSpinnerActions(minimum, maximum, step, 250, 100);
  }

  public DoubleFieldBuilder addSpinnerActions(
      final double minimum,
      final double maximum,
      final double step,
      final int initialDelay,
      final int delay) {
    addValidatorFactory(converter -> value -> Optional
        .of(value) //
        .convert(v1 -> converter.convert(v1))
        .convert(v2 -> isValid(v2, minimum, maximum))
        .getOr(() -> IValidationResult.valid()));

    final Function<Double, Double> minus = input -> Optional
        .of(input)
        .convert(v1 -> v1 - step)
        .convert(v2 -> v2 < minimum ? minimum : v2)
        .convert(v3 -> v3 > maximum ? maximum : v3)
        .getOr(() -> maximum);

    final Function<Double, Boolean> minusEnabler = input -> Optional
        .of(input)
        .convert(v4 -> v4 > minimum && minimum < maximum)
        .getOr(() -> true);

    addButtonFactory(createButton(ContrastHightIcons.MINUS, minus, minusEnabler, initialDelay, delay));

    final Function<Double, Double> add = input -> Optional
        .of(input)
        .convert(v1 -> v1 + step)
        .convert(v2 -> v2 < minimum ? minimum : v2)
        .convert(v3 -> v3 > maximum ? maximum : v3)
        .getOr(() -> minimum);

    final Function<Double, Boolean> addEnabler = input -> Optional
        .of(input)
        .convert(v4 -> v4 < maximum && minimum < maximum)
        .getOr(() -> true);

    addButtonFactory(createButton(ContrastHightIcons.ADD, add, addEnabler, initialDelay, delay));

    return this;
  }

  private IValidationResult isValid(final Double value, final double minimum, final double maximum) {
    return value < minimum //
        ? IValidationResult.inValid(value + " < " + minimum) //$NON-NLS-1$
        : value > maximum ? //
            IValidationResult.inValid(value + " > " + maximum) : // //$NON-NLS-1$
            IValidationResult.valid();
  }
}
