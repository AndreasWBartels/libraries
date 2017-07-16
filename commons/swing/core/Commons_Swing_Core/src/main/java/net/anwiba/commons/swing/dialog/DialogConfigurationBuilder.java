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

import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.preferences.DummyPreferences;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.dialog.pane.AbstractContentPane;
import net.anwiba.commons.swing.dialog.pane.IContentPaneBuilder;
import net.anwiba.commons.swing.dialog.pane.IContentPanel;
import net.anwiba.commons.swing.icon.IGuiIcon;

public class DialogConfigurationBuilder {

  private final List<IAdditionalActionFactory> additionalActionFactories = new ArrayList<>();
  private IFunction<String, String, RuntimeException> actionButtonTextFactory = s -> s;
  private IPreferences preferences = new DummyPreferences();
  private DialogType dialogType = DialogType.NONE;
  private IContentPaneFactory contentPaneFactory = new IContentPaneFactory() {

    @Override
    public IContentPanel create(final Window owner, final IPreferences preferences) {
      return new AbstractContentPane() {

        @Override
        public JComponent getComponent() {
          final JPanel panel = new JPanel();
          panel.setMinimumSize(new Dimension(200, 100));
          panel.setPreferredSize(new Dimension(200, 100));
          return panel;
        }
      };
    }
  };
  private String title = "Dialog";
  private IMessage message = null;
  private IGuiIcon icon = null;
  private ModalityType modality = ModalityType.APPLICATION_MODAL;
  private boolean isMessagePanelEnabled = false;
  private boolean isResizeable = true;
  private int dialogCloseKeyEvent = KeyEvent.KEY_LOCATION_UNKNOWN;
  private Dimension preferdSize;
  private IGuiIcon image;

  public DialogConfigurationBuilder setMessage(final IMessage message) {
    this.message = message;
    return this;
  }

  public void setActionButtonTextFactory(final IFunction<String, String, RuntimeException> actionButtonTextFactory) {
    this.actionButtonTextFactory = actionButtonTextFactory;
  }

  public DialogConfigurationBuilder setContentPaneFactory(final IContentPaneFactory contentPaneFactory) {
    this.contentPaneFactory = contentPaneFactory;
    return this;
  }

  public DialogConfigurationBuilder setResizeable(final boolean isResizeable) {
    this.isResizeable = isResizeable;
    return this;
  }

  public DialogConfigurationBuilder setIcon(final IGuiIcon icon) {
    this.icon = icon;
    return this;
  }

  public DialogConfigurationBuilder setDialogIcon(final IGuiIcon image) {
    this.image = image;
    return this;
  }

  public DialogConfigurationBuilder setMessagePanelEnabled(final boolean isMessagePanelEnabled) {
    this.isMessagePanelEnabled = isMessagePanelEnabled;
    return this;
  }

  public DialogConfigurationBuilder setModality(final ModalityType modality) {
    this.modality = modality;
    return this;
  }

  public DialogConfigurationBuilder setTitle(final String title) {
    this.title = title;
    return this;
  }

  public DialogConfigurationBuilder setDialogType(final DialogType dialogType) {
    this.dialogType = dialogType;
    return this;
  }

  public DialogConfigurationBuilder setPreferences(final IPreferences preferences) {
    this.preferences = preferences;
    return this;
  }

  public IDialogConfiguration build() {
    final IContentPaneBuilder contentPaneBuilder = new ContentPaneBuilder(this.contentPaneFactory);
    contentPaneBuilder.setPreferences(this.preferences);
    return new DialogConfiguration(
        this.preferences,
        this.preferdSize,
        this.isMessagePanelEnabled,
        this.title,
        this.message,
        this.icon,
        this.image,
        this.modality,
        this.dialogType,
        this.actionButtonTextFactory,
        this.isResizeable,
        this.dialogCloseKeyEvent,
        this.additionalActionFactories,
        contentPaneBuilder);
  }

  public DialogConfigurationBuilder setDialogCloseKeyEvent(final int dialogCloseKeyEvent) {
    this.dialogCloseKeyEvent = dialogCloseKeyEvent;
    return this;
  }

  public DialogConfigurationBuilder addAdditionalAction(final IAdditionalActionFactory factory) {
    this.additionalActionFactories.add(factory);
    return this;
  }

  public DialogConfigurationBuilder setPreferdSize(final Dimension preferdSize) {
    this.preferdSize = preferdSize;
    return this;
  }
}
