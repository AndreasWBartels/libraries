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
package net.anwiba.commons.swing.dialog.demo;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.swing.dialog.DataState;
import net.anwiba.commons.swing.dialog.tabbed.AbstractDialogTab;

public class DummyTabbedDialogTab extends AbstractDialogTab {

  public DummyTabbedDialogTab(
      final String title,
      final IMessage message,
      final Icon icon,
      final JComponent contentComponent) {
    super(title, message, icon);
    setCurrentMessage(message);
    setComponent(contentComponent);
  }

  @Override
  public boolean apply() {
    setDataState(DataState.VALIDE);
    return true;
  }

  @Override
  public void checkFieldValues() {
    // nothing todo
  }

  @Override
  public void updateView() {
    // nothing todo
  }
}
