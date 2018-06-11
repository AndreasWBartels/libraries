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
package net.anwiba.commons.swing.object.demo;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.junit.runner.RunWith;

import de.jdemo.annotation.Demo;
import de.jdemo.junit.DemoAsTestRunner;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.object.DoubleIntervalField;
import net.anwiba.commons.utilities.interval.DoubleInterval;
import net.anwiba.commons.utilities.number.DoubleToStringConverter;
import net.anwiba.commons.utilities.validation.IValidationResult;

@RunWith(DemoAsTestRunner.class)
public class DoubleIntervalFieldDemo extends AbstractObjectFieldDemo {

  @Demo
  public void demo() {
    final DecimalFormat format = createFormater();
    final IObjectModel<DoubleInterval> intervalModel = new ObjectModel<>();
    final DoubleIntervalField field = new DoubleIntervalField(
        new DoubleToStringConverter(format),
        0,
        100,
        1,
        intervalModel,
        new ObjectModel<>(IValidationResult.valid()));
    show(createPanel(field));
  }

  private DecimalFormat createFormater() {
    final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator('.');
    symbols.setGroupingSeparator(' ');
    final DecimalFormat format = new DecimalFormat();
    format.setDecimalFormatSymbols(symbols);
    format.setGroupingUsed(false);
    format.setMinimumIntegerDigits(1);
    format.setMinimumFractionDigits(2);
    format.setMaximumFractionDigits(2);
    format.setDecimalSeparatorAlwaysShown(false);
    return format;
  }

}
