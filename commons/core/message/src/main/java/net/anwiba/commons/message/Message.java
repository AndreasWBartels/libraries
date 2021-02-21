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

import java.time.LocalDateTime;
import java.util.TimeZone;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.optional.Optional;

public class Message implements IMessage {

  private static final long serialVersionUID = 7859482730024022987L;
  private final String text;
  private final String description;
  private final MessageType messageType;
  private final Throwable throwable;
  LocalDateTime timeStamp = LocalDateTime.now(
      Optional
          .of(System.getProperty("user.timezone"))
          .convert(z -> TimeZone.getTimeZone(z))
          .getOr(() -> TimeZone.getDefault())
          .toZoneId());

  public Message(
      final String text,
      final String description,
      final Throwable throwable,
      final MessageType messageType) {
    Ensure.ensureArgumentNotNull(messageType);
    this.text = text;
    this.description = description;
    this.throwable = throwable;
    this.messageType = messageType;
  }

  public static IMessage create(final String text, final String description, final MessageType messageType) {
    return new MessageBuilder().setInfo().setText(text).setDescription(description).setType(messageType).build();
  }

  public static IMessage create(final String text, final String description) {
    return new MessageBuilder()
        .setInfo()
        .setText(text)
        .setDescription(description)
        .setType(MessageType.DEFAULT)
        .build();
  }

  public static IMessage create(final String text) {
    return new MessageBuilder().setInfo().setText(text).setType(MessageType.DEFAULT).build();
  }

  @Override
  public String getText() {
    return this.text;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public Throwable getThrowable() {
    return this.throwable;
  }

  @Override
  public MessageType getMessageType() {
    return this.messageType;
  }

  @Override
  public LocalDateTime getTimeStamp() {
    return this.timeStamp;
  }
}
