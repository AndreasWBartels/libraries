/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.spatial.ckan.utilities;

import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.spatial.ckan.json.schema.v1_0.Named;
import net.anwiba.spatial.ckan.json.types.I18String;

public class CkanUtilities {

  public static String toString(final Named named) {
    return Optional
        .of(toString(named.getDisplay_name()))
        .or(toString(named.getTitle()))
        .or(toString(named.getName()))
        .or(toString(named.getDescription()))
        .get();
  }

  public static String toString(final I18String string) {
    return Optional.of(string).convert(v -> v.toString()).accept(n -> !StringUtilities.isNullOrEmpty(n)).get();
  }

}
