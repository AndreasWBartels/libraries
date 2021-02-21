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
package net.anwiba.commons.swing.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.model.ISelectionListener;
import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.model.SelectionEvent;
import net.anwiba.commons.swing.component.IComponentProvider;
import net.anwiba.commons.swing.ui.ObjectUiListCellRenderer;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.utilities.collection.IterableUtilities;

public class ObjectListComponent<T> implements IComponentProvider {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ObjectListComponent.class);
  private final JComponent component;
  private final ISelectionModel<T> selectionModel;
  private JList<T> list;
  private IObjectListConfiguration<T> configuration;

  public static final class JListSelectionListener<T> implements ListSelectionListener {
    private final IListModel<T> listModel;
    private final ListSelectionModel listSelectionModel;
    private final ISelectionModel<T> objectSelectionModel;

    public JListSelectionListener(
        final IListModel<T> listModel,
        final ListSelectionModel listSelectionModel,
        final ISelectionModel<T> objectSelectionModel) {
      this.listModel = listModel;
      this.listSelectionModel = listSelectionModel;
      this.objectSelectionModel = objectSelectionModel;
    }

    @Override
    public void valueChanged(final ListSelectionEvent event) {
      if (event.getValueIsAdjusting()) {
        return;
      }
      if (this.listSelectionModel.isSelectionEmpty()) {
        this.objectSelectionModel.removeAllSelectedObjects();
        return;
      }
      final List<T> objects = new ArrayList<>();
      for (int i = this.listSelectionModel.getMinSelectionIndex(); i <= this.listSelectionModel
          .getMaxSelectionIndex(); i++) {
        if (this.listSelectionModel.isSelectedIndex(i)) {
          objects.add(this.listModel.getObject(i));
        }
      }
      this.objectSelectionModel.setSelectedObjects(objects);
    }
  }

  public static final class SelectionListener<T> implements ISelectionListener<T> {
    private final IListModel<T> listModel;
    private final ListSelectionModel tableSelectionModel;
    private final ISelectionModel<T> objectSelectionModel;

    public SelectionListener(
        final IListModel<T> listModel,
        final ListSelectionModel tableSelectionModel,
        final ISelectionModel<T> objectSelectionModel) {
      this.listModel = listModel;
      this.tableSelectionModel = tableSelectionModel;
      this.objectSelectionModel = objectSelectionModel;
    }

    @Override
    public void selectionChanged(final SelectionEvent<T> event) {
      if (this.objectSelectionModel.isEmpty() && this.tableSelectionModel.isSelectionEmpty()) {
        return;
      }
      if (this.objectSelectionModel.isEmpty()) {
        this.tableSelectionModel.clearSelection();
        return;
      }
      final List<T> objects = getObjects(this.listModel, this.tableSelectionModel);
      final List<T> selectedObjects = IterableUtilities.asList(this.objectSelectionModel.getSelectedObjects());
      if (objects.size() == this.objectSelectionModel.size() && objects.containsAll(selectedObjects)) {
        return;
      }

      GuiUtilities.invokeLater(() -> {
        this.tableSelectionModel.setValueIsAdjusting(true);
        this.tableSelectionModel.clearSelection();
        final int[] indexes = this.listModel.getIndicesOf(selectedObjects);
        for (final int index : indexes) {
          try {
            this.tableSelectionModel.addSelectionInterval(index, index);
          } catch (final NullPointerException exception) {
            logger.log(ILevel.ERROR, exception.getMessage(), exception);
          }
        }
        this.tableSelectionModel.setValueIsAdjusting(false);
      });
    }

    private List<T> getObjects(
        final IListModel<T> tableModel,
        @SuppressWarnings("hiding") final ListSelectionModel tableSelectionModel) {
      final List<T> objects = new ArrayList<>();
      if (tableSelectionModel.isSelectionEmpty()) {
        return objects;
      }
      for (int i = tableSelectionModel.getMinSelectionIndex(); i <= tableSelectionModel.getMaxSelectionIndex(); i++) {
        if (tableSelectionModel.isSelectedIndex(i)) {
          objects.add(tableModel.getObject(i));
        }
      }
      return objects;
    }
  }

  public ObjectListComponent(final IListModel<T> listModel) {
    this(new ObjectListConfigurationBuilder<T>().build(), listModel);
  }

  public ObjectListComponent(final IObjectListConfiguration<T> configuration, final IListModel<T> listModel) {
    this.configuration = configuration;
    this.list = new JList<>(listModel);
    this.list.setVisibleRowCount(configuration.getVisibleRowCount());
    this.list.setSelectionMode(configuration.getSelectionMode());
    this.list.setLayoutOrientation(configuration.getLayoutOrientation());
    this.list.setCellRenderer(
        new ObjectUiListCellRenderer<>(
            configuration.getObjectUiCellRendererConfiguration(),
            configuration.getObjectUi()));
    final ListSelectionModel tableSelectionModel = this.list.getSelectionModel();
    this.selectionModel = configuration.getSelectionModel();
    tableSelectionModel
        .addListSelectionListener(new JListSelectionListener<>(listModel, tableSelectionModel, this.selectionModel));
    this.selectionModel
        .addSelectionListener(new SelectionListener<>(listModel, tableSelectionModel, this.selectionModel));
    this.list.setTransferHandler(configuration.getTransferHandler());
    this.list.setDropMode(configuration.getDropMode());
    this.list.setDragEnabled(configuration.isDragEnabled());

    Optional.ofNullable(configuration.getMouseListener()).ifPresent(l -> this.list.addMouseListener(l));
    this.component = new JScrollPane(this.list);
  }

  public void scrollToSelectedObject() {
    final int selectedIndex = this.list.getSelectedIndex();
    if (selectedIndex != -1) {
      GuiUtilities.invokeLater(() -> {
        this.list.ensureIndexIsVisible(selectedIndex);
      });
    }
  }

  public void setVerticalLayout() {
    GuiUtilities.invokeLater(() -> {
      this.list.setCellRenderer(
          new ObjectUiListCellRenderer<>(
              new ObjectUiCellRendererConfigurationBuilder().build(),
              this.configuration.getObjectUi()));
      this.list.setLayoutOrientation(JList.VERTICAL);
    });
  }

  public void setHorizontalWrapLayout() {
    GuiUtilities.invokeLater(() -> {
      this.list.setCellRenderer(
          new ObjectUiListCellRenderer<>(
              this.configuration.getObjectUiCellRendererConfiguration(),
              this.configuration.getObjectUi()));
      this.list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    });
  }

  @Override
  public JComponent getComponent() {
    return this.component;
  }

  public ISelectionModel<T> getSelectionModel() {
    return this.selectionModel;
  }

}
