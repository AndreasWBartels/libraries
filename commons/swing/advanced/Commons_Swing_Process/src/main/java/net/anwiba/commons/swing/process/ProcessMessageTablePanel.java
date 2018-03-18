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
package net.anwiba.commons.swing.process;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;

import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.ISelectionListener;
import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.model.SelectionEvent;
import net.anwiba.commons.swing.process.action.ProcessMessagePropertiesAction;
import net.anwiba.commons.swing.process.action.ProcessMessageRemoveAction;
import net.anwiba.commons.swing.process.action.ProcessMessageRemoveAllAction;
import net.anwiba.commons.swing.table.ColumnConfiguration;
import net.anwiba.commons.swing.table.IMouseListenerFactory;
import net.anwiba.commons.swing.table.IObjectTableConfiguration;
import net.anwiba.commons.swing.table.IObjectTableModel;
import net.anwiba.commons.swing.table.ISelectionIndexModel;
import net.anwiba.commons.swing.table.ObjectTable;
import net.anwiba.commons.swing.table.ObjectTableConfigurationBuilder;
import net.anwiba.commons.swing.utilities.ContainerUtilities;

public class ProcessMessageTablePanel extends JPanel {

  public static final class ActionHandlingTableModelListener implements TableModelListener {
    private final ProcessMessageContextTableModel processMessageContextTableModel;
    private final ProcessMessageRemoveAllAction removeAllAction;

    public ActionHandlingTableModelListener(
        final ProcessMessageContextTableModel processMessageContextTableModel,
        final ProcessMessageRemoveAllAction removeAllAction) {
      this.processMessageContextTableModel = processMessageContextTableModel;
      this.removeAllAction = removeAllAction;
    }

    @Override
    public void tableChanged(final TableModelEvent e) {
      this.removeAllAction.setEnabled(this.processMessageContextTableModel.getRowCount() > 0);
    }
  }

  public static final class SelectionListener implements ISelectionListener<IProcessMessageContext> {
    private final ISelectionModel<IProcessMessageContext> selectionModel;
    private final ProcessMessagePropertiesAction propertiesAction;
    private final ProcessMessageRemoveAction removeAction;

    public SelectionListener(
        final ISelectionModel<IProcessMessageContext> selectionModel,
        final ProcessMessagePropertiesAction propertiesAction,
        final ProcessMessageRemoveAction removeAction) {
      this.selectionModel = selectionModel;
      this.propertiesAction = propertiesAction;
      this.removeAction = removeAction;
    }

    @Override
    public void selectionChanged(final SelectionEvent<IProcessMessageContext> event) {
      this.removeAction.setEnabled(!this.selectionModel.isEmpty());
      this.propertiesAction.setEnabled(this.selectionModel.size() == 1);
    }
  }

  private static final long serialVersionUID = 1L;

  public ProcessMessageTablePanel(
      final Window owner,
      final ProcessMessageContextTableModel processMessageContextTableModel) {
    final TableCellRenderer cellRenderer = new ProcessMessageContextTableCellRenderer();
    final IObjectTableConfiguration<IProcessMessageContext> configuration = createTableConfiguration(cellRenderer);
    final ObjectTable<IProcessMessageContext> objectTable = new ObjectTable<>(
        configuration,
        processMessageContextTableModel);
    final JComponent tableComponent = objectTable.getComponent();
    tableComponent.setPreferredSize(new Dimension(200, 100));
    setLayout(new BorderLayout());
    final JToolBar toolBar = new JToolBar();
    final ISelectionModel<IProcessMessageContext> selectionModel = objectTable.getSelectionModel();
    final ProcessMessageRemoveAction removeAction = new ProcessMessageRemoveAction(
        processMessageContextTableModel,
        selectionModel);
    final ProcessMessageRemoveAllAction removeAllAction = new ProcessMessageRemoveAllAction(
        processMessageContextTableModel);
    final ProcessMessagePropertiesAction propertiesAction = new ProcessMessagePropertiesAction(owner, selectionModel);
    removeAction.setEnabled(false);
    removeAllAction.setEnabled(processMessageContextTableModel.getRowCount() > 0);
    propertiesAction.setEnabled(false);
    toolBar.add(removeAction);
    toolBar.add(removeAllAction);
    toolBar.add(propertiesAction);
    add(BorderLayout.NORTH, toolBar);
    add(BorderLayout.CENTER, tableComponent);
    selectionModel.addSelectionListener(new SelectionListener(selectionModel, propertiesAction, removeAction));
    processMessageContextTableModel
        .addTableModelListener(new ActionHandlingTableModelListener(processMessageContextTableModel, removeAllAction));
  }

  private IObjectTableConfiguration<IProcessMessageContext> createTableConfiguration(
      final TableCellRenderer cellRenderer) {
    final ObjectTableConfigurationBuilder<IProcessMessageContext> configurationBuilder = new ObjectTableConfigurationBuilder<>();
    configurationBuilder
        .addColumnConfiguration(new ColumnConfiguration(ProcessMessages.TYPE, cellRenderer, 20, true, null));
    configurationBuilder.addColumnConfiguration(new ColumnConfiguration("Date", cellRenderer, 100, true, null));
    configurationBuilder
        .addColumnConfiguration(new ColumnConfiguration(ProcessMessages.PROCESS, cellRenderer, 100, true, null));
    configurationBuilder
        .addColumnConfiguration(new ColumnConfiguration(ProcessMessages.MESSAGE, cellRenderer, 150, true, null));
    configurationBuilder.setMouseListenerFactory(new IMouseListenerFactory<IProcessMessageContext>() {

      @Override
      public MouseListener create(
          final IObjectTableModel<IProcessMessageContext> tableModel,
          final ISelectionIndexModel<IProcessMessageContext> selectionIndexModel,
          final ISelectionModel<IProcessMessageContext> selectionModel,
          final IBooleanDistributor sortStateModel) {
        return new MouseAdapter() {
          @Override
          public void mouseClicked(final MouseEvent event) {
            if (event.getClickCount() == 2) {
              if (!selectionModel.isEmpty()) {
                final ProcessMessageContextDialog dialog = new ProcessMessageContextDialog(
                    ContainerUtilities.getParentWindow(event.getComponent()),
                    selectionModel.getSelectedObjects().iterator().next());
                dialog.setVisible(true);
              }
            }
          }
        };
      }
    });
    return configurationBuilder.build();
  }
}