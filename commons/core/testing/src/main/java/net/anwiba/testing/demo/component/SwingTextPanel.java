/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
package net.anwiba.testing.demo.component;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import net.anwiba.testing.demo.DemoUtilities;

public class SwingTextPanel {

  private final JComponent content;

  public SwingTextPanel(final CharSequence text) {
    this(text, DemoUtilities.DEFAULT_FIXED_WIDTH_FONT);
  }

  public SwingTextPanel(final CharSequence text, final Font font) {
    JTextPane textPane = new JTextPane();
    textPane.setEditable(false);
    textPane.setText(text.toString());
    textPane.setFont(font);
    this.content = new JScrollPane(textPane);
  }

  public JComponent getContent() {
    return this.content;
  }

}
