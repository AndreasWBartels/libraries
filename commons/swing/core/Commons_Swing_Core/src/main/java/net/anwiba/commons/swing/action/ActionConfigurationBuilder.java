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

import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.message.IMessageCollector;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.icon.IGuiIcon;
import net.anwiba.commons.thread.cancel.ICanceler;

public class ActionConfigurationBuilder {

  private IBooleanDistributor enabledDistributor = new BooleanModel(true);
  private String tooltip = null;
  private IGuiIcon icon = null;
  private String name = null;
  private IActionProcedure procedure = null;
  private IBlock<InvocationTargetException> task;
  private IObjectModel<IGuiIcon> iconModel = new ObjectModel<>();
  private IObjectModel<String> toolTipModel = new ObjectModel<>();

  public ActionConfigurationBuilder setName(final String name) {
    this.name = name;
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

  public ActionConfigurationBuilder setEnabledDistributor(final IBooleanDistributor enabledModel) {
    this.enabledDistributor = enabledModel;
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
    final IBooleanDistributor enabledDistributor = this.enabledDistributor;
    final IActionCustomization customization = new ActionCustomization(this.name, this.icon, this.tooltip);
    final IActionProcedure procedure = createProcedure();
    if (this.iconModel.get() == null) {
      this.iconModel.set(this.icon);
    }
    if (this.toolTipModel.get() == null) {
      this.toolTipModel.set(this.tooltip);
    }
    return new IActionConfiguration() {

      @Override
      public IBooleanDistributor getEnabledDistributor() {
        return enabledDistributor;
      }

      @Override
      public IActionCustomization getCustomization() {
        return customization;
      }

      @Override
      public IObjectDistributor<IGuiIcon> getIconDistributor() {
        return ActionConfigurationBuilder.this.iconModel;
      }

      @Override
      public IObjectDistributor<String> getToolTipTextDistributor() {
        return ActionConfigurationBuilder.this.toolTipModel;
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
            CanceledException {
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

  public void setIconModel(final IObjectModel<IGuiIcon> iconModel) {
    this.iconModel = iconModel;
  }

  public void setToolTipModel(final IObjectModel<String> toolTipModel) {
    this.toolTipModel = toolTipModel;
  }

}
