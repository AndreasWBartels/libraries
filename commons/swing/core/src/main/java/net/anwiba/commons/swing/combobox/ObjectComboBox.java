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

import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.component.IComponentProvider;
import net.anwiba.commons.swing.list.IObjectListConfiguration;
import net.anwiba.commons.swing.list.ObjectListConfigurationBuilder;
import net.anwiba.commons.swing.ui.ObjectUiListCellRenderer;

public class ObjectComboBox<T> implements IComponentProvider {

  private final JComboBox<T> component;
  private IObjectModel<T> objectModel;

  public ObjectComboBox(final IComboBoxModel<T> listModel) {
    this(new ObjectListConfigurationBuilder<T>().build(), listModel);
  }

  public ObjectComboBox(final IObjectListConfiguration<T> configuration, final IComboBoxModel<T> listModel) {
    this.objectModel = listModel;
    final JComboBox<T> list = new JComboBox<>(listModel);
    list
        .setRenderer(
            new ObjectUiListCellRenderer<>(
                configuration.getObjectUiCellRendererConfiguration(),
                configuration.getObjectUi()));
    list.setPrototypeDisplayValue(configuration.getPrototype());
    list.setMaximumRowCount(configuration.getVisibleRowCount());
    list.setSelectedItem(this.objectModel.get());
    IBooleanDistributor enabledDistributor = configuration.getEnabledDistributor();
    list.setEditable(configuration.isEditable());
    list.setEnabled(enabledDistributor.isTrue());
    enabledDistributor.addChangeListener(() ->  {
      list.setEnabled(enabledDistributor.isTrue());
    });
    this.component = list;
  }

  @Override
  public JComboBox<T> getComponent() {
    return this.component;
  }

  public IObjectModel<T> getSelectionModel() {
    return this.objectModel;
  }

}
