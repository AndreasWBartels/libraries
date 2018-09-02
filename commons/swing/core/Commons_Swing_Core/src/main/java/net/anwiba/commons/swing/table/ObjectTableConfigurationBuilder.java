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
package net.anwiba.commons.swing.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListSelectionModel;

import net.anwiba.commons.swing.table.action.ITableActionConfiguration;
import net.anwiba.commons.swing.table.action.ITableActionFactory;

public class ObjectTableConfigurationBuilder<T> {

  private int selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
  private final List<IColumnConfiguration> columnConfigurations = new ArrayList<>();
  private final List<ITableActionFactory<T>> actionFactories = new ArrayList<>();
  private int preferredVisibleRowCount = 10;
  private IMouseListenerFactory<T> mouseListenerFactory;
  private IKeyListenerFactory<T> keyListenerFactory;

  public void setKeyListenerFactory(final IKeyListenerFactory<T> keyListenerFactory) {
    this.keyListenerFactory = keyListenerFactory;
  }

  public void setSelectionMode(final int selectionMode) {
    this.selectionMode = selectionMode;
  }

  public void addColumnConfiguration(final IColumnConfiguration columnConfiguration) {
    this.columnConfigurations.add(columnConfiguration);
  }

  public void addActionFactory(final ITableActionFactory<T> factory) {
    this.actionFactories.add(factory);
  }

  public void setPreferredVisibleRowCount(final int preferredVisibleRowCount) {
    this.preferredVisibleRowCount = preferredVisibleRowCount;
  }

  public IObjectTableConfiguration<T> build() {
    final ITableActionConfiguration<T> actionConfiguration = new TableActionConfiguration<>(this.actionFactories);
    return new ObjectTableConfiguration<>(
        -1,
        this.selectionMode,
        this.preferredVisibleRowCount,
        null,
        this.columnConfigurations,
        this.mouseListenerFactory,
        this.keyListenerFactory,
        actionConfiguration);
  }

  public void setMouseListenerFactory(final IMouseListenerFactory<T> mouseListenerFactory) {
    this.mouseListenerFactory = mouseListenerFactory;
  }
}
