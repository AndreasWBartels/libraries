/*
 * #%L
 *
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
package net.anwiba.database.swing.console.converter;

import net.anwiba.commons.jdbc.connection.IJdbcConnectionDescription;
import net.anwiba.commons.lang.object.IObjectToStringConverter;

import java.util.LinkedList;
import java.util.List;

public class DataBaseTableCellValueToStringConverterRegistry
    implements
    IDataBaseTableCellValueToStringConverterRegistry,
    IDataBaseTableCellValueToStringConverterProvider {

  final private List<DataBaseTableCellValueToStringConverter> factories = new LinkedList<>();

  public DataBaseTableCellValueToStringConverterRegistry(
      final List<DataBaseTableCellValueToStringConverter> converters) {
    converters.forEach(c -> add(c));
  }

  @Override
  public IObjectToStringConverter<Object> get(
      final IJdbcConnectionDescription description,
      final String columnTypeName) {
    return this.factories
        .stream()
        .filter(o -> o.applicable().isApplicable(description) && o.typeNames().contains(columnTypeName))
        .findFirst()
        .map(o -> o.dataBaseTableCellValueToStringConverter())
        .orElseGet(() -> null);
  }

  @Override
  public void add(final DataBaseTableCellValueToStringConverter converter) {
    this.factories.add(converter);
  }

}
