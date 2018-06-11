/*
 * #%L
 * anwiba commons core
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

public class MessageBuilder implements IMessageBuilder {

  private MessageType type = MessageType.DEFAULT;
  private String text;
  private String description;
  private Throwable throwable;

  @Override
  public IMessageBuilder setInfo() {
    this.type = MessageType.INFO;
    return this;
  }

  @Override
  public IMessageBuilder setWarning() {
    this.type = MessageType.WARNING;
    return this;
  }

  @Override
  public IMessageBuilder setError() {
    this.type = MessageType.ERROR;
    return this;
  }

  @Override
  public IMessageBuilder setType(final MessageType type) {
    this.type = type;
    return this;
  }

  @Override
  public IMessageBuilder setText(final String text) {
    this.text = text;
    return this;
  }

  @Override
  public IMessageBuilder setDescription(final String description) {
    this.description = description;
    return this;
  }

  @Override
  public IMessageBuilder setThrowable(final Throwable throwable) {
    this.throwable = throwable;
    return this;
  }

  @Override
  public IMessage build() {
    @SuppressWarnings("hiding")
    final String description = getDescription(this.description, this.throwable);
    if (this.throwable != null && MessageType.ERROR.equals(this.type)) {
      return new ExceptionMessage(this.text, description, this.throwable);
    }
    return new Message(this.text, description, this.throwable, this.type);
  }

  @SuppressWarnings("hiding")
  private String getDescription(final String description, final Throwable throwable) {
    if (description != null || throwable == null) {
      return description;
    }
    String text = null;
    Throwable prevoius = null;
    Throwable cause = throwable;
    do {
      text = cause.getMessage();
      prevoius = cause;
      cause = cause.getCause();
    } while ((cause != null && cause != prevoius) && text == null);
    return text;
  }

}
