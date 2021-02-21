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
import net.anwiba.commons.utilities.boole.BooleanParser;
import net.anwiba.commons.utilities.boole.BooleanStringValidator;
import net.anwiba.commons.utilities.boole.StringToBooleanConverter;

public class BooleanObjectFieldConfigurationBuilder
    extends
    AbstractObjectFieldConfigurationBuilder<Boolean, BooleanObjectFieldConfigurationBuilder> {
  private static BooleanParser parser = new BooleanParser();

  public BooleanObjectFieldConfigurationBuilder() {
    super(
        new BooleanStringValidator(parser),
        new StringToBooleanConverter(parser),
        new IConverter<Boolean, String, RuntimeException>() {

          @Override
          public String convert(final Boolean value) {
            if (value == null) {
              return null;
            }
            return value.toString();
          }
        });
  }
}
