/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.commons.swing.dialog.tabbed;

import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.swing.table.ObjectListTable;

public final class TableDialogTab<T> extends AbstractDialogTab {

  public TableDialogTab(
      final String title,
      final IMessage defaultMessage,
      final Icon defaultIcon,
      final ObjectListTable<T> table) {
    super(title, defaultMessage, defaultIcon);
    createView(table);
  }

  private void createView(final ObjectListTable<T> table) {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(new JScrollPane(table.getComponent()));
    setComponent(contentPane);
  }

  @Override
  public void updateView() {
    // nothing to do
  }

  @Override
  public boolean apply() {
    return true;
  }

  @Override
  public void checkFieldValues() {
    // nothing to do
  }
}
