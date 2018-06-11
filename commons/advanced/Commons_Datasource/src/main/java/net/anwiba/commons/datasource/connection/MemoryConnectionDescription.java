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

import java.net.URI;
import java.net.URISyntaxException;

import net.anwiba.commons.datasource.DataSourceType;
import net.anwiba.commons.datasource.DataSourceVersion;

public class MemoryConnectionDescription implements IMemoryConnectionDescription {

  private static final long serialVersionUID = 5936114248828237698L;

  @Override
  public URI getURI() {
    try {
      return new URI("memory"); //$NON-NLS-1$
    } catch (final URISyntaxException exception) {
      throw new RuntimeException(exception);
    }
  }

  @Override
  public String getUrl() {
    return "memory"; //$NON-NLS-1$
  }

  @Override
  public boolean equals(final Object obj) {
    return obj instanceof IMemoryConnectionDescription;
  }

  @Override
  public int hashCode() {
    return getUrl().hashCode();
  }

  @Override
  public DataSourceType getDataSourceType() {
    return DataSourceType.MEMORY;
  }

  @Override
  public DataSourceVersion getVersion() {
    return new DataSourceVersion(1, 0, null);
  }
}
