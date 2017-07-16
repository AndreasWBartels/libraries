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
import java.awt.Image;
import java.util.List;

import javax.swing.Icon;

import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.swing.dialog.pane.IContentPaneBuilder;
import net.anwiba.commons.swing.preference.IWindowPreferences;

public interface IDialogConfiguration {

  public static final String PREFERENCE_NODE_NAME = "dialog"; //$NON-NLS-1$

  String getTitle();

  IMessage getMessage();

  Icon getIcon();

  boolean isMessagePanelEnabled();

  ModalityType getModalityType();

  DialogType getDialogType();

  boolean isResizeable();

  IWindowPreferences getWindowPreferences();

  IContentPaneBuilder getContentPaneBuilder();

  int getDialogCloseKeyEvent();

  IFunction<String, String, RuntimeException> getActionButtonTextFactory();

  List<IAdditionalActionFactory> getAdditionalActionFactories();

  Dimension getPreferdSize();

  Image getImage();

}
