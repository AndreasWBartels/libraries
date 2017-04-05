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

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.dialog.pane.IContentPaneBuilder;

import java.awt.Dialog.ModalityType;

public final class ContentPaneBuilderUsingDialogConfiguration extends
    AbstractContentPaneBuilderUsingDialogConfiguration<Void> {
  private final IContentPaneBuilder contentPaneBuilder;

  public ContentPaneBuilderUsingDialogConfiguration(
    final IPreferences preferences,
    final IMessage message,
    final IContentPaneBuilder contentPaneBuilder,
    final DialogType dialogType) {
    super(
        preferences,
        true,
        message,
        net.anwiba.commons.swing.icon.GuiIcons.EMPTY_ICON,
        DataState.UNKNOWN,
        ModalityType.APPLICATION_MODAL,
        dialogType,
        true,
        new ObjectModel<Void>());
    this.contentPaneBuilder = contentPaneBuilder;
  }

  @Override
  protected IContentPaneBuilder getContentPaneBuilder(
      final IPreferences preferences,
      final DataState dataState,
      final IObjectModel<Void> model) {
    return this.contentPaneBuilder;
  }
}