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

import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.util.List;

import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.dialog.pane.IContentPaneBuilder;
import net.anwiba.commons.swing.icon.IGuiIcon;

public final class DialogConfiguration extends AbstractDialogConfiguration {

  private final IContentPaneBuilder contentPaneBuilder;

  public DialogConfiguration(
      final IPreferences preferences,
      final Dimension preferdSize,
      final boolean isMessagePanelEnabled,
      final String title,
      final IMessage message,
      final IGuiIcon icon,
      final IGuiIcon image,
      final ModalityType modality,
      final ModalExclusionType modalExclusionType,
      final DialogType dialogType,
      final IFunction<String, String, RuntimeException> actionButtonTextFactory,
      final boolean isResizeable,
      final int dialogCloseKeyEvent,
      final List<IAdditionalActionFactory> additionalActionFactories,
      final IContentPaneBuilder contentPaneBuilder) {
    super(
        preferences,
        preferdSize,
        isMessagePanelEnabled,
        title,
        message,
        icon,
        image,
        modality,
        modalExclusionType,
        dialogType,
        actionButtonTextFactory,
        isResizeable,
        dialogCloseKeyEvent,
        additionalActionFactories);
    this.contentPaneBuilder = contentPaneBuilder;
  }

  @Override
  public IContentPaneBuilder getContentPaneBuilder() {
    return this.contentPaneBuilder;
  }
}
