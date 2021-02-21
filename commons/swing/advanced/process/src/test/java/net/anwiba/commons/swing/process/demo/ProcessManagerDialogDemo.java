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
package net.anwiba.commons.swing.process.demo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.message.IMessageCollector;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.swing.process.ProcessContextModel;
import net.anwiba.commons.swing.process.ProcessContextModelListModel;
import net.anwiba.commons.swing.process.ProcessManagerDialog;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.thread.process.IProcess;
import net.anwiba.commons.thread.process.IProcessContext;
import net.anwiba.commons.thread.process.IProcessIdentfier;
import net.anwiba.commons.thread.process.IProcessListener;
import net.anwiba.commons.thread.process.ProcessManager;
import net.anwiba.commons.thread.process.ProcessSequencer;
import net.anwiba.commons.thread.queue.WorkQueue;
import net.anwiba.testing.demo.JDialogs;

public class ProcessManagerDialogDemo {

  public static final class ProcessListener implements IProcessListener {
    private final ProcessContextModelListModel processContextModelListModel;
    private final Map<IProcessIdentfier, ProcessContextModel> processes = new HashMap<>();

    public ProcessListener(final ProcessContextModelListModel processContextModelListModel) {
      this.processContextModelListModel = processContextModelListModel;
    }

    @Override
    public void processStarted(final IProcessContext context) {
      final ProcessContextModel processContextModel = new ProcessContextModel(context);
      this.processes.put(context.getProcessIdentfier(), processContextModel);
      this.processContextModelListModel.addProcessContextModel(processContextModel);
    }

    @Override
    public void processFinished(final IProcessIdentfier processIdentfier) {
      final ProcessContextModel processContextModel = this.processes.remove(processIdentfier);
      if (processContextModel != null) {
        this.processContextModelListModel.removeProcessContextModel(processContextModel);
      }
    }

    @Override
    public void allProgressesFinished() {
      final Collection<ProcessContextModel> values = this.processes.values();
      for (final ProcessContextModel processContextModel : values) {
        this.processContextModelListModel.removeProcessContextModel(processContextModel);
      }
    }
  }

  public static final class TestProcess implements IProcess {
    @Override
    public String getDescription() {
      return "Test Process"; //$NON-NLS-1$
    }

    @Override
    public void execute(
        final IMessageCollector processMonitor,
        final ICanceler canceler,
        final IProcessIdentfier processIdentfier)
        throws CanceledException {
      processMonitor.setNote("started"); //$NON-NLS-1$
      for (int i = 0; i < 10; i++) {
        canceler.check();
        try {
          Thread.sleep(1000);
        } catch (final InterruptedException exception) {
          canceler.cancel();
          return;
        }
        processMonitor.setNote(String.valueOf(i));
        if (i == 3) {
          processMonitor.addMessage(Message.create("Test", "Warning test", MessageType.WARNING)); //$NON-NLS-1$//$NON-NLS-2$
        }
        if (i == 7) {
          processMonitor.addMessage(Message.create("Test", "Error test", MessageType.ERROR)); //$NON-NLS-1$//$NON-NLS-2$
        }
      }
      processMonitor.setNote("finished"); //$NON-NLS-1$
    }

    @Override
    public String getQueueName() {
      return "TEST"; //$NON-NLS-1$
    }

    @Override
    public boolean isCancelable() {
      return true;
    }
  }

  @Test
  public void demo() {
    final ProcessManager processManager =
        new ProcessManager((logger, queueName) -> WorkQueue.create(logger, queueName, 1, true, 1));
    final ProcessContextModelListModel processContextModelListModel = new ProcessContextModelListModel();
    processManager.addProcessListener(new ProcessListener(processContextModelListModel));
    processContextModelListModel.addMessage(
        DemoProcessMessageContextFactory.createProsessMessageContext(
            ProcessSequencer.getNextId(),
            "Test Process 0", //$NON-NLS-1$
            "Warning test 0", //$NON-NLS-1$
            MessageType.WARNING));
    processContextModelListModel.addMessage(
        DemoProcessMessageContextFactory.createProsessMessageContext(
            ProcessSequencer.getNextId(),
            "Test Process 1", //$NON-NLS-1$
            "Warning test 1", //$NON-NLS-1$
            MessageType.WARNING));

    JDialogs.show(frame -> new ProcessManagerDialog(
        frame,
        processManager,
        processContextModelListModel),
        dialog -> {
          processManager.execute(new TestProcess());
        });
  }
}
