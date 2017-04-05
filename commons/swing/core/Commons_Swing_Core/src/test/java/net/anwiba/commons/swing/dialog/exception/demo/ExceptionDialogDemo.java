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
package net.anwiba.commons.swing.dialog.exception.demo;

import de.jdemo.annotation.Demo;
import de.jdemo.extensions.SwingDemoCase;
import de.jdemo.junit.DemoAsTestRunner;
import net.anwiba.commons.message.ExceptionMessage;
import net.anwiba.commons.swing.dialog.exception.ExceptionDialog;

import org.junit.runner.RunWith;

@RunWith(DemoAsTestRunner.class)
public class ExceptionDialogDemo extends SwingDemoCase {

  @Demo
  public void demoDefault() {
    show(new ExceptionDialog(createJFrame(), new NullPointerException()));
  }

  @Demo
  public void demoOwnMessage() {
    show(new ExceptionDialog(
        createJFrame(),
        new ExceptionMessage(
            "NullPointerException", "Fehler im Programm, bitte mache Sie eine Fehlermeldung im Bugzilla. Fügen Sie dabei bitte die Details an.", new NullPointerException()))); //$NON-NLS-1$//$NON-NLS-2$
  }
}
