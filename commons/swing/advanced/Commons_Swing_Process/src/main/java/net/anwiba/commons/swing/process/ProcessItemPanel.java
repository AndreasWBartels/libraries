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

import net.anwiba.commons.swing.icon.GuiIcons;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.swing.utilities.SpringLayoutUtilities;
import net.anwiba.commons.thread.cancel.CancelerProcess;
import net.anwiba.commons.thread.process.IProcessManager;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;

public class ProcessItemPanel extends JPanel {

  public static final class CancelAction extends AbstractAction {
    private final ProcessContextModel model;
    private static final long serialVersionUID = 1L;
    private final IProcessManager manager;

    public CancelAction(final IProcessManager manager, final ProcessContextModel model) {
      super(null, model.isEnabled()
          ? GuiIcons.CANCEL_ICON.getSmallIcon()
          : GuiIcons.DISABLED_CANCEL_ICON.getSmallIcon());
      this.manager = manager;
      this.model = model;
      setEnabled(model.isEnabled());
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
      if (!this.model.isEnabled()) {
        return;
      }
      this.manager.execute(new CancelerProcess(this.model.getCanceler(), "Cancel " + this.model.getDescription()));
    }
  }

  private static final long serialVersionUID = 1L;

  public ProcessItemPanel(final IProcessManager manager, final ProcessContextModel model) {
    final JLabel label = new JLabel(model.getDescription());
    final JPanel labelPanel = new JPanel();
    labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));
    labelPanel.add(label);
    final AbstractAction action = new CancelAction(manager, model);
    final JButton button = new JButton(action);
    button.setBorder(BorderFactory.createEmptyBorder());
    button.setEnabled(model.isEnabled());
    final JProgressBar progressBar = new JProgressBar();
    final JPanel progessPanel = new JPanel();
    final JLabel noteLable = new JLabel();
    progessPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));
    progessPanel.add(button);
    progessPanel.add(progressBar);
    progessPanel.add(noteLable);
    setLayout(new SpringLayout());
    setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    add(labelPanel);
    add(progessPanel);
    SpringLayoutUtilities.makeCompactGrid(this, 1, 2, 2, 2, 2, 2);
    progressBar.setIndeterminate(true);
    model.addProcessModelListener(new IProcessModelListener() {

      @Override
      public void noteChanged(final String note) {
        GuiUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {
            noteLable.setText(note);
            progessPanel.revalidate();
            ProcessItemPanel.this.revalidate();
          }
        });
      }

      @Override
      public void messageAdded(final IProcessMessageContext message) {
        // nothing to do
      }
    });
  }
}
