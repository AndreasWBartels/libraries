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
package net.anwiba.commons.swing.dialog.progress;

import java.awt.Component;
import java.awt.Window;

import javax.swing.SwingUtilities;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.MessageBuilder;
import net.anwiba.commons.thread.progress.IProgressTask;

public class ProgressDialogLauncher<O, E extends Exception> {

  private final MessageBuilder builder = new MessageBuilder();
  private final IProgressTask<O, E> task;
  private String titel = null;

  public ProgressDialogLauncher(final IProgressTask<O, E> task) {
    this.task = task;
  }

  public ProgressDialogLauncher<O, E> setTitle(final String titel) {
    this.titel = titel;
    return this;
  }

  public ProgressDialogLauncher<O, E> setText(final String text) {
    this.builder.setText(text);
    return this;
  }

  public ProgressDialogLauncher<O, E> setDescription(final String description) {
    this.builder.setDescription(description);
    return this;
  }

  public O launch(final Window owner) throws E, InterruptedException {
    final IMessage message = this.builder.build();
    return ProgressDialog.show(owner, this.titel == null ? message.getText() : this.titel, message, this.task);
  }

  public O launch(final Component component) throws E, InterruptedException {
    return launch(component == null ? (Window) null : SwingUtilities.windowForComponent(component));
  }
}
