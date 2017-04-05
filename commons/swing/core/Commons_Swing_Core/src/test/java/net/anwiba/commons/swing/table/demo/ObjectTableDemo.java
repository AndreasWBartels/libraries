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
package net.anwiba.commons.swing.table.demo;

import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ISelectionListener;
import net.anwiba.commons.model.SelectionEvent;
import net.anwiba.commons.swing.object.StringField;
import net.anwiba.commons.swing.table.ColumnConfiguration;
import net.anwiba.commons.swing.table.FilterableObjectTableModel;
import net.anwiba.commons.swing.table.IObjectTableConfiguration;
import net.anwiba.commons.swing.table.IObjectTableModel;
import net.anwiba.commons.swing.table.ISelectionIndexModel;
import net.anwiba.commons.swing.table.ObjectTable;
import net.anwiba.commons.swing.table.ObjectTableConfigurationBuilder;
import net.anwiba.commons.swing.table.action.AddTableRowActionFactory;
import net.anwiba.commons.swing.table.action.EditTableActionFactory;
import net.anwiba.commons.swing.table.action.ITableActionClosure;
import net.anwiba.commons.swing.table.action.MoveTableRowDownActionFactory;
import net.anwiba.commons.swing.table.action.MoveTableRowUpActionFactory;
import net.anwiba.commons.swing.table.action.RemoveTableRowActionFactory;
import net.anwiba.commons.swing.table.filter.ColumnToStingConverter;
import net.anwiba.commons.swing.table.filter.ContainsFilter;
import net.anwiba.commons.swing.table.renderer.BooleanRenderer;
import net.anwiba.commons.swing.table.renderer.NumberTableCellRenderer;
import net.anwiba.commons.swing.table.renderer.ObjectTableCellRenderer;
import net.anwiba.commons.utilities.collection.IterableUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;

import de.jdemo.annotation.Demo;
import de.jdemo.extensions.SwingDemoCase;
import de.jdemo.junit.DemoAsTestRunner;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;

import org.junit.runner.RunWith;

@RunWith(DemoAsTestRunner.class)
public class ObjectTableDemo extends SwingDemoCase {

  public IObjectTableConfiguration<DemoObject> createObjectTableConfiguration(final boolean isSortable) {
    final ObjectTableConfigurationBuilder<DemoObject> builder = new ObjectTableConfigurationBuilder<>();
    addColumnDescriptions(builder, isSortable);
    return builder.build();
  }

  @SuppressWarnings("nls")
  public void addColumnDescriptions(final ObjectTableConfigurationBuilder<DemoObject> builder, final boolean isSortable) {
    builder.addColumnConfiguration(new ColumnConfiguration(
        "Nummer",
        new NumberTableCellRenderer(),
        80,
        isSortable,
        null));
    builder
        .addColumnConfiguration(new ColumnConfiguration("Name", new ObjectTableCellRenderer(), 200, isSortable, null));
    builder.addColumnConfiguration(new ColumnConfiguration(
        "Value",
        new NumberTableCellRenderer(),
        200,
        isSortable,
        null));
    builder.addColumnConfiguration(new ColumnConfiguration("Flag", new BooleanRenderer(), 40, isSortable, null));
  }

  @Demo
  public void actions() {
    final DemoObjectFactory factory = new DemoObjectFactory(4711);
    final ObjectTableConfigurationBuilder<DemoObject> builder = new ObjectTableConfigurationBuilder<>();
    addColumnDescriptions(builder, true);
    builder.addActionFactory(new AddTableRowActionFactory<>(new ITableActionClosure<DemoObject>() {

      @Override
      public void execute(
          final Component component,
          final IObjectTableModel<DemoObject> tableModel,
          final ISelectionIndexModel<DemoObject> selectionIndexModel) {
        tableModel.add(factory.createObject());
        selectionIndexModel.set(tableModel.size() - 1);
      }
    }));
    builder.addActionFactory(new EditTableActionFactory<>(new ITableActionClosure<DemoObject>() {

      @Override
      public void execute(
          final Component component,
          final IObjectTableModel<DemoObject> tableModel,
          final ISelectionIndexModel<DemoObject> selectionIndexModel) {
        final int index = selectionIndexModel.getMinimum();
        final DemoObject object = factory.createObject();
        tableModel.set(index, object);
      }
    }));
    builder.addActionFactory(new RemoveTableRowActionFactory<DemoObject>());
    builder.addActionFactory(new MoveTableRowUpActionFactory<DemoObject>());
    builder.addActionFactory(new MoveTableRowDownActionFactory<DemoObject>());
    show(new ObjectTable<>(builder.build(), new DemoTableModel(factory.createObjectList(20))).getComponent());
  }

