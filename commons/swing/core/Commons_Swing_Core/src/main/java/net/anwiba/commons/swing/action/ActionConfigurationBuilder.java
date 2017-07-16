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

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;

import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.message.IMessageCollector;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.process.cancel.ICanceler;
import net.anwiba.commons.swing.icon.IGuiIcon;

public class ActionConfigurationBuilder {

  private IBooleanModel enabledModel = new BooleanModel(true);
  private String tooltip = null;
  private IGuiIcon icon = null;
  private String name = null;
  private IActionProcedure procedure = null;
  private IGuiIcon selectedIcon = null;
  private IBlock<InvocationTargetException> task;

  public ActionConfigurationBuilder setName(final String name) {
    this.name = name;
    return this;
  }

  public ActionConfigurationBuilder setSelectedIcon(final IGuiIcon selectedIcon) {
    this.selectedIcon = selectedIcon;
    return this;
  }

  public ActionConfigurationBuilder setIcon(final IGuiIcon icon) {
    this.icon = icon;
    return this;
  }

  public ActionConfigurationBuilder setTooltip(final String tooltip) {
    this.tooltip = tooltip;
    return this;
  }

  public ActionConfigurationBuilder setEnabledModel(final IBooleanModel enabledModel) {
    this.enabledModel = enabledModel;
    return this;
  }

  public ActionConfigurationBuilder setProcedure(final IActionProcedure closure) {
    if (this.procedure != null) {
      this.task = null;
    }
    this.procedure = closure;
    return this;
  }

  public ActionConfigurationBuilder setTask(final IBlock<InvocationTargetException> task) {
    if (task != null) {
      this.procedure = null;
    }
    this.task = task;
    return this;
  }

  @SuppressWarnings("hiding")
  public IActionConfiguration build() {
    final IBooleanModel enabledModel = this.enabledModel;
    final IActionCustomization customization = new ActionCustomization(this.name, this.icon, this.tooltip);
    final IActionProcedure procedure = createProcedure();
    return new IActionConfiguration() {

      @Override
      public IBooleanModel getEnabledModel() {
        return enabledModel;
      }

      @Override
      public IActionCustomization getCustomization() {
        return customization;
      }

      @Override
      public IActionProcedure getProcedure() {
        return procedure;
      }
    };
  }

  private IActionProcedure createProcedure() {
    if (this.procedure != null) {
      return this.procedure;
    }
    if (this.task != null) {
      return new ActionProcedurBuilder<Void, Void>().setTitle(this.name).setTask(new IActionTask<Void, Void>() {

        @Override
        public Void excecute(final IMessageCollector monitor, final ICanceler canceler, final Void value)
            throws InvocationTargetException,
            InterruptedException {
          ActionConfigurationBuilder.this.task.execute();
          return null;
        }
      }).build();
    }
    return new IActionProcedure() {

      @Override
      public void execute(final Component value) throws RuntimeException {
        // nothing to do
      }
    };
  }

}
