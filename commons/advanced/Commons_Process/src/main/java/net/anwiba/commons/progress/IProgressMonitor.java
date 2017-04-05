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
package net.anwiba.commons.progress;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.IMessageCollector;

import java.io.Serializable;

public interface IProgressMonitor extends IMessageCollector, Serializable {

  IProgressMonitor DummyMonitor = new IProgressMonitor() {

    private static final long serialVersionUID = 1L;

    @Override
    public void start(final int value, final int maximum) {
      // nothing to do
    }

    @Override
    public void start() {
      // nothing to do
    }

    @Override
    public void setValue(final int value) {
      // nothing to do
    }

    @Override
    public void setNote(final String string) {
      // nothing to do
    }

    @Override
    public void finished() {
      // nothing to do
    }

    @Override
    public void addMessage(final IMessage message) {
      // nothing to do
    }
  };

  void start();

  void start(int value, int maximum);

  void setValue(int value);

  void finished();

}
