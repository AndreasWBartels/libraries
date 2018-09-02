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

import de.jdemo.annotation.Demo;
import de.jdemo.extensions.SwingDemoCase;
import de.jdemo.junit.DemoAsTestRunner;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import net.anwiba.commons.swing.icons.GuiIcons;
import net.anwiba.commons.swing.menu.ActionListMenu;
import net.anwiba.commons.swing.menu.ActionListModel;

import org.junit.runner.RunWith;

@RunWith(DemoAsTestRunner.class)
public class ActionListMenuDemo extends SwingDemoCase {

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

  @Demo
  public void demo() {
    final Action action = new DummyAction("Action", GuiIcons.MISC_ICON.getSmallIcon()); //$NON-NLS-1$
    final JFrame frame = createJFrame();
    final JMenuBar menubar = new JMenuBar();
    final ActionListModel actionListModel = new ActionListModel();
    final JMenu listMenu = new ActionListMenu("title", actionListModel); //$NON-NLS-1$
    listMenu.add(new AbstractAction("ADD", GuiIcons.ADD_ICON.getSmallIcon()) { //$NON-NLS-1$

          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(final ActionEvent e) {
            actionListModel.add(action);
          }
        });
    listMenu.add(new AbstractAction("REMOVE", GuiIcons.MINUS_ICON.getSmallIcon()) { //$NON-NLS-1$

          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(final ActionEvent e) {
            actionListModel.remove(action);
          }
        });
    menubar.add(listMenu);
    frame.setJMenuBar(menubar);
    show(frame);
  }
}
