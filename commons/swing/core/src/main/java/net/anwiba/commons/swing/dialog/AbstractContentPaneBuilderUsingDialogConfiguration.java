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
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.preferences.IPreferences;
import net.anwiba.commons.swing.dialog.pane.IContentPaneBuilder;
import net.anwiba.commons.swing.icon.IGuiIcon;

public abstract class AbstractContentPaneBuilderUsingDialogConfiguration<T> extends AbstractDialogConfiguration {

  private final IObjectModel<T> model;
  private final IPreferences preferences;
  private final DataState dataState;

  public AbstractContentPaneBuilderUsingDialogConfiguration(
    final IPreferences preferences,
    final boolean isMessagePanelEnabled,
    final IMessage message,
    final IGuiIcon icon,
    final DataState dataState,
    final ModalityType modality,
    final DialogType dialogType,
    final boolean isResizeable,
    final IObjectModel<T> model) {
    super(
        preferences,
        null,
        isMessagePanelEnabled,
        message.getText(),
        message,
        icon,
        null,
        modality,
        null,
        dialogType,
        s -> s,
        isResizeable,
        KeyEvent.KEY_LOCATION_UNKNOWN,
        new ArrayList<>(),
        dataState);
    this.preferences = preferences;
    this.dataState = dataState;
    this.model = model;
  }

  @Override
  public IContentPaneBuilder getContentPaneBuilder() {
    return getContentPaneBuilder(this.preferences, this.dataState, this.model);
  }

  protected abstract IContentPaneBuilder getContentPaneBuilder(
      IPreferences preferences,
      DataState dataState,
      IObjectModel<T> model);

}