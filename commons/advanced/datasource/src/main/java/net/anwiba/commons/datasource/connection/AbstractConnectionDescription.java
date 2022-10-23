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

import java.util.Objects;

import net.anwiba.commons.datasource.DataSourceType;
import net.anwiba.commons.utilities.property.IProperties;

public abstract class AbstractConnectionDescription implements IConnectionDescription {

  @Override
  public int hashCode() {
    return Objects.hash(dataSourceType, properties);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AbstractConnectionDescription other = (AbstractConnectionDescription) obj;
    return this.dataSourceType == other.dataSourceType && Objects.equals(this.properties, other.properties);
  }

  private final DataSourceType dataSourceType;
  private final IProperties properties;

  public AbstractConnectionDescription(final DataSourceType dataSourceType, final IProperties properties) {
    this.dataSourceType = dataSourceType;
    this.properties = properties;
  }

  @Override
  public DataSourceType getDataSourceType() {
    return this.dataSourceType;
  }

  @Override
  public IProperties getProperties() {
    return this.properties;
  }
}