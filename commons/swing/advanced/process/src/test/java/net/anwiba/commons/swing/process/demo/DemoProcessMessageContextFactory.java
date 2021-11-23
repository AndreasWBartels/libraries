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

import java.time.LocalDateTime;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.swing.process.IProcessMessageContext;
import net.anwiba.commons.thread.process.IProcessIdentfier;

public class DemoProcessMessageContextFactory {

  public static IProcessMessageContext createProsessMessageContext(
      final IProcessIdentfier processIdentfier,
      final String description,
      final String text,
      final MessageType type) {
    final LocalDateTime time = LocalDateTime.now();
    return new IProcessMessageContext() {

      @Override
      public IProcessIdentfier getProcessIdentfier() {
        return processIdentfier;
      }

      @Override
      public LocalDateTime getTime() {
        return time;
      }

      @Override
      public String getProcessDescription() {
        return description;
      }

      @Override
      public IMessage getMessage() {
        if (type.equals(MessageType.ERROR)) {
          return Message.create("Test", text, new UnknownError(), type); //$NON-NLS-1$
        }
        return Message.create("Test", text, type); //$NON-NLS-1$
      }
    };
  }

}
