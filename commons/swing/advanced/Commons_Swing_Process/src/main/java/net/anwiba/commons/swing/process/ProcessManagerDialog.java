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

import java.awt.Window;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.model.IChangeableListListener;
import net.anwiba.commons.swing.dialog.DialogType;
import net.anwiba.commons.swing.dialog.tabbed.TabbedDialog;
import net.anwiba.commons.swing.icon.GuiIcons;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.thread.process.IProcessManager;

@SuppressWarnings("serial")
public class ProcessManagerDialog extends TabbedDialog {

  public static final class ProcessMessageListener implements IProcessMessageListener {
    private final ProcessMessageContextTableModel tableModel;

    public ProcessMessageListener(final ProcessMessageContextTableModel tableModel) {
      this.tableModel = tableModel;
    }

    @Override
    public void messageAdded(final IProcessMessageContext messageContext) {
      GuiUtilities.invokeLater(new AddMessageContextRunner(this.tableModel, messageContext));
    }

    @Override
    public void messageRemoved(final IProcessMessageContext messageContext) {
      GuiUtilities.invokeLater(new RemoveMessageContextRunner(this.tableModel, messageContext));
    }
  }

  public static final class RemoveMessageContextRunner implements Runnable {
    private final IProcessMessageContext messageContext;
    private final ProcessMessageContextTableModel tableModel;

    public RemoveMessageContextRunner(
        final ProcessMessageContextTableModel tableModel,
        final IProcessMessageContext messageContext) {
      this.tableModel = tableModel;
      this.messageContext = messageContext;
    }

    @Override
    public void run() {
      try {
        this.tableModel.remove(this.messageContext);
      } catch (final Exception exception) {
        logger.log(ILevel.WARNING, "remove message faild", exception); //$NON-NLS-1$
      }
    }
  }

  public static final class AddMessageContextRunner implements Runnable {
    private final IProcessMessageContext messageContext;
    private final ProcessMessageContextTableModel tableModel;

    public AddMessageContextRunner(
        final ProcessMessageContextTableModel tableModel,
        final IProcessMessageContext messageContext) {
      this.tableModel = tableModel;
      this.messageContext = messageContext;
    }

    @Override
    public void run() {
      try {
        this.tableModel.add(this.messageContext);
      } catch (final Exception exception) {
        logger.log(ILevel.WARNING, "add message faild", exception); //$NON-NLS-1$
      }
    }
  }

  final class ProcessMessageContextTableModelListener implements IChangeableListListener<IProcessMessageContext> {

    @Override
    public void objectsAdded(final Iterable<Integer> indeces, final Iterable<IProcessMessageContext> object) {
      // nothing to do
    }

    @Override
    public void objectsRemoved(final Iterable<Integer> indeces, final Iterable<IProcessMessageContext> objects) {
      removeMessages(objects);
    }

    @Override
    public void objectsUpdated(
        final Iterable<Integer> indeces,
        final Iterable<IProcessMessageContext> oldObjects,
        final Iterable<IProcessMessageContext> newObjects) {
      // nothing to do
    }

    @Override
    public void objectsChanged(
        final Iterable<IProcessMessageContext> oldObjects,
        final Iterable<IProcessMessageContext> newObjects) {
      // nothing to do
    }
  }

  static ILogger logger = Logging.getLogger(ProcessManagerDialog.class.getName());

  private final ProcessMessageContextTableModel tableModel = new ProcessMessageContextTableModel();
  private final IProcessMessageListener processMessageListener = new ProcessMessageListener(this.tableModel);
  private final ProcessContextModelListModel processContextModelListModel;

  public ProcessManagerDialog(
      final Window owner,
      final IProcessManager manager,
      final ProcessContextModelListModel processContextModelListModel) {
    super(owner, ProcessMessages.PROCESS_MANAGER, Message.create(ProcessMessages.PROCESS_MANAGER, ""), //$NON-NLS-1$
        GuiIcons.TASKBAR_ICON.getLargeIcon(),
        DialogType.NONE,
        false);
    this.processContextModelListModel = processContextModelListModel;
    this.tableModel.add(processContextModelListModel.getProcessContextMessages());
    this.tableModel.addListModelListener(new ProcessMessageContextTableModelListener());
    addTab(new ProcessListTab(manager, processContextModelListModel));
    addTab(new ProcessMessageTableTab(this, this.tableModel));
    processContextModelListModel.addProcessMessageListener(this.processMessageListener);
    pack();
    GuiUtilities.center(this);
  }

  @Override
  public void dispose() {
    super.dispose();
    this.processContextModelListModel.removeProcessMessageListener(this.processMessageListener);
  }

  void removeMessages(final Iterable<IProcessMessageContext> contexts) {
    for (final IProcessMessageContext context : contexts) {
      this.processContextModelListModel.removeMessage(context);
    }
  }
}