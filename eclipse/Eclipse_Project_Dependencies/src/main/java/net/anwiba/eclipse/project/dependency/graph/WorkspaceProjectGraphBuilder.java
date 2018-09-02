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
package net.anwiba.eclipse.project.dependency.graph;

import net.anwiba.eclipse.project.dependency.java.ILibrary;
import net.anwiba.eclipse.project.dependency.java.IWorkspace;
import net.anwiba.tools.simple.graphml.generated.Graph;

public class WorkspaceProjectGraphBuilder {

  final private LibraryGraph libraryGraph = new LibraryGraph();
  private boolean isNormalizeEnabled = false;

  public WorkspaceProjectGraphBuilder set(final IWorkspace workspace) {
    for (final ILibrary item : workspace.getLibraries().values()) {
      this.libraryGraph.addNode(item);
    }
    for (final ILibrary item : workspace.getLibraries().values()) {
      this.libraryGraph.addEdges(item);
    }
    return this;
  }

  @Override
  public String toString() {
    return this.libraryGraph.toString();
  }

  public Graph build() {
    return GraphmlUtilities.createGraph(this.libraryGraph, this.isNormalizeEnabled);
  }

  public void setNormalize(final boolean isNormalizeEnabled) {
    this.isNormalizeEnabled = isNormalizeEnabled;
  }
}
