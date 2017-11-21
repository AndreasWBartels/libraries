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
package net.anwiba.commons.swing.preferences.editor;

import net.anwiba.commons.swing.parameter.ParameterTable;
import net.anwiba.commons.swing.parameter.ParameterTableModel;
import net.anwiba.commons.swing.preferences.tree.IPreferenceNode;
import net.anwiba.commons.swing.preferences.tree.PreferenceNode;
import net.anwiba.commons.utilities.parameter.IParameter;
import net.anwiba.commons.utilities.parameter.IParameters;
import net.anwiba.commons.utilities.parameter.Parameters;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class DefaultPreferenceNodeEditor implements IPreferenceNodeEditor {

  private final PreferenceNode node;
  final ParameterTableModel parameterTableModel = new ParameterTableModel(true);
  private final boolean isEditable;

  public DefaultPreferenceNodeEditor(final boolean isEditable, final PreferenceNode node) {
    this.isEditable = isEditable;
    this.node = node;
    this.parameterTableModel.setParameters(node == null ? new Parameters(new ArrayList<IParameter>()) : node
        .getParameters());
  }

  @Override
  public JComponent getComponent() {
    if (this.node == null || this.parameterTableModel.getRowCount() == 0) {
      return new JPanel();
    }
    final ParameterTable table = new ParameterTable(this.parameterTableModel);
    if (!this.isEditable) {
      table.setCellEditor(null);
    }
    final JScrollPane parameterScrollPane = new JScrollPane(table);
    parameterScrollPane.setPreferredSize(new Dimension(300, 200));
    return parameterScrollPane;
  }

  @Override
  public IPreferenceNode getPreferenceNode() {
    if (this.node == null) {
      return null;
    }
    final String[] path = this.node.getPath();
    final IParameters parameters = this.parameterTableModel.getParameters();
    return new IPreferenceNode() {

      @Override
      public String[] getPath() {
        return path;
      }

      @Override
      public IParameters getParameters() {
        return parameters;
      }
    };
  }
}
