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
package net.anwiba.commons.swing.process;

import java.awt.Window;

import javax.swing.JPanel;

import net.anwiba.commons.swing.dialog.tabbed.AbstractDialogTab;

public class ProcessMessageTableTab extends AbstractDialogTab {

  public ProcessMessageTableTab(
      final Window owner,
      final ProcessMessageContextTableModel processMessageContextTableModel) {
    super(
        ProcessMessages.MESSAGES,
        null,
        net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons.DIALOG_INFORMATION.getLargeIcon());
    final JPanel contentPanel = new ProcessMessageTablePanel(owner, processMessageContextTableModel);
    setComponent(contentPanel);
  }

  @Override
  public void checkFieldValues() {
    // nothing to do
  }

  @Override
  public boolean apply() {
    return true;
  }

  @Override
  public void updateView() {
    // nothing to do
  }
}