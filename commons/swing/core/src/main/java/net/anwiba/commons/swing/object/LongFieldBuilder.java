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

public class LongFieldBuilder
    extends
    AbstractAlgebraicObjectFieldBuilder<Long, LongObjectFieldConfigurationBuilder, LongFieldBuilder> {

  public LongFieldBuilder() {
    super(new LongObjectFieldConfigurationBuilder());
  }

  @Override
  protected AbstractObjectTextField<Long> create(final IObjectFieldConfiguration<Long> configuration) {
    return new LongField(configuration);
  }

  public LongFieldBuilder addSliderActions(final long minimum, final long maximum, final long step) {
    addValidatorFactory(converter -> value -> Optional
        .of(value) //
        .convert(v1 -> converter.convert(v1))
        .convert(v2 -> isValid(v2, minimum, maximum))
        .getOr(() -> IValidationResult.valid()));

    final Function<Long, Long> minus = input -> Optional
        .of(input)
        .convert(v1 -> v1 - step)
        .convert(v2 -> v2 < minimum ? minimum : v2)
        .convert(v3 -> v3 > maximum ? maximum : v3)
        .getOr(() -> maximum);

    final Function<Long, Boolean> minusEnabler = input -> Optional
        .of(input)
        .convert(v4 -> v4 > minimum && minimum < maximum)
        .getOr(() -> true);

    addButtonFactory(createButton(ContrastHightIcons.MINUS, minus, minusEnabler, 250, 100));

    final Function<Long, Long> add = input -> Optional
        .of(input)
        .convert(v1 -> v1 + step)
        .convert(v2 -> v2 < minimum ? minimum : v2)
        .convert(v3 -> v3 > maximum ? maximum : v3)
        .getOr(() -> minimum);

    final Function<Long, Boolean> addEnabler = input -> Optional
        .of(input)
        .convert(v4 -> v4 < maximum && minimum < maximum)
        .getOr(() -> true);

    addButtonFactory(createButton(ContrastHightIcons.ADD, add, addEnabler, 250, 100));
    return this;
  }

  public LongFieldBuilder addSpinnerActions(final long minimum, final long maximum, final long step) {
    return addSpinnerActions(minimum, maximum, step, 250, 100);
  }

  public LongFieldBuilder addSpinnerActions(
      final long minimum,
      final long maximum,
      final long step,
      final int initialDelay,
      final int delay) {
    addValidatorFactory(converter -> value -> Optional
        .of(value) //
        .convert(v1 -> converter.convert(v1))
        .convert(v2 -> isValid(v2, minimum, maximum))
        .getOr(() -> IValidationResult.valid()));

    final Function<Long, Long> minus = input -> Optional
        .of(input)
        .convert(v1 -> v1 - step)
        .convert(v2 -> v2 < minimum ? minimum : v2)
        .convert(v3 -> v3 > maximum ? maximum : v3)
        .getOr(() -> maximum);

    final Function<Long, Boolean> minusEnabler = input -> Optional
        .of(input)
        .convert(v4 -> v4 > minimum & minimum < maximum)
        .getOr(() -> true);

    addButtonFactory(createButton(ContrastHightIcons.MINUS, minus, minusEnabler, initialDelay, delay));

    final Function<Long, Long> add = input -> Optional
        .of(input)
        .convert(v1 -> v1 + step)
        .convert(v2 -> v2 < minimum ? minimum : v2)
        .convert(v3 -> v3 > maximum ? maximum : v3)
        .getOr(() -> minimum);

    final Function<Long, Boolean> addEnabler = input -> Optional
        .of(input)
        .convert(v4 -> v4 < maximum & minimum < maximum)
        .getOr(() -> true);

    addButtonFactory(createButton(ContrastHightIcons.ADD, add, addEnabler, initialDelay, delay));
    return this;
  }

  public IValidationResult isValid(final Long value, final long minimum, final long maximum) {
    return value < minimum //
        ? IValidationResult.inValid(value + " < " + minimum) //$NON-NLS-1$
        : value > maximum ? //
            IValidationResult.inValid(value + " > " + maximum) : // //$NON-NLS-1$
            IValidationResult.valid();
  }
}
