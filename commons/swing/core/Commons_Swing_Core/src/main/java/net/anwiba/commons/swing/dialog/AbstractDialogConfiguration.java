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
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.icon.IGuiIcon;
import net.anwiba.commons.swing.preference.IWindowPreferences;
import net.anwiba.commons.swing.preference.WindowPreferences;

public abstract class AbstractDialogConfiguration implements IDialogConfiguration {

  private final IMessage message;
  private final IGuiIcon icon;
  private final ModalityType modality;
  private final boolean isMessagePanelEnabled;
  private final DialogType dialogType;
  private final boolean isResizeable;
  private final WindowPreferences windowPreferences;
  private final String title;
  private final int dialogCloseKeyEvent;
  private final IFunction<String, String, RuntimeException> actionButtonTextFactory;
  private final List<IAdditionalActionFactory> additionalActionFactories = new ArrayList<>();
  private final Dimension preferdSize;

  public AbstractDialogConfiguration(
      final IPreferences preferences,
      final Dimension preferdSize,
      final boolean isMessagePanelEnabled,
      final String title,
      final IMessage message,
      final IGuiIcon icon,
      final ModalityType modality,
      final DialogType dialogType,
      final IFunction<String, String, RuntimeException> actionButtonTextFactory,
      final boolean isResizeable,
      final int dialogCloseKeyEvent,
      final List<IAdditionalActionFactory> additionalActionFactories) {
    this.preferdSize = preferdSize;
    this.title = title;
    this.actionButtonTextFactory = actionButtonTextFactory;
    this.dialogCloseKeyEvent = dialogCloseKeyEvent;
    this.additionalActionFactories.addAll(additionalActionFactories);
    this.windowPreferences = new WindowPreferences(preferences.node(PREFERENCE_NODE_NAME));
    this.message = message;
    this.icon = icon;
    this.modality = modality;
    this.dialogType = dialogType;
    this.isMessagePanelEnabled = isMessagePanelEnabled;
    this.isResizeable = isResizeable;
  }

  @Override
  public final String getTitle() {
    return this.title;
  }

  @Override
  public final IMessage getMessage() {
    return this.message;
  }

  @Override
  public final Icon getIcon() {
    if (this.icon == null) {
      return null;
    }
    return this.icon.getLargeIcon();
  }

  @Override
  public final boolean isMessagePanelEnabled() {
    return this.isMessagePanelEnabled;
  }

  @Override
  public final ModalityType getModalityType() {
    return this.modality;
  }

  @Override
  public final DialogType getDialogType() {
    return this.dialogType;
  }

  @Override
  public final boolean isResizeable() {
    return this.isResizeable;
  }

  @Override
  public Dimension getPreferdSize() {
    return this.preferdSize;
  }

  @Override
  public IWindowPreferences getWindowPreferences() {
    return this.windowPreferences;
  }

  @Override
  public final int getDialogCloseKeyEvent() {
    return this.dialogCloseKeyEvent;
  }

  @Override
  public List<IAdditionalActionFactory> getAdditionalActionFactories() {
    return this.additionalActionFactories;
  }

  @Override
  public final IFunction<String, String, RuntimeException> getActionButtonTextFactory() {
    return this.actionButtonTextFactory;
  }

}