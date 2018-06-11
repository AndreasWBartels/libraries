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
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.text.PlainDocument;

import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.object.IKeyListenerFactory;
import net.anwiba.commons.swing.object.IObjectField;
import net.anwiba.commons.swing.object.StringFieldBuilder;
import net.anwiba.commons.swing.table.filter.ContainsFilter;
import net.anwiba.commons.swing.table.filter.IColumToStringConverter;
import net.anwiba.commons.utilities.string.StringUtilities;

public class ObjectListTable<T> extends ObjectTable<T> {

  final private IObjectModel<IRowFilter> rowFilterModel;
  private final IObjectListTableConfiguration<T> configuration;

  public ObjectListTable(final IObjectListTableConfiguration<T> configuration, final List<T> list) {
    super(
        configuration,
        new FilterableObjectTableModel<>(
            new ObjectListTableModel<>(
                list,
                configuration.getColumnValueProviders(),
                configuration.getColumnValueAdaptors(),
                configuration.getColumnClassProvider())));
    this.configuration = configuration;
    final FilterableObjectTableModel<T> tableModel = (FilterableObjectTableModel<T>) getTableModel();
    @SuppressWarnings("hiding")
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
    if (this.configuration.isTextFieldEnable() || !this.configuration.getTextFieldActionConfiguration().isEmpty()) {
      final StringFieldBuilder builder = new StringFieldBuilder().addClearAction(ObjectListTableMessages.clear);

      this.configuration.getTextFieldActionConfiguration().getFactories().forEach(
          f -> builder.addActionFactory(
              (model, document, enabledDistributor, clearBlock) -> f.create(
                  getTableModel(),
                  getSelectionIndexModel(),
                  getSelectionModel(),
                  enabledDistributor,
                  model,
                  clearBlock)));

      Optional.of(this.configuration.getTextFieldKeyListenerFactory()).consume(
          f -> builder.setKeyListenerFactory(new IKeyListenerFactory<String>() {

            @Override
            public KeyListener create(
                final IObjectModel<String> model,
                final PlainDocument document,
                final IBlock<RuntimeException> clearBlock) {
              return f.create(getTableModel(), getSelectionIndexModel(), getSelectionModel(), model, clearBlock);
            }
          }));

      final IObjectField<String> stringField = builder.build();
      final IObjectModel<String> model = stringField.getModel();

      if (this.configuration.isFilterable()) {
        @SuppressWarnings("hiding")
        final IObjectModel<IRowFilter> rowFilterModel = getRowFilterModel();
        final IColumToStringConverter filterToStringConverter = this.configuration.getRowFilterToStringConverter();
        model.addChangeListener(() -> {
          final String value = model.get();
          if (StringUtilities.isNullOrTrimmedEmpty(value)) {
            rowFilterModel.set(null);
            return;
          }
          rowFilterModel.set(new ContainsFilter(value, filterToStringConverter));
        });
      }
      final JPanel contentPane = new JPanel();
      contentPane.setLayout(new BorderLayout());
      contentPane.add(stringField.getComponent(), BorderLayout.NORTH);
      // contentPane.add(BorderLayout.NORTH, toolBar);
      contentPane.add(BorderLayout.CENTER, super.getComponent());
      return contentPane;
    }
    return super.getComponent();
  }
}
