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
package net.anwiba.commons.swing.statebar.demo;

import static net.anwiba.testing.demo.JFrames.show;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.swing.statebar.IStateBarComponent;
import net.anwiba.commons.swing.statebar.Side;
import net.anwiba.commons.swing.statebar.StateBarComponentConfiguration;
import net.anwiba.commons.swing.statebar.StateBarComponentDescription;
import net.anwiba.commons.swing.statebar.StateBarManager;

public class StateBarManagerDemo {

  @Test
  public void demo() {
    final StateBarManager stateBarManager = new StateBarManager();
    stateBarManager.add(new StateBarComponentConfiguration(
        new StateBarComponentDescription(Side.RIGHT, 1),
        new IStateBarComponent() {

          @Override
          public Component getComponent() {
            final JLabel label = new JLabel("Text 1"); //$NON-NLS-1$
            label.setBorder(BorderFactory.createLoweredBevelBorder());
            return label;
          }
        }));
    stateBarManager.add(new StateBarComponentConfiguration(
        new StateBarComponentDescription(Side.RIGHT, 2),
        new IStateBarComponent() {

          @Override
          public Component getComponent() {
            return Box.createHorizontalGlue();
          }
        }));
    stateBarManager.add(new StateBarComponentConfiguration(
        new StateBarComponentDescription(Side.LEFT, 2),
        new IStateBarComponent() {

          @Override
          public Component getComponent() {
            final JLabel label = new JLabel("Text 2"); //$NON-NLS-1$
            label.setBorder(BorderFactory.createLoweredBevelBorder());
            return label;
          }
        }));
    stateBarManager.add(new StateBarComponentConfiguration(
        new StateBarComponentDescription(Side.LEFT, 0),
        new IStateBarComponent() {

          @Override
          public Component getComponent() {
            final JLabel label = new JLabel("Text 3"); //$NON-NLS-1$
            label.setBorder(BorderFactory.createLoweredBevelBorder());
            return label;
          }
        }));
    stateBarManager.add(new StateBarComponentConfiguration(
        new StateBarComponentDescription(Side.RIGHT, 4),
        new IStateBarComponent() {

          @Override
          public Component getComponent() {
            final JLabel label = new JLabel("Text 4"); //$NON-NLS-1$
            label.setBorder(BorderFactory.createLoweredBevelBorder());
            return label;
          }
        }));
    final JScrollPane scrollPane = new JScrollPane(new JTextArea());
    scrollPane.setPreferredSize(new Dimension(320, 240));
    final JPanel contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(BorderLayout.CENTER, scrollPane);
    contentPane.add(BorderLayout.SOUTH, stateBarManager.getStateBar());
    show(contentPane);
  }
}
