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

import static net.anwiba.testing.demo.JDialogs.show;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.swing.process.IProcessMessageContext;
import net.anwiba.commons.swing.process.ProcessMessageContextDialog;
import net.anwiba.commons.thread.process.ProcessSequencer;

public class ProcessMessageContextDialogDemo {

  @Test
  public void demoWarning() {
    final IProcessMessageContext processMessageContext =
        DemoProcessMessageContextFactory.createProsessMessageContext(ProcessSequencer.getNextId(),
            "Test Process 0", //$NON-NLS-1$
            "Warning test 0", //$NON-NLS-1$
            MessageType.WARNING);
    show(frame -> new ProcessMessageContextDialog(frame, processMessageContext));
  }

  @Test
  public void demoError() {
    final IProcessMessageContext processMessageContext =
        DemoProcessMessageContextFactory.createProsessMessageContext(ProcessSequencer.getNextId(),
            "Test Process 0", //$NON-NLS-1$
            "Warning test 0", //$NON-NLS-1$
            MessageType.ERROR);
    show(frame -> new ProcessMessageContextDialog(frame, processMessageContext));
  }
}
