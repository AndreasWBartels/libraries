/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.commons.datasource.connection;

import java.io.Serializable;
import java.net.URI;
import java.util.Set;

import net.anwiba.commons.datasource.DataSourceType;
import net.anwiba.commons.reference.url.IAuthentication;
import net.anwiba.commons.utilities.property.IProperties;
import net.anwiba.commons.utilities.property.Properties;
import net.anwiba.commons.utilities.string.StringUtilities;

public interface IConnectionDescription extends Serializable {

  IConnectionDescription adapt(IAuthentication authentication);

  IAuthentication getAuthentication();

  String getUrl();

  URI getURI();

  String getFormat();

  DataSourceType getDataSourceType();

  default IProperties getProperties() {
    return Properties.empty();
  }

  public static final Set<String> reserved = Set.of("schema", "table", "column");

  public static boolean isProperty(final String name, final String value) {
    if (StringUtilities.isNullOrTrimmedEmpty(name)
        || StringUtilities.isNullOrTrimmedEmpty(value)) {
      return false;
    }
    return !reserved.contains(name.toLowerCase());
  }
}
