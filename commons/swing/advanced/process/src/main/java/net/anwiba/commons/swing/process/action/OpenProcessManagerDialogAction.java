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
package net.anwiba.commons.swing.process.action;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.swing.icon.GuiIconDecorator;
import net.anwiba.commons.swing.icon.GuiIconSize;
import net.anwiba.commons.swing.icon.IGuiIcon;
import net.anwiba.commons.swing.icons.GuiIcons;
import net.anwiba.commons.swing.process.IProcessMessageContext;
import net.anwiba.commons.swing.process.IProcessMessageListener;
import net.anwiba.commons.swing.process.ProcessContextModelListModel;
import net.anwiba.commons.swing.process.ProcessManagerDialog;
import net.anwiba.commons.swing.process.ProcessMessages;
import net.anwiba.commons.swing.ui.GuiIconDecoration;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.thread.process.IProcessManager;

public final class OpenProcessManagerDialogAction extends AbstractAction {

  private static final long serialVersionUID = 1L;
  private final Window owner;
  private MessageType messageState = MessageType.DEFAULT;
  final ProcessContextModelListModel processContextModelListModel;
  private ProcessManagerDialog dialog;
  private final IProcessManager manager;

  private final Object shemaphor = new Object();

  public OpenProcessManagerDialogAction(
      final Window owner,
      final IProcessManager manager,
      final ProcessContextModelListModel processContextModelListModel) {
    super(null, GuiIcons.TASKBAR_ICON.getSmallIcon());
    this.owner = owner;
    this.manager = manager;
    this.processContextModelListModel = processContextModelListModel;
    this.processContextModelListModel.addProcessMessageListener(new IProcessMessageListener() {

      @Override
      public void messageAdded(final IProcessMessageContext messageContext) {
        synchronized (OpenProcessManagerDialogAction.this.shemaphor) {
          final MessageType messageType = messageContext.getMessage().getMessageType();
          if (messageType.ordinal() <= getMessageState().ordinal()) {
            return;
          }
          update(messageType);
        }
      }

      @Override
      public void messageRemoved(final IProcessMessageContext messageContext) {
        synchronized (OpenProcessManagerDialogAction.this.shemaphor) {
          final MessageType messageType = getCurrentMessageState(
              processContextModelListModel.getProcessContextMessages());
          update(messageType);
        }
      }
    });
    putValue(Action.SHORT_DESCRIPTION, ProcessMessages.PROCESS_MANAGER);
    final MessageType messageType = getCurrentMessageState(processContextModelListModel.getProcessContextMessages());
    update(messageType);
  }

  private void setMessageState(final MessageType messageType) {
    synchronized (this.shemaphor) {
      this.messageState = messageType;
    }
  }

  private Icon getDecoratedIcon(final IGuiIcon guiIcon, final GuiIconDecoration decoration) {
    return GuiIconDecorator.decorate(GuiIconSize.SMALL, guiIcon, decoration);
  }

  protected MessageType getCurrentMessageState(final IProcessMessageContext[] processMessageContexts) {
    synchronized (this.shemaphor) {
      MessageType messageType = MessageType.DEFAULT;
      for (final IProcessMessageContext processMessageContext : processMessageContexts) {
        if (processMessageContext.getMessage().getMessageType().ordinal() > messageType.ordinal()) {
          messageType = processMessageContext.getMessage().getMessageType();
        }
      }
      return messageType;
    }
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    if (this.dialog == null) {
      this.dialog = new ProcessManagerDialog(this.owner, this.manager, this.processContextModelListModel);
    }
    GuiUtilities.invokeLater(() -> {
      this.dialog.setVisible(true);
      this.dialog.toFront();
    });
  }

  protected MessageType getMessageState() {
    synchronized (this.shemaphor) {
      return this.messageState;
    }
  }

  private void update(final MessageType messageType) {
    setMessageState(messageType);
    GuiUtilities.invokeLater(
        () -> putValue(
            Action.SMALL_ICON,
            getDecoratedIcon(GuiIcons.TASKBAR_ICON, GuiIconDecoration.getByMessageType(messageType))));
  }
}