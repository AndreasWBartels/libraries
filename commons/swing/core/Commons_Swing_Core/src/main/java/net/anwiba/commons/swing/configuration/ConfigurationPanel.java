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
package net.anwiba.commons.swing.configuration;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import net.anwiba.commons.model.ISelectionListener;
import net.anwiba.commons.model.ISelectionModel;
import net.anwiba.commons.model.SelectionEvent;
import net.anwiba.commons.swing.list.ObjectListComponent;
import net.anwiba.commons.swing.list.ObjectListComponentModel;
import net.anwiba.commons.swing.list.ObjectListConfigurationBuilder;
import net.anwiba.commons.swing.parameter.ParameterTable;
import net.anwiba.commons.swing.parameter.ParameterTableModel;

public class ConfigurationPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  public ConfigurationPanel(final List<IConfiguration> configurations) {
    super(new BorderLayout());
    final ObjectListComponent<IConfiguration> configurationList = createConfigurationList(configurations);
    final JScrollPane configurationScrollPane = new JScrollPane(configurationList.getComponent());
    configurationScrollPane.setPreferredSize(new Dimension(150, 200));
    final ParameterTableModel parameterTableModel = new ParameterTableModel();
    final JScrollPane parameterScrollPane = new JScrollPane(new ParameterTable(parameterTableModel));
    parameterScrollPane.setPreferredSize(new Dimension(300, 200));
    final JSplitPane splitPane = new JSplitPane(
        JSplitPane.HORIZONTAL_SPLIT,
        configurationScrollPane,
        parameterScrollPane);
    splitPane.setOneTouchExpandable(true);
    splitPane.setResizeWeight(1);
    add(splitPane, BorderLayout.CENTER);
    final ISelectionModel<IConfiguration> selectionModel = configurationList.getSelectionModel();
    selectionModel.addSelectionListener(new ISelectionListener<IConfiguration>() {

      @Override
      public void selectionChanged(final SelectionEvent<IConfiguration> event) {
        updateParameters(selectionModel, parameterTableModel);
      }

    });
    updateParameters(selectionModel, parameterTableModel);
  }

  protected final void updateParameters(
      final ISelectionModel<IConfiguration> selectionModel,
      final ParameterTableModel parameterTableModel) {
    if (selectionModel.isEmpty()) {
      parameterTableModel.setParameters(null);
      return;
    }
    final IConfiguration configuration = selectionModel.getSelectedObjects().iterator().next();
    parameterTableModel.setParameters(configuration.getParameters());
  }

  private ObjectListComponent<IConfiguration> createConfigurationList(final List<IConfiguration> configurations) {
    final ObjectListConfigurationBuilder<IConfiguration> builder = new ObjectListConfigurationBuilder<>();
    builder.setVisibleRowCount(-1);
    builder.setSingleSelectionMode();
    builder.setObjectUi(new ConfigurationUi());
    final ObjectListComponent<IConfiguration> list = new ObjectListComponent<>(
        builder.build(),
        new ObjectListComponentModel<>(configurations));
    return list;
  }

}
