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
package net.anwiba.commons.swing.combobox;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.component.IComponentProvider;
import net.anwiba.commons.swing.list.IObjectListConfiguration;
import net.anwiba.commons.swing.list.ObjectListConfigurationBuilder;
import net.anwiba.commons.swing.ui.ObjectUiCellRenderer;

public class ObjectComboBoxComponent<T> implements IComponentProvider {

  private final JComponent component;
  private IObjectModel<T> objectModel;

  public ObjectComboBoxComponent(final IComboBoxModel<T> listModel) {
    this(new ObjectListConfigurationBuilder<T>().build(), listModel);
  }

  public ObjectComboBoxComponent(final IObjectListConfiguration<T> configuration, final IComboBoxModel<T> listModel) {
    this.objectModel = listModel;
    final JComboBox<T> list = new JComboBox<>(listModel);
    list.setRenderer(new ObjectUiCellRenderer<>(configuration.getObjectUiCellRendererConfiguration(), configuration
        .getObjectUi()));
    this.component = list;
  }

  @Override
  public JComponent getComponent() {
    return this.component;
  }

  public IObjectModel<T> getSelectionModel() {
    return this.objectModel;
  }

}
