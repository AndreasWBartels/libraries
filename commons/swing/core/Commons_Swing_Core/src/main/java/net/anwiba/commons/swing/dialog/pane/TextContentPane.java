/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
package net.anwiba.commons.swing.dialog.pane;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.anwiba.commons.swing.dialog.DataState;

public final class TextContentPane extends AbstractContentPane {

  private final String text;
  {
    getDataStateModel().set(DataState.MODIFIED);
  }

  public TextContentPane(final String text) {
    this.text = text;
  }

  @Override
  public JComponent getComponent() {
    final JPanel component = new JPanel();
    component.setLayout(new GridLayout(1, 1));
    final JTextArea textArea = new JTextArea(this.text);
    textArea.setLineWrap(false);
    textArea.setEditable(false);
    final JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(400, 300));
    component.add(scrollPane);
    return component;
  }
}
