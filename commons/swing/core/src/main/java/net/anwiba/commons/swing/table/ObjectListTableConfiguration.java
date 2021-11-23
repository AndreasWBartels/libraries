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

import java.util.List;

import javax.swing.JComponent;

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.swing.table.action.ITableActionConfiguration;
import net.anwiba.commons.swing.table.action.ITableTextFieldActionConfiguration;
import net.anwiba.commons.swing.table.action.ITableTextFieldKeyListenerFactory;
import net.anwiba.commons.swing.table.filter.IColumToStringConverter;
import net.anwiba.commons.utilities.collection.ListUtilities;
import net.anwiba.commons.utilities.string.IStringSubstituter;

public class ObjectListTableConfiguration<T> extends ObjectTableConfiguration<T>
    implements
    IObjectListTableConfiguration<T> {

  private final List<IColumnValueProvider<T>> columnValueProviders;
  private final List<IColumnValueAdaptor<T>> columnValueAdaptors;
  private final IColumToStringConverter columnToStringConverter;
  private final IColumnClassProvider columnClassProvider;
  private final ITableTextFieldActionConfiguration<T> textFieldActionConfiguration;
  private final ITableTextFieldKeyListenerFactory<T> textFieldKeyListenerFactory;
  private final IFactory<IObjectTableModel<T>, JComponent, RuntimeException> accessoryHeaderPanelFactory;
  private final IFactory<IObjectTableModel<T>, JComponent, RuntimeException> accessoryFooterPanelFactory;
  private final IObjectDistributor<IAcceptor<T>> rowFilterDistributor;

  public ObjectListTableConfiguration(
      final IStringSubstituter toolTipSubstituter,
      final IColumToStringConverter columnToStringConverter,
      final IObjectDistributor<IAcceptor<T>> filterModel,
      final int autoRizeMode,
      final int selectionMode,
      final int preferredVisibleRowCount,
      final List<IObjectListColumnConfiguration<T>> columnConfigurations,
      final IMouseListenerFactory<T> headerMouseListenerFactory,
      final IMouseListenerFactory<T> tableMouseListenerFactory,
      final IKeyListenerFactory<T> keyListenerFactory,
      final ITableActionConfiguration<T> actionConfiguration,
      final ITableTextFieldActionConfiguration<T> textFieldActionConfiguration,
      final IFactory<IObjectTableModel<T>, JComponent, RuntimeException> accessoryHeaderPanelFactory,
      final IFactory<IObjectTableModel<T>, JComponent, RuntimeException> accessoryFooterPanelFactory,
      final ITableTextFieldKeyListenerFactory<T> textFieldKeyListenerFactory) {
    super(
        autoRizeMode,
        selectionMode,
        preferredVisibleRowCount,
        toolTipSubstituter,
        columnConfigurations,
        headerMouseListenerFactory,
        tableMouseListenerFactory,
        keyListenerFactory,
        actionConfiguration);
    this.columnToStringConverter = columnToStringConverter;
    this.rowFilterDistributor = filterModel;
    this.textFieldActionConfiguration = textFieldActionConfiguration;
    this.accessoryHeaderPanelFactory = accessoryHeaderPanelFactory;
    this.accessoryFooterPanelFactory = accessoryFooterPanelFactory;
    this.textFieldKeyListenerFactory = textFieldKeyListenerFactory;
    this.columnValueProviders = ListUtilities.convert(
        columnConfigurations,
        new IConverter<IObjectListColumnConfiguration<T>, IColumnValueProvider<T>, RuntimeException>() {

          @Override
          public IColumnValueProvider<T> convert(final IObjectListColumnConfiguration<T> input)
              throws RuntimeException {
            return input.getColumnValueProvider();
          }
        });
    this.columnValueAdaptors = ListUtilities.convert(
        columnConfigurations,
        new IConverter<IObjectListColumnConfiguration<T>, IColumnValueAdaptor<T>, RuntimeException>() {

          @Override
          public IColumnValueAdaptor<T> convert(final IObjectListColumnConfiguration<T> input) throws RuntimeException {
            return input.getColumnValueAdaptor();
          }
        });

    this.columnClassProvider = new IColumnClassProvider() {

      @Override
      public Class<?> getClass(final int columnIndex) {
        final IObjectListColumnConfiguration<T> configuration = columnConfigurations.get(columnIndex);
        if (configuration == null) {
          return Object.class;
        }
        return configuration.getColumnClass();
      }
    };
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
    return this.columnToStringConverter != null || this.rowFilterDistributor != null;
  }

  @Override
  public IObjectDistributor<IAcceptor<T>> getRowFilterDistributor() {
    return this.rowFilterDistributor;
  }

  @Override
  public IColumToStringConverter getRowFilterToStringConverter() {
    return this.columnToStringConverter;
  }

  @Override
  public IColumnClassProvider getColumnClassProvider() {
    return this.columnClassProvider;
  }

  @Override
  public ITableTextFieldActionConfiguration<T> getTextFieldActionConfiguration() {
    return this.textFieldActionConfiguration;
  }

  @Override
  public ITableTextFieldKeyListenerFactory<T> getTextFieldKeyListenerFactory() {
    return this.textFieldKeyListenerFactory;
  }

  @Override
  public IFactory<IObjectTableModel<T>, JComponent, RuntimeException> getAccessoryHeaderPanelFactory() {
    return this.accessoryHeaderPanelFactory;
  }

  @Override
  public IFactory<IObjectTableModel<T>, JComponent, RuntimeException> getAccessoryFooterPanelFactory() {
    return this.accessoryFooterPanelFactory;
  }
}
