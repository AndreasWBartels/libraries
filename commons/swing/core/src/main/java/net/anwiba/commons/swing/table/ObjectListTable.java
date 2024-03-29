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

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.object.IKeyListenerFactory;
import net.anwiba.commons.swing.object.IObjectField;
import net.anwiba.commons.swing.object.StringFieldBuilder;
import net.anwiba.commons.swing.table.filter.IColumToStringConverter;
import net.anwiba.commons.swing.table.filter.ObjectListTableFilter;
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
    ((ObjectListTableModel<T>) tableModel.getObjectTableModel()).setChangeable(!getSortStateModel().isTrue());
    getSortStateModel().addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        ((ObjectListTableModel<T>) tableModel.getObjectTableModel()).setChangeable(!getSortStateModel().isTrue());
      }
    });
  }

  public IObjectModel<IRowFilter> getRowFilterModel() {
    return this.rowFilterModel;
  }

  @Override
  public JComponent getComponent() {
    if (this.configuration.isTextFieldEnable()
        || !this.configuration.getTextFieldActionConfiguration().isEmpty()
        || this.configuration.getAccessoryHeaderPanelFactory() != null
        || this.configuration.getAccessoryFooterPanelFactory() != null) {
      final IObjectField<String> stringField = createStringField();
      final JPanel contentPane = new JPanel();
      contentPane.setLayout(new BorderLayout());
      if (stringField != null || this.configuration.getAccessoryHeaderPanelFactory() != null) {
        final JPanel headPane = new JPanel();
        headPane.setLayout(new BorderLayout(0, 0));
        Optional.of(stringField).consume(c -> headPane.add(c.getComponent(), BorderLayout.CENTER));
        if (this.configuration.getAccessoryHeaderPanelFactory() != null) {
          headPane.add(this.configuration.getAccessoryHeaderPanelFactory().create(getTableModel()), BorderLayout.EAST);
        }
        contentPane.add(headPane, BorderLayout.NORTH);
      }
      contentPane.add(BorderLayout.CENTER, super.getComponent());
      if (this.configuration.getAccessoryFooterPanelFactory() != null) {
        contentPane.add(this.configuration.getAccessoryFooterPanelFactory().create(getTableModel()),
            BorderLayout.SOUTH);
      }
      return contentPane;
    }
    final IObjectDistributor<IAcceptor<T>> rowFilterDistributor = this.configuration.getRowFilterDistributor();
    if (rowFilterDistributor != null) {
      final IObjectModel<IRowFilter> rowFilterModel = getRowFilterModel();
      final IChangeableObjectListener listener = () -> {
        if (rowFilterDistributor.isEmpty()) {
          rowFilterModel.set(null);
          return;
        }
        rowFilterModel
            .set(new ObjectListTableFilter<T>(rowFilterDistributor.get()));
      };
      rowFilterDistributor.addChangeListener(listener);
      listener.objectChanged();
    }
    return super.getComponent();
  }

  protected IObjectField<String> createStringField() {
    if (!this.configuration.isTextFieldEnable()
        && this.configuration.getTextFieldActionConfiguration().isEmpty()) {
      return null;
    }

    final StringFieldBuilder builder = new StringFieldBuilder();

    this.configuration.getTextFieldActionConfiguration()
        .getFactories()
        .forEach(
            f -> builder.addActionFactory(
                (model, document, enabledDistributor, clearBlock) -> f.create(
                    getTableModel(),
                    getSelectionIndexModel(),
                    getSelectionModel(),
                    enabledDistributor,
                    model,
                    clearBlock)));

    builder.addClearAction(ObjectListTableMessages.clear);

    Optional.of(this.configuration.getTextFieldKeyListenerFactory())
        .consume(
            f -> builder.setKeyListenerFactory(new IKeyListenerFactory<String>() {

              @Override
              public KeyListener create(
                  final IObjectModel<String> model,
                  final PlainDocument document,
                  final IBlock<RuntimeException> clearBlock) {
                return f.create(getTableModel(), getSelectionIndexModel(), getSelectionModel(), model, clearBlock);
              }
            }));
    builder.setColumns(40);
    final IObjectField<String> stringField = builder.build();
    final IObjectModel<String> model = stringField.getModel();

    if (this.configuration.isFilterable()) {
      final IObjectModel<IRowFilter> rowFilterModel = getRowFilterModel();
      final IColumToStringConverter filterToStringConverter = this.configuration.getRowFilterToStringConverter();
      final IObjectDistributor<IAcceptor<T>> rowFilterDistributor = this.configuration.getRowFilterDistributor();
      if (filterToStringConverter != null && rowFilterDistributor != null) {
        @SuppressWarnings("hiding")
        final IChangeableObjectListener listener = () -> {
          final String value = model.get();
          if (StringUtilities.isNullOrTrimmedEmpty(value) && rowFilterDistributor.isEmpty()) {
            rowFilterModel.set(null);
          } else if (!StringUtilities.isNullOrTrimmedEmpty(value) && rowFilterDistributor.isEmpty()) {
            rowFilterModel.set(new ObjectListTableFilter<T>(value, filterToStringConverter));
          } else if (StringUtilities.isNullOrTrimmedEmpty(value) && !rowFilterDistributor.isEmpty()) {
            rowFilterModel.set(new ObjectListTableFilter<T>(rowFilterDistributor.get()));
          } else {
            rowFilterModel
                .set(new ObjectListTableFilter<T>(value, filterToStringConverter, rowFilterDistributor.get()));
          }
        };
        rowFilterDistributor.addChangeListener(listener);
        model.addChangeListener(listener);
        listener.objectChanged();
      } else if (filterToStringConverter != null && rowFilterDistributor == null) {
        @SuppressWarnings("hiding")
        final IChangeableObjectListener listener = () -> {
          final String value = model.get();
          if (StringUtilities.isNullOrTrimmedEmpty(value)) {
            rowFilterModel.set(null);
            return;
          }
          rowFilterModel.set(new ObjectListTableFilter<T>(value, filterToStringConverter));
        };
        model.addChangeListener(listener);
        listener.objectChanged();
      } else if (filterToStringConverter == null && rowFilterDistributor != null) {
        final IChangeableObjectListener listener = () -> {
          if (rowFilterDistributor.isEmpty()) {
            rowFilterModel.set(null);
            return;
          }
          rowFilterModel
              .set(new ObjectListTableFilter<T>(rowFilterDistributor.get()));
        };
        rowFilterDistributor.addChangeListener(listener);
        listener.objectChanged();
      }
    }
    return stringField;
  }
}
