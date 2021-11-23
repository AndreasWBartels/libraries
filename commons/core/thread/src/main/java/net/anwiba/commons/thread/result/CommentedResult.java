/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
package net.anwiba.commons.thread.result;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;

import net.anwiba.commons.lang.exception.UnreachableCodeReachedException;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.message.MessageException;
import net.anwiba.commons.message.MessageType;

public class CommentedResult<O> {

  private static IMessage DEFAULT_SUCCESS_MESSAGE = Message.builder().setText("OK").setInfo().build();

  private O value;
  private IMessage message;

  public static <O> CommentedResult<O> success() {
    return new CommentedResult<>(null, DEFAULT_SUCCESS_MESSAGE);
  }

  public static <O> CommentedResult<O> success(O value) {
    return new CommentedResult<>(value, DEFAULT_SUCCESS_MESSAGE);
  }

  public static <O> CommentedResult<O> success(O value, String comment) {
    return new CommentedResult<>(value,
        Message.builder()
        .setText(comment)
        .setInfo()
        .build());
  }
  
  public static <O> CommentedResult<O> success(O value, String comment, String description) {
    return new CommentedResult<>(value,
        Message.builder()
        .setText(comment)
        .setDescription(description)
        .setInfo()
        .build());
  }

  public static <O> CommentedResult<O> unsatisfied(String comment) {
    return new CommentedResult<>(null,
        Message.builder()
        .setText(comment)
        .setWarning()
        .build());
  }

  public static <O> CommentedResult<O> unsatisfied(O value, String comment) {
    return new CommentedResult<>(value,
        Message.builder()
            .setText(comment)
            .setWarning()
            .build());
  }

  public static <O> CommentedResult<O> unsatisfied(O value, String comment, String description) {
    return new CommentedResult<>(value,
        Message.builder()
            .setText(comment)
            .setDescription(description)
            .setWarning()
            .build());
  }

  public static <O> CommentedResult<O> failure(Throwable throwable) {
    return new CommentedResult<>(null,
        Message.builder()
            .setText(throwable.getMessage())
            .setThrowable(throwable)
            .setError()
            .build());
  }

  public static <O> CommentedResult<O> failure(String comment, Throwable throwable) {
    return new CommentedResult<>(null,
        Message.builder()
            .setText(comment)
            .setThrowable(throwable)
            .setError()
            .build());
  }

  public static <O> CommentedResult<O> failure(String comment, String description, Throwable throwable) {
    return new CommentedResult<>(null,
        Message.builder()
            .setText(comment)
            .setDescription(description)
            .setThrowable(throwable)
            .setError()
            .build());
  }

  public static <O> CommentedResult<O> failure(String comment, String description) {
    return new CommentedResult<>(null,
        Message.builder()
            .setText(comment)
            .setDescription(description)
            .setError()
            .build());
  }

  public static <O> CommentedResult<O> failure(String comment) {
    return new CommentedResult<>(null,
        Message.builder()
            .setText(comment)
            .setError()
            .build());
  }

  public static <O, E extends Exception> CommentedResult<O> from(IOptional<O, E> optional) {
    try {
      if (optional.isSuccessful()) {
        return success(optional.get());
      }
    } catch (Throwable exception) {
      throw new UnreachableCodeReachedException(exception);
    }
    return failure(optional.getCause());
  }

  public CommentedResult(O value, IMessage message) {
    this.value = value;
    this.message = message;
  }

  public IOptional<O, MessageException> optional() {
    if (isSuccessful()) {
      return Optional.of(MessageException.class, value);
    }
    return Optional.failed(MessageException.class, new MessageException(message));
  }

  public boolean isSuccessful() {
    return !Objects.equals(message.getMessageType(), MessageType.ERROR);
  }

  public boolean isUnsatisfied() {
    return !Objects.equals(message.getMessageType(), MessageType.WARNING);
  }

  public boolean isFailed() {
    return Objects.equals(message.getMessageType(), MessageType.ERROR);
  }

  public boolean contains(O value) {
    return isSuccessful() && Objects.equals(this.value, value);
  }

  public O get() throws MessageException {
    if (isSuccessful()) {
      return value;
    } else {
      throw new MessageException(message);
    }
  }

  public O getOr(Supplier<O> supplier) {
    if (isSuccessful()) {
      return value;
    } else {
      return supplier.get();
    }
  }
  
  public O getOrThrow() {
    if (isSuccessful()) {
      return value;
    } else {
      // can be changed with java 15
      NoSuchElementException noSuchElementException = new NoSuchElementException("no success value");
      noSuchElementException.initCause( new MessageException(message));
      throw noSuchElementException;
    }
  }

  public MessageException getFailureOrThrow() {
    if (isSuccessful()) {
      throw new NoSuchElementException("no failure value");
    } else {
      return new MessageException(message);
    }
  }

  public IMessage getComment() {
    return message;
  }
}
