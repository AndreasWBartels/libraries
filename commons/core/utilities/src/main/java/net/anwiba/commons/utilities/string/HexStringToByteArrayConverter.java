/*
 * #%L
 * anwiba commons core
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

package net.anwiba.commons.utilities.string;

import net.anwiba.commons.lang.functional.ConversionException;
import net.anwiba.commons.lang.functional.IConverter;

public class HexStringToByteArrayConverter implements IConverter<String, byte[], ConversionException> {

  @Override
  public byte[] convert(final String value) throws ConversionException {
    if (value == null) {
      return new byte[0];
    }
    final byte[] array = new byte[value.length() / 2];
    for (int i = 0; i < value.length(); i += 2) {
      final int valueOf = Integer.valueOf(value.substring(i, i + 2), 16);
      array[i / 2] = (byte) (valueOf > 128 ? valueOf - 256 : valueOf);
    }
    return array;
  }

}
