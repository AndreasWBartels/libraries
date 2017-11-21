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
import java.awt.Frame;
import java.awt.Window;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.preference.IWindowPreferences;

public class MessageDialog extends AbstractDialog {
  private static final long serialVersionUID = 1L;

  public MessageDialog(
      final Frame owner,
      final String title,
      final IMessage message,
      final Icon icon,
      final DialogType dialogType) {
    this(owner, title, message, icon, dialogType, true);
  }

  public MessageDialog(
      final Frame owner,
      final String title,
      final IMessage message,
      final Icon icon,
      final DialogType dialogType,
      final boolean modal) {
    super(owner, title, message, icon, dialogType, modal);
  }

  public MessageDialog(
      final Window owner,
      final String title,
      final IMessage message,
      final Icon icon,
      final DialogType dialogType) {
    this(owner, title, message, icon, dialogType, true);
  }

  public MessageDialog(
      final Window owner,
      final IWindowPreferences preferences,
      final String title,
      final IMessage message,
      final Icon icon,
      final DialogType dialogType) {
    this(owner, preferences, title, message, icon, dialogType, Collections.emptyList(), new ObjectModel<>(), true);
  }

  public MessageDialog(
      final Window owner,
      final String title,
      final IMessage message,
      final Icon icon,
      final DialogType dialogType,
      final boolean modal) {
    super(owner, title, message, icon, dialogType, modal);
  }

  public MessageDialog(
      final Window owner,
      final IWindowPreferences preferences,
      final String title,
      final IMessage message,
      final Icon icon,
      final DialogType dialogType,
      final List<IAdditionalActionFactory> actionFactories,
      final IObjectModel<DataState> dataStateModel,
      final boolean modal) {
    super(owner, preferences, title, message, icon, dialogType, actionFactories, dataStateModel, modal);
  }

  @Override
  protected Component getDetailsComponent() {
    return null;
  }

  @Override
  protected boolean apply() {
    return true;
  }

  @Override
  protected boolean tryOut() {
    return true;
  }

  @Override
  protected boolean cancel() {
    return true;
  }
}
