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

import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.swing.process.ProcessMessageContextTableModel;
import net.anwiba.commons.swing.process.ProcessMessageTablePanel;
import net.anwiba.commons.thread.process.ProcessSequencer;
import de.jdemo.annotation.Demo;
import de.jdemo.extensions.SwingDemoCase;
import de.jdemo.junit.DemoAsTestRunner;

import java.awt.Window;

import org.junit.runner.RunWith;

@RunWith(DemoAsTestRunner.class)
public class ProcessMessageTablePanelDemo extends SwingDemoCase {

  @Demo
  public void demo() {
    final ProcessMessageContextTableModel tableModel = new ProcessMessageContextTableModel();
    tableModel.add(DemoProcessMessageContextFactory.createProsessMessageContext(
        ProcessSequencer.getNextId(),
        "Test Process 0", "Warning test 0", MessageType.WARNING)); //$NON-NLS-1$ //$NON-NLS-2$
    tableModel.add(DemoProcessMessageContextFactory.createProsessMessageContext(
        ProcessSequencer.getNextId(),
        "Test Process 1", "Warning test 1", MessageType.WARNING)); //$NON-NLS-1$ //$NON-NLS-2$
    tableModel.add(DemoProcessMessageContextFactory.createProsessMessageContext(
        ProcessSequencer.getNextId(),
        "Test Process 2", "Error test", MessageType.ERROR)); //$NON-NLS-1$ //$NON-NLS-2$
    tableModel.add(DemoProcessMessageContextFactory.createProsessMessageContext(
        ProcessSequencer.getNextId(),
        "Test Process 3", "Query test", MessageType.QUERY)); //$NON-NLS-1$ //$NON-NLS-2$
    tableModel.add(DemoProcessMessageContextFactory.createProsessMessageContext(
        ProcessSequencer.getNextId(),
        "Test Process 4", "Warning test 2", MessageType.WARNING)); //$NON-NLS-1$ //$NON-NLS-2$
    tableModel.add(DemoProcessMessageContextFactory.createProsessMessageContext(
        ProcessSequencer.getNextId(),
        "Test Process 5", "Info test", MessageType.INFO)); //$NON-NLS-1$ //$NON-NLS-2$
    show(new ProcessMessageTablePanel((Window) null, tableModel));
  }
}