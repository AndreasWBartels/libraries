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
import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.model.SelectionModel;
import net.anwiba.commons.swing.component.IComponentProvider;
import net.anwiba.commons.swing.table.action.ITableActionFactory;
import net.anwiba.commons.swing.table.listener.SelectionListener;
import net.anwiba.commons.swing.table.listener.TableSelectionListener;
import net.anwiba.commons.swing.utilities.SpringLayoutUtilities;

public class ObjectTable<T> implements IComponentProvider {

  final private SelectionModel<T> selectionModel = new SelectionModel<>();
  final private JComponent component;
  private final IObjectTableModel<T> tableModel;
  private final IBooleanDistributor sortStateModel;

  public ObjectTable(final IObjectTableConfiguration<T> configuration, final IObjectTableModel<T> tableModel) {
    this.tableModel = tableModel;
    final ObjectTableRowSorter<T> tableRowSorter = configuration.getRowSorter(tableModel);
    final ISortedRowMapper sortedRowMapper = new SortedRowMapper<>(tableRowSorter);
    final Table table = new Table(tableModel);
    table.setRowSorter(tableRowSorter);
    table.setAutoResizeMode(configuration.getAutoizeMode());
    table.setSelectionMode(configuration.getSelectionMode());
    table.setAutoCreateColumnsFromModel(false);
    table.setPreferredScrollableViewportSize(
        new Dimension(
            100,
            configuration.getPreferredVisibleRowCount() * (table.getRowHeight() + table.getRowMargin())));
    final TableColumnModel columnModel = table.getColumnModel();
    for (int i = 0; i < columnModel.getColumnCount(); i++) {
      applyToColumn(columnModel.getColumn(i), configuration.getColumnConfiguration(i));
    }
    final ListSelectionModel tableSelectionModel = table.getSelectionModel();
    tableSelectionModel.addListSelectionListener(
        new TableSelectionListener<>(tableModel, tableSelectionModel, this.selectionModel, sortedRowMapper));
    this.selectionModel.addSelectionListener(
        new SelectionListener<>(tableModel, tableSelectionModel, this.selectionModel, sortedRowMapper));
    final ISelectionIndexModel<T> selectionIndexModel = new SelectionIndexModel<>(
        tableSelectionModel,
        sortedRowMapper,
        this.selectionModel);
    this.sortStateModel = tableRowSorter == null ? new BooleanModel(false) : tableRowSorter.getSortStateModel();
    final IMouseListenerFactory<T> mouseListenerFactory = configuration.getMouseListenerFactory();
    table.addMouseListener(
        mouseListenerFactory.create(tableModel, selectionIndexModel, this.getSelectionModel(), this.sortStateModel));
    final IKeyListenerFactory<T> keyListenerFactory = configuration.getKeyListenerFactory();
    table.addKeyListener(
        keyListenerFactory.create(tableModel, selectionIndexModel, this.selectionModel, this.sortStateModel));
    if (configuration.getTableActionConfiguration().isEmpty()) {
      this.component = new JScrollPane(table);
      return;
    }
    final Iterable<ITableActionFactory<T>> factories = configuration.getTableActionConfiguration().getFactories();
    final JPanel buttonPanel = new JPanel(new SpringLayout());
    for (final ITableActionFactory<T> factory : factories) {
      final Action action = factory.create(tableModel, selectionIndexModel, this.selectionModel, this.sortStateModel);
      final JButton button = new JButton(action);
      buttonPanel.add(button);
    }
    SpringLayoutUtilities.makeCompactGrid(buttonPanel, 1, buttonPanel.getComponentCount(), 6, 6, 6, 6);
    this.component = new JPanel(new BorderLayout());
    this.component.add(new JScrollPane(table), BorderLayout.CENTER);
    final JPanel buttonPanelContainer = new JPanel(new BorderLayout());
    buttonPanelContainer.add(buttonPanel, BorderLayout.NORTH);
    this.component.add(buttonPanelContainer, BorderLayout.EAST);
  }

  public IObjectTableModel<T> getTableModel() {
    return this.tableModel;
  }

  public IBooleanDistributor getSortStateModel() {
    return this.sortStateModel;
  }

  private void applyToColumn(final TableColumn column, final IColumnConfiguration configuration) {
    if (configuration == null) {
      return;
    }
    if (configuration.getCellEditor() != null) {
      column.setCellEditor(configuration.getCellEditor());
    }
    if (configuration.getCellRenderer() != null) {
      column.setCellRenderer(configuration.getCellRenderer());
    }
    if (configuration.getPreferredWidth() > 0) {
      column.setPreferredWidth(configuration.getPreferredWidth());
    }
    if (configuration.getHeaderValue() != null) {
      column.setHeaderValue(configuration.getHeaderValue());
    }
  }

  @Override
  public JComponent getComponent() {
    return this.component;
  }

  public ISelectionModel<T> getSelectionModel() {
    return this.selectionModel;
  }
}