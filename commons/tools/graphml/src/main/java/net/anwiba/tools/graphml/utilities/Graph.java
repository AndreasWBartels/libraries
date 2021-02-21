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

import java.util.HashMap;
import java.util.Map;

import net.anwiba.commons.lang.stream.Streams;

public class Graph<T> {

  Map<T, Node<T>> nodes = new HashMap<>();

  public Node<T> get(final T object) {
    return this.nodes.get(object);
  }

  public boolean contains(final T object) {
    return this.nodes.containsKey(object);
  }

  public boolean contains(final Node<T> node) {
    return this.nodes.containsKey(node.getObject());
  }

  public void add(final Node<T> node) {
    this.nodes.put(node.getObject(), node);
  }

  public Iterable<Node<T>> nodes() {
    return this.nodes.values();
  }

  public void reset() {
    Streams.of(nodes()).foreach(Node::reset);
  }
}
