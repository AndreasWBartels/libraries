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
package net.anwiba.commons.swing.dialog.tabbed;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.swing.icon.GuiIcons;

public class TextDialogTab extends AbstractNoneEditTabbedDialogTab {

  public TextDialogTab(final String header, final String text) {
    this(header, null, GuiIcons.INFORMATION_ICON.getLargeIcon(), header, text);
  }

  public TextDialogTab(
      final String name,
      final IMessage message,
      final Icon icon,
      final String header,
      final String text) {
    super(name, message, icon);
    final JPanel component = new JPanel();
    component.setLayout(new GridLayout(1, 1));
    final String textString = createText(header, text);
    final JEditorPane textArea = new JEditorPane("text/html", textString); //$NON-NLS-1$
    textArea.setCaretPosition(0);
    textArea.setMinimumSize(new Dimension(200, 100));
    textArea.setPreferredSize(new Dimension(200, 100));
    textArea.setEditable(false);
    final JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(200, 100));
    component.add(scrollPane);
    setComponent(component);
  }

  private String createText(final String header, final String text) {
    final StringBuilder builder = new StringBuilder();
    builder.append("<html><body>"); //$NON-NLS-1$
    if (header != null) {
      builder.append("<H2>"); //$NON-NLS-1$
      builder.append(header);
      builder.append("</H2>"); //$NON-NLS-1$
    }
    builder.append(text);
    builder.append("</body><html>"); //$NON-NLS-1$
    return builder.toString();
  }
}
