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
package net.anwiba.commons.swing.menu.demo;

import net.anwiba.commons.swing.icons.GuiIcons;
import net.anwiba.commons.swing.menu.JRadioButtonMenuItemProvider;
import net.anwiba.commons.swing.menu.MenuActionItemConfiguration;
import net.anwiba.commons.swing.menu.MenuActionItemDescription;
import net.anwiba.commons.swing.menu.MenuDescription;
import net.anwiba.commons.swing.menu.MenuItemGroupDescription;
import net.anwiba.commons.swing.menu.MenuManager;
import net.anwiba.commons.swing.menu.MenuMenuItemConfiguration;
import net.anwiba.commons.swing.menu.MenuMenuItemDescription;

import de.jdemo.annotation.Demo;
import de.jdemo.extensions.SwingDemoCase;
import de.jdemo.junit.DemoAsTestRunner;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;

import org.junit.runner.RunWith;

@RunWith(DemoAsTestRunner.class)
public class MenuManagerDemo extends SwingDemoCase {

  static class DummyAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public DummyAction(final String name, final Icon icon) {
      super(name, icon);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
      // nothing to do
    }
  }

  @SuppressWarnings("unchecked")
  @Demo
  public void demo() {
    final MenuManager menuManager = new MenuManager();

    final MenuDescription menuDescription0 = new MenuDescription("menu0", "Menu0", 1); //$NON-NLS-1$//$NON-NLS-2$
    final MenuDescription menuDescription1 = new MenuDescription("menu1", "Menu1", 2); //$NON-NLS-1$ //$NON-NLS-2$
    final MenuDescription menuDescription2 = new MenuDescription("menu3", "Menu2", 0); //$NON-NLS-1$ //$NON-NLS-2$
    final MenuItemGroupDescription menuItemGroupDescription = new MenuItemGroupDescription("menugroup", 1); //$NON-NLS-1$
    final MenuItemGroupDescription menuItemToggleGroupDescription =
        new MenuItemGroupDescription("menutogglegroup", -1, true); //$NON-NLS-1$

    menuManager.add(new MenuActionItemConfiguration(
        new MenuActionItemDescription(menuDescription0, 2),
        new DummyAction("mi0", //$NON-NLS-1$
            GuiIcons.GLOBE_ICON.getSmallIcon())));
    menuManager.add(new MenuActionItemConfiguration(
        new MenuActionItemDescription(menuDescription0, 1),
        new DummyAction("mi2", //$NON-NLS-1$
            GuiIcons.COLORIZE_ICON.getSmallIcon())));
    menuManager.add(new MenuActionItemConfiguration(new MenuActionItemDescription(
        menuDescription0,
        menuItemGroupDescription,
        1), new DummyAction("mi3", GuiIcons.ERROR_ICON.getSmallIcon()))); //$NON-NLS-1$

    menuManager.add(new MenuActionItemConfiguration(new MenuActionItemDescription(
        menuDescription1,
        menuItemGroupDescription,
        4), new DummyAction("mi4", GuiIcons.HELP_ICON.getSmallIcon()))); //$NON-NLS-1$

    menuManager.add(new MenuActionItemConfiguration(new MenuActionItemDescription(
        menuDescription1,
        menuItemToggleGroupDescription,
        3), new JRadioButtonMenuItemProvider(new DummyAction("mi7", GuiIcons.INFORMATION_ICON.getSmallIcon())))); //$NON-NLS-1$
    menuManager.add(new MenuActionItemConfiguration(new MenuActionItemDescription(
        menuDescription1,
        menuItemToggleGroupDescription,
        2), new JRadioButtonMenuItemProvider(new DummyAction("mi8", GuiIcons.WARNING_ICON.getSmallIcon())))); //$NON-NLS-1$
    menuManager.add(new MenuActionItemConfiguration(new MenuActionItemDescription(
        menuDescription1,
        menuItemToggleGroupDescription,
        1), new JRadioButtonMenuItemProvider(new DummyAction("mi9", GuiIcons.ERROR_ICON.getSmallIcon())))); //$NON-NLS-1$

    menuManager.add(new MenuActionItemConfiguration(
        new MenuActionItemDescription(menuDescription2, 4),
        new DummyAction("mi5", //$NON-NLS-1$
            GuiIcons.INFORMATION_ICON.getSmallIcon())));
    
    menuManager.add(new MenuMenuItemConfiguration(
        new MenuMenuItemDescription(menuDescription2, 4),
        "menu3.submenu0", "SubMenu0")); //$NON-NLS-1$//$NON-NLS-2$
    
    menuManager.add(new MenuActionItemConfiguration(
        new MenuActionItemDescription(new MenuDescription("menu3.submenu0", "SubMenu0", 0), 4), //$NON-NLS-1$ //$NON-NLS-2$
        new DummyAction("mi6", GuiIcons.INFORMATION_ICON.getSmallIcon()))); //$NON-NLS-1$
    final JFrame frame = createJFrame();
    frame.setJMenuBar(menuManager.getMenuBar());
    show(frame);
  }
}
