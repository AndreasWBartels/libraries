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
package net.anwiba.commons.swing.table;

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.swing.table.action.ITableActionConfiguration;
import net.anwiba.commons.swing.table.filter.IColumToStringConverter;
import net.anwiba.commons.utilities.collection.ListUtilities;

import java.util.List;

public class ObjectListTableConfiguration<T> extends ObjectTableConfiguration<T> implements
    IObjectListTableConfiguration<T> {

  private List<IColumnValueProvider<T>> columnValueProviders;
  private List<IColumnValueAdaptor<T>> columnValueAdaptors;
  private final IColumToStringConverter columnToStringConverter;

  public ObjectListTableConfiguration(
    final IColumToStringConverter columnToStringConverter,
    final int selectionMode,
    final int preferredVisibleRowCount,
    final List<IObjectListColumnConfiguration<T>> columnConfigurations,
    final IMouseListenerFactory<T> mouseListenerFactory,
    final IKeyListenerFactory<T> keyListenerFactory,
    final ITableActionConfiguration<T> actionConfiguration) {
    super(
        selectionMode,
        preferredVisibleRowCount,
        columnConfigurations,
        mouseListenerFactory,
        keyListenerFactory,
        actionConfiguration);
    this.columnToStringConverter = columnToStringConverter;
    this.columnValueProviders =
        ListUtilities.convert(
            columnConfigurations,
            new IConverter<IObjectListColumnConfiguration<T>, IColumnValueProvider<T>, RuntimeException>() {

              @Override
              public IColumnValueProvider<T> convert(final IObjectListColumnConfiguration<T> input)
                  throws RuntimeException {
                return input.getColumnValueProvider();
              }
            });
    this.columnValueAdaptors =
        ListUtilities.convert(
            columnConfigurations,
            new IConverter<IObjectListColumnConfiguration<T>, IColumnValueAdaptor<T>, RuntimeException>() {

              @Override
              public IColumnValueAdaptor<T> convert(final IObjectListColumnConfiguration<T> input)
                  throws RuntimeException {
                return input.getColumnValueAdaptor();
              }
            });
  }

  @Override
  public List<IColumnValueProvider<T>> getColumnValueProviders() {
    return this.columnValueProviders;
  }

  @Override
  public List<IColumnValueAdaptor<T>> getColumnValueAdaptors() {
    return this.columnValueAdaptors;
  }

  @Override
  public boolean isFilterable() {
    return this.columnToStringConverter != null;
  }

  @Override
  public IColumToStringConverter getRowFilterToStringConverter() {
    return this.columnToStringConverter;
  }
}
