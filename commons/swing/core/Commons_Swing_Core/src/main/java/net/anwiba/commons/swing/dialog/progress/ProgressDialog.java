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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.anwiba.commons.lang.object.IObjectContainer;
import net.anwiba.commons.lang.object.ObjectContainer;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.swing.dialog.DialogResult;
import net.anwiba.commons.swing.dialog.DialogType;
import net.anwiba.commons.swing.dialog.MessageDialog;
import net.anwiba.commons.swing.icon.GuiIcons;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.thread.cancel.Canceler;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.thread.progress.IProgressMonitor;
import net.anwiba.commons.thread.progress.IProgressTask;

public class ProgressDialog extends MessageDialog implements IProgressBarParent {

  public static final class ProgressMonitor implements IProgressMonitor {

    private static final long serialVersionUID = 1L;
    private final String messageText;
    private final List<IMessage> messages = new ArrayList<>();

    final JProgressBar progressBar = new JProgressBar();
    final IProgressBarParent parent;

    public ProgressMonitor(final IProgressBarParent parent, final String messageText) {
      this.parent = parent;
      this.messageText = messageText;
    }

    @Override
    public void setValue(final int value) {
      GuiUtilities.invokeLater( //
          () -> { //
            this.progressBar.setValue(value);
          });
    }

    @Override
    public void start() {
      GuiUtilities.invokeLater( //
          () -> { //
            this.progressBar.setIndeterminate(true);
            this.parent.add(this.progressBar);
          });
    }

    @Override
    public void start(final int value, final int maximum) {
      GuiUtilities.invokeLater( //
          () -> { //
            this.progressBar.setMaximum(maximum);
            this.progressBar.setValue(value);
            this.parent.add(this.progressBar);
          });
    }

    @Override
    public void finished() {
      GuiUtilities.invokeLater( //
          () -> { //
            this.parent.setVisible(false);
            this.parent.dispose();
          });
    }

    @Override
    public void setNote(final String note) {
      GuiUtilities.invokeLater( //
          () -> { //
            this.parent.setMessage(Message.create(this.messageText, note, MessageType.DEFAULT));
          });
    }

    @Override
    public void addMessage(final IMessage message) {
      GuiUtilities.invokeLater( //
          () -> { //
            this.messages.add(message);
          });
    }
  }

  private static final long serialVersionUID = 1L;

  private final ICanceler canceler = new Canceler(true);

  private final IProgressMonitor progressMonitor;
  private final JPanel contentPane;

  public ProgressDialog(final Window owner, final String title, final IMessage message) {
    super(owner, title, message, GuiIcons.INFORMATION_ICON.getLargeIcon(), DialogType.CANCEL);
    this.contentPane = new JPanel();
    this.contentPane.setLayout(new FlowLayout(FlowLayout.CENTER));
    this.contentPane.setPreferredSize(new Dimension(20, 40));
    this.contentPane.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
    setContentPane(this.contentPane);
    this.progressMonitor = new ProgressMonitor(this, message.getText());
    setResizable(false);
  }

  @Override
  protected boolean cancel() {
    this.canceler.cancel();
    return false;
  }

  public ICanceler getCanceler() {
    return this.canceler;
  }

  public IProgressMonitor getProgressMonitor() {
    return this.progressMonitor;
  }

  @SuppressWarnings("unchecked")
  public static <O, E extends Exception> O show(
      final Window owner,
      final String title,
      final IMessage message,
      final IProgressTask<O, E> task) throws E, InterruptedException {
    final ProgressDialog dialog = new ProgressDialog(owner, title, message);
    final IProgressMonitor progressMonitor = dialog.getProgressMonitor();
    final IObjectContainer<Exception> exceptionContainer = new ObjectContainer<>();
    final IObjectContainer<O> resultContainer = new ObjectContainer<>();
    final Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          progressMonitor.start();
          final O result = task.run(progressMonitor, dialog.getCanceler());
          resultContainer.set(result);
        } catch (final Exception exception) {
          exceptionContainer.set(exception);
        } finally {
          progressMonitor.finished();
        }
      }
    });
    thread.start();
    dialog.setVisible(true);
    final Exception exception = exceptionContainer.get();
    if (exception != null) {
      if (exception instanceof InterruptedException) {
        throw (InterruptedException) exception;
      }
      if (exception instanceof RuntimeException) {
        throw (RuntimeException) exception;
      }
      throw (E) exception;
    }
    if (DialogResult.CANCEL.equals(dialog.getResult())) {
      throw new InterruptedException();
    }
    return resultContainer.get();
  }

  @Override
  public void add(final JProgressBar progressBar) {
    GuiUtilities.invokeLater( //
        () -> { //
          this.contentPane.add(progressBar);
          this.contentPane.revalidate();
        });
  }

  public static <O, E extends Exception> O show(
      final Window window,
      final IMessage message,
      final IProgressTask<O, E> task) throws E, InterruptedException {
    return show(window, message.getText(), message, task);
  }
}
