/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.database.oracle;

import net.anwiba.commons.nls.NLS;

public class ValidationResultMessages extends NLS {

  public static String contains_whitespace_character;
  public static String null_or_empty_string_value;
  public static String UnkownCoordinateReferenceSystemValidator_unkown_srid;
  public static String whitespace_at_character;
  public static String unsupported_character_at_index;
  public static String null_value;
  public static String empty_string_value;
  public static String more_than_i0_characters;
  public static String reserved_name;

  static {
    // initialize resource bundle
    initialize(ValidationResultMessages.class, (c, r) -> c.getResourceAsStream(r));
  }

  private ValidationResultMessages() {
  }
}
