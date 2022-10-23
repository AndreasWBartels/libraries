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

package net.anwiba.commons.swing.object.temporal;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.function.Function;

import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons;
import net.anwiba.commons.swing.object.AbstractAlgebraicObjectFieldBuilder;
import net.anwiba.commons.swing.object.AbstractObjectTextField;
import net.anwiba.commons.swing.object.IObjectFieldConfiguration;
import net.anwiba.commons.utilities.validation.IValidationResult;

public class DateFieldBuilder
    extends
    AbstractAlgebraicObjectFieldBuilder<Date, DateObjectFieldConfigurationBuilder, DateFieldBuilder> {

  public DateFieldBuilder() {
    super(new DateObjectFieldConfigurationBuilder());
  }

  @Override
  protected AbstractObjectTextField<Date> create(final IObjectFieldConfiguration<Date> configuration) {
    return new DateField(configuration);
  }

  public DateFieldBuilder addSliderActions(
      final Date minimum,
      final Date maximum,
      final long step,
      final TemporalUnit unit) {
    addValidatorFactory(converter -> value -> Optional
        .of(value) //
        .convert(v1 -> converter.convert(v1))
        .convert(v2 -> isValid(v2, minimum, maximum))
        .getOr(() -> IValidationResult.valid()));

    final Function<Date, Date> add = input -> Optional
        .of(input)
        .convert(v1 -> {
          final LocalDateTime ldt = LocalDateTime.ofInstant(v1.toInstant(), ZoneId.systemDefault());
          final LocalDateTime plus = ldt.plus(step, unit);
          return Date.from(plus.atZone(ZoneId.systemDefault()).toInstant());
        })
        .convert(v2 -> v2.getTime() < minimum.getTime() ? minimum : v2)
        .convert(v3 -> v3.getTime() > maximum.getTime() ? maximum : v3)
        .getOr(() -> minimum);

    final Function<Date, Date> minus = input -> Optional
        .of(input)
        .convert(v1 -> {
          final LocalDateTime ldt = LocalDateTime.ofInstant(v1.toInstant(), ZoneId.systemDefault());
          final LocalDateTime result = ldt.minus(step, unit);
          return Date.from(result.atZone(ZoneId.systemDefault()).toInstant());
        })
        .convert(v2 -> v2.getTime() < minimum.getTime() ? minimum : v2)
        .convert(v3 -> v3.getTime() > maximum.getTime() ? maximum : v3)
        .getOr(() -> maximum);

    final Function<Date, Boolean> minusEnabler = input -> Optional
        .of(input)
        .convert(v4 -> v4.getTime() > minimum.getTime() && minimum.getTime() < maximum.getTime())
        .getOr(() -> true);

    addButtonFactory(createButton(ContrastHightIcons.MINUS, minus, minusEnabler, 250, 100));

    final Function<Date, Boolean> addEnabler = input -> Optional
        .of(input)
        .convert(v4 -> v4.getTime() < maximum.getTime() && minimum.getTime() < maximum.getTime())
        .getOr(() -> true);

    addButtonFactory(createButton(ContrastHightIcons.ADD, add, addEnabler, 250, 100));
    return this;
  }

  public IValidationResult isValid(final Date value, final Date minimum, final Date maximum) {
    return value.getTime() < minimum.getTime() //
        ? IValidationResult.inValid(value + " < " + minimum) //$NON-NLS-1$
        : value.getTime() > maximum.getTime() ? //
            IValidationResult.inValid(value + " > " + maximum) : // //$NON-NLS-1$
            IValidationResult.valid();
  }
}
