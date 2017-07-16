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
import java.awt.event.ActionEvent;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.swing.dialog.MessageDialogLauncher;

@SuppressWarnings("serial")
public class ConfigurableAction extends AbstractCustomizedAction {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ConfigurableAction.class.getName());
  private final IActionProcedure closure;
  private final IActionConfiguration configuration;

  public ConfigurableAction(final IActionConfiguration configuration) {
    super(configuration.getCustomization());
    this.configuration = configuration;
    configuration.getEnabledModel().addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        ConfigurableAction.super.setEnabled(configuration.getEnabledModel().get());
      }
    });
    super.setEnabled(configuration.getEnabledModel().get());
    this.closure = configuration.getProcedure();
  }

  @Override
  public void setEnabled(final boolean newValue) {
    this.configuration.getEnabledModel().set(newValue);
  }

  @Override
  protected void execute(final Component componment, final ActionEvent event) {
    try {
      this.closure.execute(componment);
    } catch (final Exception exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      new MessageDialogLauncher().text(exception.getMessage()).error().throwable(exception).launch(componment);
    }
  }

}
