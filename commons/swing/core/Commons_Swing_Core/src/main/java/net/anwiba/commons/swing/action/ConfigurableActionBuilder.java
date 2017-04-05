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
package net.anwiba.commons.swing.action;

import javax.swing.AbstractAction;

import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.swing.icon.IGuiIcon;
import net.anwiba.commons.utilities.factory.IFactory;

public class ConfigurableActionBuilder {

  ActionConfigurationBuilder configurationBuilder = new ActionConfigurationBuilder();

  public ConfigurableActionBuilder setName(final String name) {
    this.configurationBuilder.setName(name);
    return this;
  }

  public ConfigurableActionBuilder setSelectedIcon(final IGuiIcon selectedIcon) {
    this.configurationBuilder.setSelectedIcon(selectedIcon);
    return this;
  }

  public ConfigurableActionBuilder setIcon(final IGuiIcon icon) {
    this.configurationBuilder.setIcon(icon);
    return this;
  }

  public ConfigurableActionBuilder setTooltip(final String tooltip) {
    this.configurationBuilder.setTooltip(tooltip);
    return this;
  }

  public ConfigurableActionBuilder setEnabledModel(final IBooleanModel enabledModel) {
    this.configurationBuilder.setEnabledModel(enabledModel);
    return this;
  }

  public ConfigurableActionBuilder setProcedure(final IActionProcedure procedure) {
    this.configurationBuilder.setProcedure(procedure);
    return this;
  }

  public ConfigurableActionBuilder setText(final String string) {
    this.configurationBuilder.setName(string);
    return this;
  }

  public <I, O> ConfigurableActionBuilder createProcedure(
      final IFactory<IActionProcedurBuilder<I, O>, IActionProcedure, RuntimeException> factory) {
    this.configurationBuilder.setProcedure(factory.create(new ActionProcedurBuilder<I, O>()));
    return this;
  }

  public AbstractAction build() {
    return new ConfigurableAction(this.configurationBuilder.build());
  }

}
