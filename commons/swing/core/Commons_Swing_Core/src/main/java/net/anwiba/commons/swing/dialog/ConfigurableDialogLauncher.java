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

package net.anwiba.commons.swing.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;

import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.icon.GuiIcon;
import net.anwiba.commons.swing.utilities.GuiUtilities;

public class ConfigurableDialogLauncher {

  private final DialogConfigurationBuilder dialogConfigurationBuilder = new DialogConfigurationBuilder();

  public ConfigurableDialogLauncher setIcon(final GuiIcon icon) {
    this.dialogConfigurationBuilder.setIcon(icon);
    return this;
  }

  public ConfigurableDialogLauncher setCloseButtonDialog() {
    this.dialogConfigurationBuilder.setDialogType(DialogType.CLOSE);
    return this;
  }

  public ConfigurableDialogLauncher setCancleOkButtonDialog() {
    this.dialogConfigurationBuilder.setDialogType(DialogType.CANCEL_OK);
    return this;
  }

  public ConfigurableDialogLauncher setDialogType(final DialogType dialogType) {
    this.dialogConfigurationBuilder.setDialogType(dialogType);
    return this;
  }

  public ConfigurableDialogLauncher setActionButtonTextFactory(final IFunction<String, String, RuntimeException> factory) {
    this.dialogConfigurationBuilder.setActionButtonTextFactory(factory);
    return this;
  }

  public ConfigurableDialogLauncher setTitle(final String title) {
    this.dialogConfigurationBuilder.setTitle(title);
    return this;
  }

  public ConfigurableDialogLauncher setContentPaneFactory(final IContentPaneFactory contentPaneFactory) {
    this.dialogConfigurationBuilder.setContentPaneFactory(contentPaneFactory);
    return this;
  }

  public ConfigurableDialogLauncher setPreferences(final IPreferences preferences) {
    this.dialogConfigurationBuilder.setPreferences(preferences);
    return this;
  }

  public ConfigurableDialogLauncher enableCloseOnEscape() {
    this.dialogConfigurationBuilder.setDialogCloseKeyEvent(KeyEvent.VK_ESCAPE);
    return this;
  }

  public ConfigurableDialogLauncher setResizeable() {
    this.dialogConfigurationBuilder.setResizeable(true);
    return this;
  }

  public ConfigurableDialogLauncher setUnresizeable() {
    this.dialogConfigurationBuilder.setResizeable(false);
    return this;
  }

  public ConfigurableDialogLauncher addAdditionalAction(final IAdditionalActionFactory factory) {
    this.dialogConfigurationBuilder.addAdditionalAction(factory);
    return this;
  }

  public ConfigurableDialogLauncher setMessage(final IMessage message) {
    this.dialogConfigurationBuilder.setMessage(message);
    this.dialogConfigurationBuilder.setMessagePanelEnabled(message != null);
    return this;
  }

  public ConfigurableDialogLauncher setOkButtonText(final String string) {
    this.dialogConfigurationBuilder.setActionButtonTextFactory(s -> {
      return s.equals(DialogMessages.OK) ? string : s;
    });
    return this;
  }

  public ConfigurableDialogLauncher setPreferdSize(final int width, final int height) {
    this.dialogConfigurationBuilder.setPreferdSize(new Dimension(width, height));
    return this;
  }

  public IDialogResult launch(final Component component) {
    return launch(component == null ? (Window) null : SwingUtilities.windowForComponent(component));
  }

  public IDialogResult launch(final Window owner) {
    final IObjectModel<IDialogResult> model = new ObjectModel<>();
    GuiUtilities.invokeAndWait(() -> {
      final IDialogConfiguration configuration = this.dialogConfigurationBuilder.build();
      final ConfigurableDialog dialog = new ConfigurableDialog(owner, configuration);
      dialog.setVisible(true);
      dialog.toFront();
      model.set(dialog.getResult());
    });
    return model.get();
  }
}