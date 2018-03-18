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

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.model.IChangeableListListener;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.thread.process.IProcessManager;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class ProcessListPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  private final JPanel listPanel = new JPanel();
  private final ProcessContextModelListModel model;

  private final IProcessManager manager;

  public ProcessListPanel(final IProcessManager manager, final ProcessContextModelListModel model) {
    this.manager = manager;
    Ensure.ensureArgumentNotNull(model);
    setLayout(new BorderLayout());
    add(BorderLayout.NORTH, this.listPanel);
    this.listPanel.setLayout(new BoxLayout(this.listPanel, BoxLayout.PAGE_AXIS));
    this.model = model;
    model.addListModelListener(new IChangeableListListener<ProcessContextModel>() {

      @Override
      public void objectsRemoved(final Iterable<Integer> indeces, final Iterable<ProcessContextModel> models) {
        updateView();
      }

      @Override
      public void objectsAdded(final Iterable<Integer> indeces, final Iterable<ProcessContextModel> models) {
        updateView();
      }

      @Override
      public void objectsUpdated(
          final Iterable<Integer> indeces,
          final Iterable<ProcessContextModel> oldObjects,
          final Iterable<ProcessContextModel> newObjects) {
        updateView();
      }

      @Override
      public void objectsChanged(
          final Iterable<ProcessContextModel> oldObjects,
          final Iterable<ProcessContextModel> newObjects) {
        updateView();
      }

    });
    updateView();
  }

  protected void updateView() {
    this.listPanel.removeAll();
    for (final ProcessContextModel process : this.model.getProcessModels()) {
      this.listPanel.add(new ProcessItemPanel(this.manager, process));
    }
    GuiUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        if (!isVisible()) {
          return;
        }
        revalidate();
        repaint();
      }
    });
  }
}
