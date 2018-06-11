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
package net.anwiba.commons.swing.dialog.progress.demo;

import java.lang.reflect.InvocationTargetException;

import org.junit.runner.RunWith;

import de.jdemo.annotation.Demo;
import de.jdemo.extensions.SwingDemoCase;
import de.jdemo.junit.DemoAsTestRunner;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.swing.dialog.progress.ProgressDialog;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.thread.progress.AbstractProgressTask;
import net.anwiba.commons.thread.progress.IProgressMonitor;

@RunWith(DemoAsTestRunner.class)
public class ProgressDialogDemo extends SwingDemoCase {

  @Demo
  public void demo() {
    final ProgressDialog dialog = new ProgressDialog(
        createJFrame(),
        "Progess Dialog Demo", //$NON-NLS-1$
        Message.create("Progess Dialog Demo", "", MessageType.DEFAULT)); //$NON-NLS-1$ //$NON-NLS-2$
    final AbstractProgressTask task = new AbstractProgressTask() {

      @Override
      public void execute(final IProgressMonitor progressMonitor, final ICanceler canceler)
          throws InterruptedException {
        int i = 0;
        while (i < 5) {
          canceler.check();
          progressMonitor.setNote("index " + i); //$NON-NLS-1$
          Thread.sleep(1000);
          i++;
        }
      }

    };
    final Thread thread = new Thread(new Runnable() {

      @Override
      public void run() {
        final IProgressMonitor progressMonitor = dialog.getProgressMonitor();
        try {
          progressMonitor.start();
          task.run(progressMonitor, dialog.getCanceler());
        } catch (final InvocationTargetException exception) {
          // nothing to do
        } catch (final InterruptedException exception) {
          // nothing to do
        } finally {
          progressMonitor.finished();
        }
      }

    });
    thread.start();
    show(dialog);
  }
}
