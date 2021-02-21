/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels 
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
package net.anwiba.tools.graphml.utilities;

import java.util.HashSet;
import java.util.Set;

public class Node<T> {

  Set<Node<T>> subchildren = new HashSet<>();
  Set<Node<T>> notContainingBySubchildren = new HashSet<>();
  Set<Node<T>> children = new HashSet<>();
  private final T object;

  public Node(final T object) {
    this.object = object;
  }

  public void add(final Node<T> childe) {
    this.children.add(childe);
  }

  public void remove(final Node<T> childe) {
    this.children.remove(childe);
  }

  public Iterable<Node<T>> children() {
    return this.children;
  }

  public T getObject() {
    return this.object;
  }

  public boolean childrenContains(final Node<T> node) {
    if (this.subchildren.contains(node)) {
      return true;
    }
    if (this.notContainingBySubchildren.contains(node)) {
      return false;
    }
    for (final Node<T> child : this.children) {
      if (child.contains(node)) {
        this.subchildren.add(node);
        return true;
      }
    }
    this.notContainingBySubchildren.add(node);
    return false;
  }

  public boolean contains(final Node<T> node) {
    if (this.children.contains(node) || this.subchildren.contains(node)) {
      return true;
    }
    if (this.notContainingBySubchildren.contains(node)) {
      return false;
    }
    for (final Node<T> child : this.children) {
      if (child.contains(node)) {
        this.subchildren.add(node);
        return true;
      }
    }
    this.notContainingBySubchildren.add(node);
    return false;
  }

  public void reset() {
    this.subchildren.clear();;
    this.notContainingBySubchildren.clear();
  }
}
