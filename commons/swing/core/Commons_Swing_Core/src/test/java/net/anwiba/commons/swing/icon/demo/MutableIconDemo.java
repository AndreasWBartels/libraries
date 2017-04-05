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
package net.anwiba.commons.swing.icon.demo;

import de.jdemo.annotation.Demo;
import de.jdemo.extensions.SwingDemoCase;
import de.jdemo.junit.DemoAsTestRunner;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;

import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.swing.icon.GuiIcons;
import net.anwiba.commons.swing.icon.MutableImageIcon;

import org.junit.runner.RunWith;

@RunWith(DemoAsTestRunner.class)
public class MutableIconDemo extends SwingDemoCase {

  @Demo
  public void simpleDemo() {
    final MutableImageIcon icon = new MutableImageIcon(GuiIcons.MISC_ICON.getLargeIcon());
    show(icon);
  }

  @Demo
  public void complexDemo() {
    final MutableImageIcon icon = new MutableImageIcon(GuiIcons.MISC_ICON.getLargeIcon());
    final JLabel label = new JLabel(icon);
    icon.getModel().addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        label.repaint();
      }
    });
    show(label);
    final Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          Thread.sleep(2000);
          icon.getModel().set(GuiIcons.ADD_ICON.getLargeIcon());
          Thread.sleep(2000);
          icon.getModel().set(GuiIcons.CLOSE_ICON.getLargeIcon());
        } catch (final InterruptedException exception) {
          return;
        }
      }
    });
    thread.start();
  }

  @Demo
  public void complexDemoWithAction() {
    final MutableImageIcon icon = new MutableImageIcon(GuiIcons.MISC_ICON.getLargeIcon());
    final Action action = new AbstractAction(null, icon) {

      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        // nothing to do
      }
    };
    final JButton button = new JButton(action);
    icon.getModel().addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        button.repaint();
      }
    });
    show(button);
    final Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          Thread.sleep(2000);
          icon.getModel().set(GuiIcons.ADD_ICON.getLargeIcon());
          Thread.sleep(2000);
          icon.getModel().set(GuiIcons.CLOSE_ICON.getLargeIcon());
        } catch (final InterruptedException exception) {
          return;
        }
      }
    });
    thread.start();
  }

  @Demo
  public void complexDemoWithActionAndNormalIcon() {
    final Action action = new AbstractAction(null, GuiIcons.MISC_ICON.getLargeIcon()) {

      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        // nothing to do
      }
    };
    final JButton button = new JButton(action);
    show(button);
    final Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          Thread.sleep(2000);
          action.putValue(Action.SMALL_ICON, GuiIcons.ADD_ICON.getLargeIcon());
          Thread.sleep(2000);
          action.putValue(Action.SMALL_ICON, GuiIcons.CLOSE_ICON.getLargeIcon());
        } catch (final InterruptedException exception) {
          return;
        }
      }
    });
    thread.start();
  }
}