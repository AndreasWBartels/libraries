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
package net.anwiba.commons.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.swing.icons.GuiIcons;
import net.anwiba.commons.swing.utilities.GuiUtilities;

public class MessagePanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private final JTextArea messageTextArea = new JTextArea();
  private final JLabel messageTitleLabel = new JLabel();
  private final JLabel messageTypeLabel = new JLabel();
  private final IMessage defaultMessage;
  private Icon icon = GuiIcons.EMPTY_ICON.getLargeIcon();

  public MessagePanel(final IMessage defaultMessage, final Icon icon) {
    this.defaultMessage = defaultMessage;
    setIcon(icon);
    setMessage(defaultMessage);
    createView();
  }

  protected void createView() {
    final JPanel textPanel = new JPanel();
    textPanel.setLayout(new BorderLayout());
    textPanel.setBackground(Color.WHITE);
    textPanel.add(BorderLayout.NORTH, this.messageTitleLabel);

    this.messageTextArea.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    this.messageTextArea.setBackground(Color.WHITE);
    this.messageTextArea.setOpaque(true);
    this.messageTextArea.setColumns(30);
    this.messageTextArea.setRows(2);
    this.messageTextArea.setEditable(false);
    this.messageTextArea.setLineWrap(true);
    this.messageTextArea.setWrapStyleWord(true);
    final JScrollPane pane = new JScrollPane(this.messageTextArea);
    pane.setViewportBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    pane.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
    pane.setBackground(Color.WHITE);
    textPanel.add(BorderLayout.CENTER, pane);

    setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
    setBackground(Color.WHITE);
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, textPanel);
    final JPanel labelPanel = new JPanel();
    labelPanel.setLayout(new BorderLayout());
    labelPanel.setBackground(Color.WHITE);
    labelPanel.add(BorderLayout.NORTH, this.messageTypeLabel);
    add(BorderLayout.EAST, labelPanel);
  }

  final public void setIcon(final Icon icon) {
    if (icon == null) {
      this.icon = GuiIcons.EMPTY_ICON.getLargeIcon();
      return;
    }
    this.icon = icon;
  }

  final public void setMessage(final IMessage message) {
    if (message == null) {
      setMessage(this.defaultMessage);
      return;
    }
    GuiUtilities.invokeLater(() -> {
      this.messageTitleLabel.setForeground(MessageUI.getColor(message));
      this.messageTitleLabel.setText(message.getText());
      this.messageTextArea.setForeground(MessageUI.getColor(message));
      this.messageTextArea.setText(message.getDescription());
      this.messageTypeLabel.setIcon(MessageUI.getIcon(message, getIcon()));
    });
  }

  final public Icon getIcon() {
    return this.icon;
  }
}
