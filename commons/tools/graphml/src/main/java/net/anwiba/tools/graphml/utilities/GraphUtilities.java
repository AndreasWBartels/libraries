/*
 * #%L
 * anwiba commons tools
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
package net.anwiba.tools.graphml.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphUtilities {

  public static Map<String, List<String>> normalize(final Map<String, List<String>> graphMap) {
    final Graph<String> graph = convert(graphMap);
    normalize(graph);
    return convert(graph);
  }

  public static void normalize(final Graph<String> graph) {
    for (final Node<String> node : graph.nodes()) {
      final Iterable<Node<String>> children = copy(node.children());
      for (final Node<String> child : children) {
        if (node.childrenContains(child)) {
          node.remove(child);
        }
      }
    }
    graph.reset();
  }

  public static Graph<String> add(final Graph<String> graph, final Graph<String> other) {
    final Set<Node<String>> nodes = new HashSet<>();
    final Graph<String> result = new Graph<>();
    for (final Node<String> node : graph.nodes()) {
      nodes.add(node);
      result.add(new Node<>(node.getObject()));
    }
    for (final Node<String> node : other.nodes()) {
      nodes.add(node);
      result.add(new Node<>(node.getObject()));
    }
    for (final Node<String> node : nodes) {
      final Node<String> clone = result.get(node.getObject());
      for (final Node<String> child : node.children()) {
        if (!nodes.contains(child)) {
          continue;
        }
        clone.add(result.get(child.getObject()));
      }
    }
    return result;
  }

  public static Graph<String> intersect(final Graph<String> graph, final Graph<String> other) {
    final Set<Node<String>> nodes = new HashSet<>();
    final Graph<String> result = new Graph<>();
    for (final Node<String> node : graph.nodes()) {
      if (!other.contains(node)) {
        continue;
      }
      nodes.add(node);
      result.add(new Node<>(node.getObject()));
    }
    for (final Node<String> node : nodes) {
      final Node<String> clone = result.get(node.getObject());
      for (final Node<String> child : node.children()) {
        if (!nodes.contains(child)) {
          continue;
        }
        clone.add(result.get(child.getObject()));
      }
    }
    return result;
  }

  public static Iterable<Node<String>> copy(final Iterable<Node<String>> nodes) {
    final List<Node<String>> copy = new ArrayList<>();
    for (final Node<String> node : nodes) {
      copy.add(node);
    }
    return copy;
  }

  public static Graph<String> convert(final Map<String, List<String>> graphMap) {
    final Set<String> keys = graphMap.keySet();
    final Graph<String> graph = new Graph<>();
    for (final String key : keys) {
      graph.add(new Node<>(key));
    }
    for (final String key : keys) {
      final Node<String> node = graph.get(key);
      final List<String> children = graphMap.get(key);
      for (final String child : children) {
        node.add(graph.get(child));
      }
    }
    return graph;
  }

  public static Map<String, List<String>> convert(final Graph<String> graph) {
    final Map<String, List<String>> graphMap = new HashMap<>();
    final Iterable<Node<String>> nodes = graph.nodes();
    for (final Node<String> node : nodes) {
      if (!graphMap.containsKey(node.getObject())) {
        graphMap.put(node.getObject(), new ArrayList<String>());
      }
      final List<String> edges = graphMap.get(node.getObject());
      final Iterable<Node<String>> children = node.children();
      for (final Node<String> child : children) {
        edges.add(child.getObject());
      }
    }
    return graphMap;
  }
}