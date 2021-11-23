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
package net.anwiba.commons.swing.process;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.swing.utilities.SpringLayoutUtilities;

public class ProcessMessageContextPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  public ProcessMessageContextPanel(final IProcessMessageContext context) {
    final JPanel head = new JPanel();
    final JTextField processField = new JTextField(context.getProcessDescription());
    processField.setColumns(25);
    processField.setEditable(false);
    final IMessage message = context.getMessage();
    final JTextField timeField = new JTextField(message.getTimeStamp().toString());
    timeField.setColumns(25);
    timeField.setEditable(false);
    final JTextField messageField = new JTextField(message.getText());
    messageField.setColumns(25);
    messageField.setEditable(false);
    head.add(new JLabel(ProcessMessages.PROCESS + ":")); //$NON-NLS-1$
    head.add(processField);
    head.add(new JLabel("Time" + ":")); //$NON-NLS-1$
    head.add(timeField);
    head.add(new JLabel(ProcessMessages.MESSAGE + ":")); //$NON-NLS-1$
    head.add(messageField);
    head.add(new JLabel(ProcessMessages.DETAIL + ":")); //$NON-NLS-1$
    head.add(new JPanel());
    head.setLayout(new SpringLayout());
    SpringLayoutUtilities.makeCompactGrid(head, 2, 4, 6, 6, 6, 6);
    final JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BorderLayout());
    contentPanel.add(head, BorderLayout.NORTH);
    final JPanel body = getBody(message);
    contentPanel.add(body, BorderLayout.CENTER);
    setLayout(new GridLayout(1, 1));
    add(contentPanel);
  }

  private JPanel getBody(final IMessage message) {
    if (message.getThrowable() != null) {
      final JPanel body = new JPanel(new BorderLayout());
      body.add(createDetailPane(message.getDescription()), BorderLayout.NORTH);
      body.add(createDetailPane(Message.toDetailInfo(message.getThrowable())), BorderLayout.CENTER);
      return body;
    }
    final JPanel body = new JPanel(new GridLayout(1, 1));
    body.add(createDetailPane(message.getDescription()), BorderLayout.NORTH);
    return body;
  }

  private Component createDetailPane(final String description) {
    final JTextArea textPane = new JTextArea(description);
    textPane.setLineWrap(true);
    final JScrollPane scrollPane = new JScrollPane(textPane);
    scrollPane.setPreferredSize(new Dimension(200, 150));
    return scrollPane;
  }
}