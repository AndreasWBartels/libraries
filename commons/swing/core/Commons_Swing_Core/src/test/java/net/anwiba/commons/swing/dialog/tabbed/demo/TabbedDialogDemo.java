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

import javax.swing.JPanel;

import net.anwiba.commons.message.Message;
import net.anwiba.commons.swing.dialog.demo.DummyTabbedDialogTab;
import net.anwiba.commons.swing.dialog.tabbed.TabbedDialog;
import net.anwiba.commons.swing.icon.GuiIcons;

import org.junit.runner.RunWith;

import de.jdemo.annotation.Demo;
import de.jdemo.extensions.SwingDemoCase;
import de.jdemo.junit.DemoAsTestRunner;

@RunWith(DemoAsTestRunner.class)
public class TabbedDialogDemo extends SwingDemoCase {

  @Demo
  public void demoDefaultWithoutTabs() {
    show(new TabbedDialog(createJFrame(), "Demo")); //$NON-NLS-1$
  }

  @Demo
  public void demoDefaultWithOneTab() {
    final TabbedDialog tabbedDialog = new TabbedDialog(createJFrame(), "Demo"); //$NON-NLS-1$
    tabbedDialog.addTab(new DummyTabbedDialogTab("Tab", //$NON-NLS-1$
        Message.create("Test Tab", "Keine besondere Bedeutung"), //$NON-NLS-1$//$NON-NLS-2$
        GuiIcons.GLOBE_ICON.getLargeIcon(),
        new JPanel()));
    tabbedDialog.setSelectedTab(0);
    show(tabbedDialog);
  }

  @Demo
  public void demoDefaultWithTowTabs() {
    final TabbedDialog tabbedDialog = new TabbedDialog(createJFrame(), "Demo"); //$NON-NLS-1$
    tabbedDialog.addTab(new DummyTabbedDialogTab("Tab0", //$NON-NLS-1$
        Message.create("Test Tab 0", "Keine besondere Bedeutung"), //$NON-NLS-1$//$NON-NLS-2$
        GuiIcons.GLOBE_ICON.getLargeIcon(),
        new JPanel()));
    tabbedDialog.addTab(new DummyTabbedDialogTab("Tab1", //$NON-NLS-1$
        Message.create("Test Tab 1", "Keine besondere Bedeutung"), //$NON-NLS-1$//$NON-NLS-2$
        GuiIcons.INFORMATION_ICON.getLargeIcon(),
        new JPanel()));
    tabbedDialog.setSelectedTab(0);
    show(tabbedDialog);
  }

  @Demo
  public void demoDefaultWithTabs() {
    final TabbedDialog tabbedDialog = new TabbedDialog(createJFrame(), "Demo"); //$NON-NLS-1$
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
    show(tabbedDialog);
  }
}
