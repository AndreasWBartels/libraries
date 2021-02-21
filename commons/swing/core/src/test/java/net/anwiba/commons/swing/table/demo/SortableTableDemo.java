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

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.object.StringField;
import net.anwiba.commons.swing.table.FilterableObjectTableModel;
import net.anwiba.commons.swing.table.SortableTable;
import net.anwiba.commons.swing.table.filter.ColumnToStingConverter;
import net.anwiba.commons.swing.table.filter.ContainsFilter;
import net.anwiba.commons.utilities.string.StringUtilities;

public class SortableTableDemo {

  @Test
  public void sortable() {
    final DemoObjectFactory factory = new DemoObjectFactory(4711);
    show(new JScrollPane(new SortableTable(new DemoTableModel(factory.createObjectList(20)))));
  }

  @Test
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
    panel.add(stringField.getComponent(), BorderLayout.NORTH);
    panel.add(new JScrollPane(new SortableTable(filterableObjectTableModel)), BorderLayout.CENTER);
    show(panel);
  }
}