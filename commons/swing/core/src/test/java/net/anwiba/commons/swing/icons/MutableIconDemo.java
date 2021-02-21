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
package net.anwiba.commons.swing.icons;

import static net.anwiba.testing.demo.JFrames.show;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.swing.icon.MutableImageIcon;
import net.anwiba.commons.swing.icons.GuiIcons;

public class MutableIconDemo {

  @Test
  public void simpleDemo() {
    final MutableImageIcon icon = new MutableImageIcon(GuiIcons.MISC_ICON.getLargeIcon());
    show(icon);
  }

  @Test
  public void complexDemo() {
    final MutableImageIcon icon = new MutableImageIcon(GuiIcons.MISC_ICON.getLargeIcon());
    final JLabel label = new JLabel(icon);
    icon.getModel().addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        label.repaint();
      }
    });
    show(label, frame -> {
      try {
        Thread.sleep(2000);
        icon.getModel().set(GuiIcons.ADD_ICON.getLargeIcon());
        Thread.sleep(2000);
        icon.getModel().set(GuiIcons.CLOSE_ICON.getLargeIcon());
        Thread.sleep(2000);
      } catch (final InterruptedException exception) {
        return;
      }
    });
  }

  @Test
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
    show(button, frame -> {
      try {
        Thread.sleep(2000);
        icon.getModel().set(GuiIcons.ADD_ICON.getLargeIcon());
        Thread.sleep(2000);
        icon.getModel().set(GuiIcons.CLOSE_ICON.getLargeIcon());
        Thread.sleep(2000);
      } catch (final InterruptedException exception) {
        return;
      }
    });
  }

  @Test
  public void complexDemoWithActionAndNormalIcon() {
    final Action action = new AbstractAction(null, GuiIcons.MISC_ICON.getLargeIcon()) {

      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        // nothing to do
      }
    };
    final JButton button = new JButton(action);
    show(button, frame -> {
      try {
        Thread.sleep(2000);
        action.putValue(Action.SMALL_ICON, GuiIcons.ADD_ICON.getLargeIcon());
        Thread.sleep(2000);
        action.putValue(Action.SMALL_ICON, GuiIcons.CLOSE_ICON.getLargeIcon());
        Thread.sleep(2000);
      } catch (final InterruptedException exception) {
        return;
      }
    });
  }
}