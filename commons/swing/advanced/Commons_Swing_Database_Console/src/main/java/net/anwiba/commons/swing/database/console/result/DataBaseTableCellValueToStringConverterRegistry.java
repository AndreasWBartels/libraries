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
package net.anwiba.commons.swing.database.console.result;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.anwiba.commons.jdbc.connection.IJdbcConnectionDescription;
import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.object.IObjectToStringConverter;
import net.anwiba.commons.lang.object.ObjectPair;

public class DataBaseTableCellValueToStringConverterRegistry
    implements
    IDataBaseTableCellValueToStringConverterRegistry,
    IDataBaseTableCellValueToStringConverterProvider {

  final private List<ObjectPair<IApplicable<ObjectPair<IJdbcConnectionDescription, String>>, IObjectToStringConverter<Object>>> factories = new LinkedList<>();

  @Override
  public IObjectToStringConverter<Object> get(
      final IJdbcConnectionDescription description,
      final String columnTypeName) {
    return this.factories
        .stream()
        .filter(o -> o.getFirstObject().isApplicable(new ObjectPair<>(description, columnTypeName)))
        .findFirst()
        .map(o -> o.getSecondObject())
        .orElseGet(() -> null);
  }

  @Override
  public void add(
      final IApplicable<IJdbcConnectionDescription> applicable,
      final Set<String> typeNames,
      final IObjectToStringConverter<Object> dataBaseTableCellValueToStringConverter) {
    this.factories.add(
        new ObjectPair<IApplicable<ObjectPair<IJdbcConnectionDescription, String>>, IObjectToStringConverter<Object>>(
            new IApplicable<ObjectPair<IJdbcConnectionDescription, String>>() {
              @Override
              public boolean isApplicable(final ObjectPair<IJdbcConnectionDescription, String> context) {
                final boolean isApplicable = applicable.isApplicable(context.getFirstObject());
                final boolean equalsIgnoreCase = typeNames.contains(context.getSecondObject());
                return isApplicable && equalsIgnoreCase;
              }
            },
            dataBaseTableCellValueToStringConverter));
  }

}
