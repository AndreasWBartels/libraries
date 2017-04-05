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

  Map<String, String> labelToId = new HashMap<>();
  Map<String, String> idToLabel = new HashMap<>();
  Map<String, LibraryType> idToType = new HashMap<>();
  Map<String, List<String>> graph = new HashMap<>();
  int counter = 0;

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