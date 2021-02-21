/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.commons.thread.process;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.IMessageCollector;

public interface IProcessMonitor extends IMessageCollector {

  IProcessMonitor DummyMonitor = new IProcessMonitor() {

    @Override
    public void setNote(final String note) {
      // nothing to do
    }

    @Override
    public void removeProcessMonitorListener(final IProcessMonitorListener listener) {
      // nothing to do
    }

    @Override
    public void addProcessMonitorListener(final IProcessMonitorListener listener) {
      // nothing to do
    }

    @Override
    public void addMessage(final IMessage message) {
      // nothing to do
    }

  };

  public void removeProcessMonitorListener(IProcessMonitorListener listener);

  public void addProcessMonitorListener(IProcessMonitorListener listener);

}
