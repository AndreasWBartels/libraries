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
package net.anwiba.commons.message;

public class MessageException extends Exception {

  private static final long serialVersionUID = 1L;
  private IMessage message;

  public MessageException(final String message, final Throwable throwable) {
    this(message, throwable, MessageType.ERROR);
  }

  public MessageException(final IMessage message) {
    super(message.getText(), message.getThrowable());
    this.message = message;
  }

  public MessageException(final String message, final Throwable throwable, final MessageType type) {
    this(new MessageBuilder().setText(message).setThrowable(throwable).setType(type).build());
  }

  public MessageType getMessageType() {
    return this.message.getMessageType();
  }

  public IMessage getMessageObject() {
    return this.message;
  }
}
