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
package net.anwiba.commons.swing.statebar;

import net.anwiba.commons.utilities.registry.KeyValueRegistry;

import java.io.Serializable;
import java.util.Comparator;

import javax.swing.BorderFactory;

public class StateBarManager implements IStateBarComponentRegistry {

  public static final class StateBarComponentDescriptionComparator implements
      Comparator<StateBarComponentDescription>,
      Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(final StateBarComponentDescription o1, final StateBarComponentDescription o2) {
      return Integer.valueOf(o1.getWeight()).compareTo(Integer.valueOf(o2.getWeight()));
    }
  }

  final private KeyValueRegistry<StateBarComponentDescription, StateBarComponentConfiguration> registry = new KeyValueRegistry<>();
  final Comparator<StateBarComponentDescription> comparator = new StateBarComponentDescriptionComparator();

  private void addComponent(final StateBarComponentConfiguration stateBarComponentConfiguration) {
    this.registry.register(
        stateBarComponentConfiguration.getStateBarComponentDescription(),
        stateBarComponentConfiguration);
  }

  @Override
  public void add(final StateBarComponentConfiguration... stateBarComponentConfigurations) {
    for (final StateBarComponentConfiguration stateBarComponentConfiguration : stateBarComponentConfigurations) {
      addComponent(stateBarComponentConfiguration);
    }
  }

  public StateBar getStateBar() {
    final StateBarComponentConfiguration[] stateBarComponentConfigurations =
        this.registry.getItems(
            this.comparator,
            StateBarComponentDescription.class,
            StateBarComponentConfiguration.class);
    final StateBar stateBar = new StateBar();
    stateBar.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    for (final StateBarComponentConfiguration stateBarComponentConfiguration : stateBarComponentConfigurations) {
      stateBar.add(
          stateBarComponentConfiguration.getStateBarComponentDescription().getSide(),
          stateBarComponentConfiguration.getStateBarComponent().getComponent());
    }
    return stateBar;
  }
}