  @SuppressWarnings("nls")
  @Demo
  public void sortable() {
    final DemoObjectFactory factory = new DemoObjectFactory(4711);
    final ObjectTableConfigurationBuilder<DemoObject> builder = new ObjectTableConfigurationBuilder<>();
    builder.addColumnConfiguration(new ColumnConfiguration("Nummer", new NumberTableCellRenderer(), 80, true, null));
    builder.addColumnConfiguration(new ColumnConfiguration("Name", new ObjectTableCellRenderer(), 200, true, null));
    builder.addColumnConfiguration(new ColumnConfiguration("Value", new NumberTableCellRenderer(), 200, false, null));
    builder.addColumnConfiguration(new ColumnConfiguration("Flag", new BooleanRenderer(), 40, true, null));
    show(new ObjectTable<>(builder.build(), new DemoTableModel(factory.createObjectList(20))).getComponent());
  }

  @Demo
  public void selection() {
    final DemoObjectFactory factory = new DemoObjectFactory(4711);
    final JPanel panel = new JPanel(new GridLayout(2, 1));
    final List<DemoObject> objectList = factory.createObjectList(20);
    final ObjectTable<DemoObject> masterTable =
        new ObjectTable<>(createObjectTableConfiguration(true), new DemoTableModel(objectList));
    final ObjectTable<DemoObject> minorTable =
        new ObjectTable<>(createObjectTableConfiguration(true), new DemoTableModel(objectList));
    panel.add(masterTable.getComponent());
    panel.add(minorTable.getComponent());
    masterTable.getSelectionModel().addSelectionListener(new ISelectionListener<DemoObject>() {

      @Override
      public void selectionChanged(final SelectionEvent<DemoObject> event) {
        final List<DemoObject> objects = IterableUtilities.asList(masterTable.getSelectionModel().getSelectedObjects());
        minorTable.getSelectionModel().setSelectedObjects(objects);
      }
    });
    minorTable.getSelectionModel().addSelectionListener(new ISelectionListener<DemoObject>() {

      @Override
      public void selectionChanged(final SelectionEvent<DemoObject> event) {
        final List<DemoObject> objects = IterableUtilities.asList(minorTable.getSelectionModel().getSelectedObjects());
        masterTable.getSelectionModel().setSelectedObjects(objects);
      }
    });
    show(panel);
  }

  @Demo
  public void filterable() {
    final DemoObjectFactory factory = new DemoObjectFactory(4711);
    final DemoTableModel tableModel = new DemoTableModel(factory.createObjectList(20));
    final FilterableObjectTableModel<DemoObject> filterableObjectTableModel =
        new FilterableObjectTableModel<>(tableModel);
    final JPanel panel = new JPanel(new BorderLayout());
    final StringField stringField = new StringField();
    final IObjectModel<String> model = stringField.getModel();
    model.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        final String value = model.get();
        if (StringUtilities.isNullOrTrimmedEmpty(value)) {
          filterableObjectTableModel.setRowFilter(null);
          return;
        }
        filterableObjectTableModel.setRowFilter(new ContainsFilter(value, new ColumnToStingConverter(0, 1, 2, 3)));
      }
    });
    final ObjectTable<DemoObject> objectTable =
        new ObjectTable<>(createObjectTableConfiguration(true), filterableObjectTableModel);
    panel.add(stringField.getComponent(), BorderLayout.NORTH);
    panel.add(objectTable.getComponent(), BorderLayout.CENTER);
    show(panel);
  }
}