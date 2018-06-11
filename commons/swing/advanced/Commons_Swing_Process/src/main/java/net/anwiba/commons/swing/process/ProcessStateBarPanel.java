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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EtchedBorder;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.observer.IMessageAddedListener;
import net.anwiba.commons.message.observer.IObservableMessageCollector;
import net.anwiba.commons.swing.process.action.OpenProcessManagerDialogAction;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.thread.process.IProcessContext;
import net.anwiba.commons.thread.process.IProcessIdentfier;
import net.anwiba.commons.thread.process.IProcessListener;
import net.anwiba.commons.thread.process.IProcessManager;

@SuppressWarnings("serial")
public class ProcessStateBarPanel extends JPanel implements IBasicProgessBar {

  private static final class ProcessListener implements IProcessListener {
    private final Map<IProcessIdentfier, ProcessContextModel> processes = new HashMap<>();
    private final ProcessContextModelListModel processContextModelListModel;
    private final IBasicProgessBar progressBar;

    public ProcessListener(
        final ProcessContextModelListModel processContextModelListModel,
        final IBasicProgessBar progressBar) {
      this.processContextModelListModel = processContextModelListModel;
      this.progressBar = progressBar;
    }

    @Override
    public synchronized void processStarted(final IProcessContext context) {
      this.progressBar.setIndeterminate(true);
      final ProcessContextModel model = new ProcessContextModel(context);
      this.processes.put(context.getProcessIdentfier(), model);
      this.processContextModelListModel.addProcessContextModel(model);
    }

    @Override
    public synchronized void processFinished(final IProcessIdentfier processIdentfier) {
      this.processContextModelListModel.removeProcessContextModel(this.processes.remove(processIdentfier));
    }

    @Override
    public synchronized void allProgressesFinished() {
      this.progressBar.setIndeterminate(false);
    }
  }

  transient final ProcessContextModelListModel processContextModelListModel = new ProcessContextModelListModel();
  final JProgressBar progressBar = new JProgressBar();

  public ProcessStateBarPanel(
      final Window owner,
      final IProcessManager processManager,
      final IObservableMessageCollector messageCollector) {
    messageCollector.addMessageAddedListener(new IMessageAddedListener() {

      @Override
      public void messageAdded(final IMessage message) {
        final LocalDateTime time = LocalDateTime.now();
        ProcessStateBarPanel.this.processContextModelListModel.addMessage(new IProcessMessageContext() {

          IProcessIdentfier processIdentfier = new IProcessIdentfier() {
            @Override
            public String toString() {
              return ""; //$NON-NLS-1$
            }
          };

          @Override
          public IProcessIdentfier getProcessIdentfier() {
            return this.processIdentfier;
          }

          @Override
          public LocalDateTime getTime() {
            return time;
          }

          @Override
          public String getProcessDescription() {
            return ""; //$NON-NLS-1$
          }

          @Override
          public IMessage getMessage() {
            return message;
          }
        });
      }
    });
    processManager.addProcessListener(new ProcessListener(this.processContextModelListModel, this));
    setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    final Action action = new OpenProcessManagerDialogAction(owner, processManager, this.processContextModelListModel);
    final JButton button = new JButton(action);
    button.setMaximumSize(button.getMinimumSize());
    button.setBackground(getBackground());
    button.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    add(button);
    this.progressBar.setPreferredSize(new Dimension(50, button.getMinimumSize().height - 1));
    this.progressBar.setVisible(this.progressBar.isIndeterminate());
    add(this.progressBar);
  }

  @Override
  public final synchronized void setIndeterminate(final boolean value) {
    GuiUtilities.invokeLater(() -> {
      if (this.progressBar.isIndeterminate() != value) {
        this.progressBar.setIndeterminate(value);
      }
      if (this.progressBar.isVisible() != value) {
        this.progressBar.setVisible(value);
        this.progressBar.revalidate();
      }
    });
  }
}
