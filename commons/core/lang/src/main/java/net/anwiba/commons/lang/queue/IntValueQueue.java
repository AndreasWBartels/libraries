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
package net.anwiba.commons.lang.queue;

import java.util.NoSuchElementException;

public class IntValueQueue {

  static class Node {

    private Node successor = null;
    private final int value;

    Node(final int value) {
      this.value = value;
    }

    int getValue() {
      return this.value;
    }

    void setSuccessor(final Node successor) {
      this.successor = successor;
    }

    Node getSuccessor() {
      return this.successor;
    }
  }

  private Node first = null;
  private Node last = null;

  public synchronized boolean isEmpty() {
    return this.first == null;
  }

  public synchronized void clear() {
    this.first = null;
    this.last = null;
  }

  public synchronized int poll() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    final int value = this.first.getValue();
    this.first = this.first.getSuccessor();
    if (this.first == null) {
      this.last = null;
    }
    return value;
  }

  public synchronized void add(final int value) {
    if (this.first == null) {
      this.first = this.last = new Node(value);
      return;
    }
    this.last.setSuccessor(new Node(value));
    this.last = this.last.getSuccessor();
  }

}
