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

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import net.anwiba.commons.swing.dialog.tabbed.AbstractDialogTab;
import net.anwiba.commons.swing.icons.GuiIcons;
import net.anwiba.commons.thread.process.IProcessManager;

public class ProcessListTab extends AbstractDialogTab {

  private final ProcessListPanel processListPanel;

  public ProcessListTab(final IProcessManager manager, final ProcessContextModelListModel processDescriptionModel) {
    super(ProcessMessages.PROCESSES, null, GuiIcons.MISC_ICON.getLargeIcon());
    this.processListPanel = new ProcessListPanel(manager, processDescriptionModel);
    // processListPanel.setPreferredSize(new Dimension(100, 100));
    final JScrollPane scrollPane = new JScrollPane(this.processListPanel);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    setComponent(scrollPane);
  }

  @Override
  public void dispose() {
    this.processListPanel.dispose();
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