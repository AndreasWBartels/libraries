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

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.object.StringField;
import net.anwiba.commons.swing.object.StringObjectFieldConfigurationBuilder;
import net.anwiba.commons.swing.table.filter.ContainsFilter;
import net.anwiba.commons.swing.table.filter.IColumToStringConverter;
import net.anwiba.commons.utilities.string.StringUtilities;

public class ObjectListTable<T> extends ObjectTable<T> {

  final private IObjectModel<IRowFilter> rowFilterModel;
  private final IObjectListTableConfiguration<T> configuration;

  public ObjectListTable(final IObjectListTableConfiguration<T> configuration, final List<T> list) {
    super(configuration, new FilterableObjectTableModel<>(new ObjectListTableModel<>(
        list,
        configuration.getColumnValueProviders(),
        configuration.getColumnValueAdaptors())));
    this.configuration = configuration;
    final FilterableObjectTableModel<T> tableModel = (FilterableObjectTableModel<T>) getTableModel();
    final IObjectModel<IRowFilter> rowFilterModel = new ObjectModel<>();
    rowFilterModel.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        tableModel.setRowFilter(rowFilterModel.get());
      }
    });
    this.rowFilterModel = rowFilterModel;
    ((ObjectListTableModel<T>) tableModel.getObjectTableModel()).setChangeable(!getSortStateModel().get());
    getSortStateModel().addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        ((ObjectListTableModel<T>) tableModel.getObjectTableModel()).setChangeable(!getSortStateModel().get());
      }
    });
  }

  public IObjectModel<IRowFilter> getRowFilterModel() {
    return this.rowFilterModel;
  }

  @Override
  public JComponent getComponent() {
    if (this.configuration.isFilterable()) {
      final StringField stringField = new StringField(new StringObjectFieldConfigurationBuilder().addClearAction(
          "clear filter").build());
      final IObjectModel<String> model = stringField.getModel();
      final IObjectModel<IRowFilter> rowFilterModel = getRowFilterModel();
      final IColumToStringConverter filterToStringConverter = this.configuration.getRowFilterToStringConverter();
      model.addChangeListener(new IChangeableObjectListener() {

        @Override
        public void objectChanged() {
          final String value = model.get();
          if (StringUtilities.isNullOrTrimmedEmpty(value)) {
            rowFilterModel.set(null);
            return;
          }
          rowFilterModel.set(new ContainsFilter(value, filterToStringConverter));
        }
      });
      final JPanel contentPane = new JPanel();
      contentPane.setLayout(new BorderLayout());
      contentPane.add(stringField.getComponent(), BorderLayout.NORTH);
      // contentPane.add(BorderLayout.NORTH, toolBar);
      contentPane.add(BorderLayout.CENTER, new JScrollPane(super.getComponent()));
      return contentPane;
    }
    return super.getComponent();
  }
}
