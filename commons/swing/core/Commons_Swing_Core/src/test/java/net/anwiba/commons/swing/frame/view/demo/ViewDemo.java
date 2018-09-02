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
package net.anwiba.commons.swing.frame.view.demo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.anwiba.commons.swing.frame.ApplicationFrame;
import net.anwiba.commons.swing.frame.IKeyActionConfiguration;
import net.anwiba.commons.swing.frame.KeyActionManager;
import net.anwiba.commons.swing.frame.view.ViewAdapter;
import net.anwiba.commons.swing.frame.view.ViewManager;
import net.anwiba.commons.swing.icons.GuiIcons;
import net.anwiba.commons.swing.menu.AbstractMenuItemConfiguration;
import net.anwiba.commons.swing.menu.AbstractMenuItemDescription;
import net.anwiba.commons.swing.menu.MenuActionItemConfiguration;
import net.anwiba.commons.swing.menu.MenuActionItemDescription;
import net.anwiba.commons.swing.menu.MenuDescription;
import net.anwiba.commons.swing.menu.MenuManager;
import net.anwiba.commons.swing.statebar.IStateBarComponent;
import net.anwiba.commons.swing.statebar.Side;
import net.anwiba.commons.swing.statebar.StateBarComponentConfiguration;
import net.anwiba.commons.swing.statebar.StateBarComponentDescription;
import net.anwiba.commons.swing.statebar.StateBarManager;
import net.anwiba.commons.swing.toolbar.ToolBarDescription;
import net.anwiba.commons.swing.toolbar.ToolBarItemConfiguration;
import net.anwiba.commons.swing.toolbar.ToolBarItemDescription;
import net.anwiba.commons.swing.toolbar.ToolBarManager;

import org.junit.runner.RunWith;

import de.jdemo.annotation.Demo;
import de.jdemo.extensions.SwingDemoCase;
import de.jdemo.junit.DemoAsTestRunner;

@RunWith(DemoAsTestRunner.class)
public class ViewDemo extends SwingDemoCase {

  @Demo
  public void demo() {

    final AbstractMenuItemConfiguration<? extends AbstractMenuItemDescription>[] menuItemConfigurations = new MenuActionItemConfiguration[]{ new MenuActionItemConfiguration(
        new MenuActionItemDescription(new MenuDescription("fileId", "File", //$NON-NLS-1$ //$NON-NLS-2$
            0), 0),
        new AbstractAction("test", GuiIcons.MISC_ICON.getSmallIcon()) { //$NON-NLS-1$

          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(final ActionEvent e) {
            // nothing to do
          }
        }) };

    final ToolBarItemConfiguration[] toolBarItemConfigurations = new ToolBarItemConfiguration[]{ new ToolBarItemConfiguration(
        new ToolBarItemDescription(new ToolBarDescription("toolBar", //$NON-NLS-1$
            0,
            true), 0),
        new AbstractAction("", GuiIcons.MISC_ICON.getSmallIcon()) { //$NON-NLS-1$

          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(final ActionEvent e) {
            // nothing to do
          }
        }) };

    final JPanel panel = new JPanel();
    panel.setPreferredSize(new Dimension(300, 300));
    panel.setBackground(Color.WHITE);

    final StateBarComponentConfiguration[] stateBarComponentConfigurations = new StateBarComponentConfiguration[]{
        new StateBarComponentConfiguration(new StateBarComponentDescription(Side.LEFT, 1), new IStateBarComponent() {

          @Override
          public Component getComponent() {
            final JLabel label = new JLabel("text on left Side"); //$NON-NLS-1$
            label.setBorder(BorderFactory.createLoweredBevelBorder());
            return label;
          }
        }),
        new StateBarComponentConfiguration(new StateBarComponentDescription(Side.RIGHT, 1), new IStateBarComponent() {

          @Override
          public Component getComponent() {
            final JLabel label = new JLabel("right side text"); //$NON-NLS-1$
            label.setBorder(BorderFactory.createLoweredBevelBorder());
            return label;
          }
        }) };

    final ViewAdapter viewAdapter = new ViewAdapter(
        panel,
        menuItemConfigurations,
        toolBarItemConfigurations,
        stateBarComponentConfigurations,
        new IKeyActionConfiguration[0]);
    final ViewManager viewManager = new ViewManager();
    viewManager.add(viewAdapter);

    final ApplicationFrame applicationFrame = new ApplicationFrame(
        new MenuManager(),
        new ToolBarManager(),
        viewManager,
        new StateBarManager(),
        new KeyActionManager());
    applicationFrame.initialize();
    applicationFrame.pack();
    show(applicationFrame);
  }
}
