/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2019 Andreas Bartels
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
// Copyright (c) 2009 by Andreas W. Bartels
package net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.unit;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.spatial.coordinatereferencesystem.Authority;

public class UnitFactory {

  private static ILogger logger = Logging.getLogger(UnitFactory.class.getName());

  public static Unit create(final String name, final double factor, final Authority authority) {
    return new Unit(name, getType(name), factor, authority);
  }

  @SuppressWarnings("nls")
  private static UnitType getType(final String name) {
    final String[] names = name.split(" "); //$NON-NLS-1$
    for (final String value : names) {
      if (StringUtilities
          .containsIgnoreCase(value.trim(), "meter", "metre", "m", "foot", "yard", "chain", "mile", "link", "fathom")) {
        return UnitType.DISTANCE;
      }
      if (StringUtilities.containsIgnoreCase(value.trim(), "radian", "grad", "degree", "gon")) {
        return UnitType.ANGLE;
      }
    }
    logger.log(ILevel.WARNING, "unknown unit " + name);
    return UnitType.UNKNOWN;
  }
}
