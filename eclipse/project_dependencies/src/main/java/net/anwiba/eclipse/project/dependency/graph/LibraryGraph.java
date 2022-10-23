/*
 * #%L
 * anwiba eclipse java tools
 * %%
 * Copyright (C) 2007 - 2022 Andreas Bartels
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
package net.anwiba.eclipse.project.dependency.graph;

import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.LibraryType;
import net.anwiba.tools.graphml.utilities.GraphUtilities;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class LibraryGraph {

  final private Map<String, String> labelToId = new HashMap<>();
  final private Map<String, String> idToLabel = new HashMap<>();
  final private Map<String, LibraryType> idToType = new HashMap<>();
  final private Map<String, List<String>> graph = new HashMap<>();
  private int counter = 0;

  public LibraryGraph addNode(final ILibrary item) {
    if (this.labelToId.containsKey(item.getName())) {
      return this;
    }
    final String identifier = MessageFormat.format("N{0,number,00}", Integer.valueOf(this.counter++)); //$NON-NLS-1$
    final String name = item.getName();
    this.idToLabel.put(identifier, name);
    this.idToType.put(identifier, item.getLibraryType());
    this.labelToId.put(name, identifier);
    this.graph.put(identifier, new ArrayList<String>());
    return this;
  }

  public LibraryGraph addEdges(final ILibrary item) {
    if (!this.labelToId.containsKey(item.getName())) {
      return this;
    }
    final String identifier = this.labelToId.get(item.getName());
    final Iterable<ILibrary> usedLibraries = item.getUsedLibraries();
    final List<String> edges = this.graph.get(identifier);
    for (final ILibrary library : usedLibraries) {
      edges.add(this.labelToId.get(library.getName()));
    }
    return this;
  }

  public Map<String, List<String>> getGraph() {
    return this.graph;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(toString(this.graph));
    builder.append("\n\n---\n\n"); //$NON-NLS-1$
    builder.append(toString(GraphUtilities.normalize(this.graph)));
    return builder.toString();
  }

  private String toString(final Map<String, List<String>> graphMap) {
    final StringBuilder builder = new StringBuilder();
    final Set<String> keys = graphMap.keySet();
    for (final String key : keys) {
      builder.append(key).append(": [ "); //$NON-NLS-1$
      final List<String> nodes = graphMap.get(key);
      boolean flag = false;
      for (final String node : nodes) {
        if (flag) {
          builder.append(", "); //$NON-NLS-1$
        }
        builder.append(node);
        flag = true;
      }
      builder.append("]\n"); //$NON-NLS-1$
    }
    return builder.toString();
  }

  public String getLabel(final String key) {
    return this.idToLabel.get(key);
  }

  public LibraryType getType(final String key) {
    return this.idToType.get(key);
  }

}
