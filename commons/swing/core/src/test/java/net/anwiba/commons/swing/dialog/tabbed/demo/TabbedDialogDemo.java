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
package net.anwiba.commons.swing.dialog.tabbed.demo;

import static net.anwiba.testing.demo.JDialogs.show;

import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.message.Message;
import net.anwiba.commons.swing.dialog.demo.DummyTabbedDialogTab;
import net.anwiba.commons.swing.dialog.tabbed.TabbedDialog;
import net.anwiba.commons.swing.icons.GuiIcons;

public class TabbedDialogDemo {

  @Test
  public void demoDefaultWithoutTabs() {
    show(frame -> new TabbedDialog(frame, "Demo")); //$NON-NLS-1$
  }

  @Test
  public void demoDefaultWithOneTab() {
    show(frame -> {
      final TabbedDialog tabbedDialog = new TabbedDialog(frame, "Demo"); //$NON-NLS-1$
      tabbedDialog.addTab(new DummyTabbedDialogTab("Tab", //$NON-NLS-1$
          Message.create("Test Tab", "Keine besondere Bedeutung"), //$NON-NLS-1$//$NON-NLS-2$
          GuiIcons.GLOBE_ICON.getLargeIcon(),
          new JPanel()));
      tabbedDialog.setSelectedTab(0);
      return tabbedDialog;
    });
  }

  @Test
  public void demoDefaultWithTowTabs() {
    show(frame -> {
      final TabbedDialog tabbedDialog = new TabbedDialog(frame, "Demo"); //$NON-NLS-1$
      tabbedDialog.addTab(new DummyTabbedDialogTab("Tab0", //$NON-NLS-1$
          Message.create("Test Tab 0", "Keine besondere Bedeutung"), //$NON-NLS-1$//$NON-NLS-2$
          GuiIcons.GLOBE_ICON.getLargeIcon(),
          new JPanel()));
      tabbedDialog.addTab(new DummyTabbedDialogTab("Tab1", //$NON-NLS-1$
          Message.create("Test Tab 1", "Keine besondere Bedeutung"), //$NON-NLS-1$//$NON-NLS-2$
          GuiIcons.INFORMATION_ICON.getLargeIcon(),
          new JPanel()));
      tabbedDialog.setSelectedTab(0);
      return tabbedDialog;
    });
  }

  @Test
  public void demoDefaultWithTabs() {
    show(frame -> {
      final TabbedDialog tabbedDialog = new TabbedDialog(frame, "Demo"); //$NON-NLS-1$
      tabbedDialog.addTab(new DummyTabbedDialogTab("Tab0", //$NON-NLS-1$
          Message.create("Test Tab 0", "Keine besondere Bedeutung"), //$NON-NLS-1$//$NON-NLS-2$
          GuiIcons.GLOBE_ICON.getLargeIcon(),
          new JPanel()));
      tabbedDialog.addTab(new DummyTabbedDialogTab("Tab1", //$NON-NLS-1$
          Message.create("Test Tab 1", "Keine besondere Bedeutung"), //$NON-NLS-1$//$NON-NLS-2$
          GuiIcons.INFORMATION_ICON.getLargeIcon(),
          new JPanel()));
      tabbedDialog.addTab(new DummyTabbedDialogTab("Tab2", //$NON-NLS-1$
          Message.create("Test Tab 2", "Keine besondere Bedeutung"), //$NON-NLS-1$//$NON-NLS-2$
          GuiIcons.WARNING_ICON.getLargeIcon(),
          new JPanel()));
      tabbedDialog.setSelectedTab(0);
      return tabbedDialog;
    });
  }
}
