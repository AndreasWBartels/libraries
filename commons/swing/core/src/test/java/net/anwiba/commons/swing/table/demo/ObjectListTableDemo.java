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

import static net.anwiba.testing.demo.JFrames.show;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.model.ISelectionListener;
import net.anwiba.commons.model.SelectionEvent;
import net.anwiba.commons.swing.table.IColumnObjectFactory;
import net.anwiba.commons.swing.table.IColumnValueAdaptor;
import net.anwiba.commons.swing.table.IColumnValueProvider;
import net.anwiba.commons.swing.table.IObjectListTableConfiguration;
import net.anwiba.commons.swing.table.IObjectTableBuilder;
import net.anwiba.commons.swing.table.ObjectListColumnConfiguration;
import net.anwiba.commons.swing.table.ObjectListTable;
import net.anwiba.commons.swing.table.ObjectListTableConfigurationBuilder;
import net.anwiba.commons.swing.table.ObjectTableBuilder;
import net.anwiba.commons.swing.table.filter.ColumnToStingConverter;
import net.anwiba.commons.swing.table.renderer.BooleanRenderer;
import net.anwiba.commons.swing.table.renderer.NumberTableCellRenderer;
import net.anwiba.commons.swing.table.renderer.ObjectTableCellRenderer;
import net.anwiba.commons.utilities.collection.IterableUtilities;

public class ObjectListTableDemo {

  final DemoObjectFactory factory = new DemoObjectFactory(4711);

  final IColumnObjectFactory<DemoObject, DemoObject, RuntimeException> columnObjectFactory =
      new IColumnObjectFactory<DemoObject, DemoObject, RuntimeException>() {

        @Override
        public DemoObject create(final Component parent, final DemoObject value) throws RuntimeException {
          return ObjectListTableDemo.this.factory.createObject();
        }
      };

  public IObjectListTableConfiguration<DemoObject> createObjectTableConfiguration(final boolean isSortable) {
    final ObjectListTableConfigurationBuilder<DemoObject> builder = new ObjectListTableConfigurationBuilder<>();
    addColumnDescriptions(builder, isSortable);
    return builder.build();
  }

  public void addColumnDescriptions(
      final ObjectListTableConfigurationBuilder<DemoObject> builder,
      final boolean isSortable) {
    builder.addColumnConfiguration(createNumberColumn(isSortable));
    builder.addColumnConfiguration(createNameColumn(isSortable));
    builder.addColumnConfiguration(createValueColumn(isSortable));
    builder.addColumnConfiguration(createFlagColumn(isSortable));
  }

  public ObjectListColumnConfiguration<DemoObject> createFlagColumn(final boolean isSortable) {
    return new ObjectListColumnConfiguration<>("Flag", new IColumnValueProvider<DemoObject>() {

      @Override
      public Object getValue(final DemoObject object) {
        if (object == null) {
          return null;
        }
        return object.getFlag();
      }
    }, new BooleanRenderer(), new IColumnValueAdaptor<DemoObject>() {

      @Override
      public DemoObject adapt(final DemoObject object, final Object value) {
        if (object == null) {
          return new DemoObject(null, null, null, (Boolean) value);
        }
        return new DemoObject(object.getNummer(), object.getName(), object.getValue(), (Boolean) value);
      }
    }, new DefaultCellEditor(new JCheckBox()), 40, isSortable, null);
  }

  public ObjectListColumnConfiguration<DemoObject> createValueColumn(final boolean isSortable) {
    return new ObjectListColumnConfiguration<>("Value", new IColumnValueProvider<DemoObject>() {

      @Override
      public Object getValue(final DemoObject object) {
        if (object == null) {
          return null;
        }
        return object.getValue();
      }
    }, new NumberTableCellRenderer(), 200, isSortable, null);
  }

  public ObjectListColumnConfiguration<DemoObject> createNameColumn(final boolean isSortable) {
    return new ObjectListColumnConfiguration<>("Name", new IColumnValueProvider<DemoObject>() {

      @Override
      public Object getValue(final DemoObject object) {
        if (object == null) {
          return null;
        }
        return object.getName();
      }
    }, new ObjectTableCellRenderer(), new IColumnValueAdaptor<DemoObject>() {

      @Override
      public DemoObject adapt(final DemoObject object, final Object value) {
        if (object == null) {
          return new DemoObject(null, (String) value, null, null);
        }
        return new DemoObject(object.getNummer(), (String) value, object.getValue(), object.getFlag());
      }
    }, new DefaultCellEditor(new JTextField()), 200, isSortable, null);
  }

  public ObjectListColumnConfiguration<DemoObject> createNumberColumn(final boolean isSortable) {
    return new ObjectListColumnConfiguration<>("Nummer", new IColumnValueProvider<DemoObject>() {

      @Override
      public Object getValue(final DemoObject object) {
        if (object == null) {
          return null;
        }
        return object.getNummer();
      }
    }, new NumberTableCellRenderer(), 80, isSortable, null);
  }

  @Test
  public void actions() {
    final ObjectListTableConfigurationBuilder<DemoObject> builder = new ObjectListTableConfigurationBuilder<>();
    addColumnDescriptions(builder, true);
    builder
        .addAddObjectAction(this.columnObjectFactory)
        .addEditObjectAction(this.columnObjectFactory)
        .addRemoveObjectsAction()
        .addMoveObjectUpAction()
        .addMoveObjectDownAction();
    show(new ObjectListTable<>(builder.build(), this.factory.createObjectList(20)).getComponent());
  }

  @Test
  public void sortable() {
    final ObjectListTableConfigurationBuilder<DemoObject> builder = new ObjectListTableConfigurationBuilder<>();
    addColumnDescriptions(builder, true);
    show(new ObjectListTable<>(builder.build(), this.factory.createObjectList(20)).getComponent());
  }

  @Test
  public void selection() {
    final JPanel panel = new JPanel(new GridLayout(2, 1));
    final List<DemoObject> objectList = this.factory.createObjectList(20);
    final ObjectListTable<DemoObject> masterTable = new ObjectListTable<>(
        createObjectTableConfiguration(true),
        objectList);
    final ObjectListTable<DemoObject> minorTable = new ObjectListTable<>(
        createObjectTableConfiguration(true),
        objectList);
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

  @Test
  public void filterable() {
    final IObjectTableBuilder<DemoObject> builder = new ObjectTableBuilder<>();
    builder.setValues(this.factory.createObjectList(20));
    final boolean isSortable = true;
    builder.addColumn(createNumberColumn(isSortable));
    builder.addColumn(createNameColumn(isSortable));
    builder.addColumn(createValueColumn(isSortable));
    builder.addColumn(createFlagColumn(isSortable));
    builder
        .addAddObjectAction(this.columnObjectFactory)
        .addEditObjectAction(this.columnObjectFactory)
        .addRemoveObjectsAction()
        .addMoveObjectUpAction()
        .addMoveObjectDownAction()
        .setFilterToStringConverter(new ColumnToStingConverter(0, 1, 2, 3));
    show(builder.build().getComponent());
  }
}