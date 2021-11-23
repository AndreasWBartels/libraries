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

import javax.swing.Action;

import net.anwiba.commons.lang.functional.IConsumer;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.swing.dialog.MessageDialogLauncher;
import net.anwiba.commons.swing.icon.IGuiIcon;
import net.anwiba.commons.swing.utilities.GuiUtilities;

@SuppressWarnings("serial")
public class ConfigurableAction extends AbstractCustomizedAction {

  public static ConfigurableActionBuilder builder() {
    return new ConfigurableActionBuilder();
  }

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ConfigurableAction.class.getName());
  private final IActionProcedure closure;
  private final IBooleanModel internalEnabledModel = new BooleanModel(true);
  private final IConsumer<Boolean, RuntimeException> enabledConsumer;

  public ConfigurableAction(final IActionConfiguration configuration) {
    super(configuration.getCustomization());

    IConsumer<Boolean, RuntimeException> consumer = configuration.getEnabledConsumer();
    final IBooleanDistributor enabledDistributor = consumer == null
        ? configuration.getEnabledDistributor().and(this.internalEnabledModel)
        : configuration.getEnabledDistributor();
    this.enabledConsumer = consumer == null
        ? flag -> this.internalEnabledModel.set(flag.booleanValue())
        : consumer;

    enabledDistributor.addChangeListener(() -> {
      final boolean newValue = enabledDistributor.isTrue();
      ConfigurableAction.super.setEnabled(newValue);
    });
    super.setEnabled(enabledDistributor.isTrue());
    this.closure = configuration.getProcedure();
    final IObjectDistributor<IGuiIcon> iconDistributor = configuration.getIconDistributor();
    iconDistributor.addChangeListener(
        () -> GuiUtilities.invokeLater(
            () -> putValue(
                Action.SMALL_ICON,
                Optional.of(iconDistributor.get()).convert(i -> i.getSmallIcon()).get())));
    final IObjectDistributor<String> toolTipTextDistributor = configuration.getToolTipTextDistributor();
    toolTipTextDistributor.addChangeListener(
        () -> GuiUtilities.invokeLater(() -> putValue(Action.SHORT_DESCRIPTION, toolTipTextDistributor.get())));
    Optional.of(configuration.getPropertyChangeListener()).consume(l -> addPropertyChangeListener(l));
  }

  @Override
  public void setEnabled(final boolean newValue) {
    this.enabledConsumer.consume(Boolean.valueOf(newValue));
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
