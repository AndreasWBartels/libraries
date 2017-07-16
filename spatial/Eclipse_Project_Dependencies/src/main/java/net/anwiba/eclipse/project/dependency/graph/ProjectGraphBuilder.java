/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2017 Andreas Bartels
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

import net.anwiba.commons.lang.object.ObjectPair;
import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IProject;
import net.anwiba.eclipse.project.dependency.java.IWorkspace;
import net.anwiba.tools.graphml.utilities.Graph;
import net.anwiba.tools.graphml.utilities.GraphUtilities;
import net.anwiba.tools.graphml.utilities.Node;

import java.util.ArrayList;
import java.util.List;

public class ProjectGraphBuilder {

  private final IWorkspace workspace;

  private static enum BuildAction {
    ADD, INTERSECT
  }

  private final List<ObjectPair<BuildAction, IProject>> actions = new ArrayList<>();
  private boolean isNormalizeEnabled = false;

  public ProjectGraphBuilder(final IWorkspace workspace) {
    this.workspace = workspace;
  }

  public ProjectGraphBuilder add(final IProject project) {
    this.actions.add(new ObjectPair<>(BuildAction.ADD, project));
    return this;
  }

  public ProjectGraphBuilder intersect(final IProject project) {
    this.actions.add(new ObjectPair<>(BuildAction.INTERSECT, project));
    return this;
  }

  public void setNormalize(final boolean isNormalizeEnabled) {
    this.isNormalizeEnabled = isNormalizeEnabled;
  }

  public net.anwiba.tools.simple.graphml.generated.Graph build() {
    Graph<String> graph = new Graph<>();
    for (final ObjectPair<BuildAction, IProject> action : this.actions) {
      final Graph<String> current = create(action.getSecondObject());
      switch (action.getFirstObject()) {
        case ADD: {
          graph = add(graph, current);
          continue;
        }
        case INTERSECT: {
          graph = intersect(graph, current);
          continue;
        }
      }
    }
    final LibraryGraph libraryGraph = new LibraryGraph();
    for (final Node<String> node : graph.nodes()) {
      final ILibrary library = this.workspace.getLibraries().get(node.getObject());
      libraryGraph.addNode(library);
    }
    for (final Node<String> node : graph.nodes()) {
      final ILibrary library = this.workspace.getLibraries().get(node.getObject());
      libraryGraph.addEdges(library);
    }
    return GraphmlUtilities.createGraph(libraryGraph, this.isNormalizeEnabled);
  }

  private Graph<String> intersect(final Graph<String> graph, final Graph<String> current) {
    return GraphUtilities.intersect(graph, current);
  }

  private Graph<String> add(final Graph<String> graph, final Graph<String> current) {
    return GraphUtilities.add(graph, current);
  }

  private Graph<String> create(final IProject project) {
    final Graph<String> graph = new Graph<>();
    addTo(graph, project);
    GraphUtilities.normalize(graph);
    return graph;
  }

  private void addTo(final Graph<String> graph, final ILibrary library) {
    if (graph.containts(library.getIdentifier())) {
      return;
    }
    graph.add(new Node<>(library.getIdentifier()));
    final Iterable<ILibrary> usedLibraries = library.getUsedLibraries();
    for (final ILibrary used : usedLibraries) {
      addTo(graph, used);
    }
  }

}
