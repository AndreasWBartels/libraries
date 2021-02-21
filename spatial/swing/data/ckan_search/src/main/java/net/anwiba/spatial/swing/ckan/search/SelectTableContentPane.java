/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.spatial.swing.ckan.search;

import java.util.List;

import javax.swing.JComponent;

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ISelectionListener;
import net.anwiba.commons.model.SelectionEvent;
import net.anwiba.commons.swing.dialog.DataState;
import net.anwiba.commons.swing.dialog.pane.AbstractContentPane;
import net.anwiba.commons.swing.table.IObjectTableModel;
import net.anwiba.commons.swing.table.ObjectListTable;
import net.anwiba.commons.swing.table.ObjectTableBuilder;
import net.anwiba.commons.swing.table.filter.IColumToStringConverter;
import net.anwiba.commons.utilities.collection.IterableUtilities;
import net.anwiba.spatial.swing.ckan.search.message.Messages;

public final class SelectTableContentPane<T> extends AbstractContentPane {
  private final List<T> selections;
  private final IObjectTableModel<T> tableModel;
  private final List<T> values;
  private final IConverter<T, String, RuntimeException> converter;

  public SelectTableContentPane(
    final IObjectModel<DataState> dataStateModel,
    final List<T> selections,
    final IObjectTableModel<T> tableModel,
    final List<T> values,
    final IConverter<T, String, RuntimeException> converter) {
    super(dataStateModel);
    this.selections = selections;
    this.tableModel = tableModel;
    this.values = values;
    this.converter = converter;
  }

  @Override
  public JComponent getComponent() {
    final ObjectListTable<T> table =
        new ObjectTableBuilder<T>().setFilterToStringConverter(new IColumToStringConverter() {

          @Override
          public int[] getFilterableColumnIndicies() {
            return new int[] { 0 };
          }

          @Override
          public String convert(final int index, final Object value) {
            return Optional.of(value).convert(o -> o.toString()).get();
          }
        })
            .setValues(this.values)
            .addSortableStringColumn(Messages.name, value -> this.converter.convert(value), 10)
            .build();
    table.getSelectionModel().setSelectedObjects(IterableUtilities.asList(this.tableModel.values()));
    table.getSelectionModel().addSelectionListener(new ISelectionListener<T>() {

      @Override
      public void selectionChanged(final SelectionEvent<T> event) {
        getDataStateModel().set(DataState.MODIFIED);
        if (event.getSource().isEmpty()) {
          SelectTableContentPane.this.selections.clear();
        }
        SelectTableContentPane.this.selections.clear();
        event.getSource().getSelectedObjects().forEach(s -> SelectTableContentPane.this.selections.add(s));
      }
    });
    return table.getComponent();
  }
}
