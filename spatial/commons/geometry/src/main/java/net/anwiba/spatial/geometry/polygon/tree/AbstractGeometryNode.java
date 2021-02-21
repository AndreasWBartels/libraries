/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2019 Andreas Bartels
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
// Copyright (c) 2013 by Andreas W. Bartels 
package net.anwiba.spatial.geometry.polygon.tree;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGeometryNode {

  private final List<IGeometryNode> geometryNodes = new ArrayList<>();

  public List<IGeometryNode> getChildren() {
    return this.geometryNodes;
  }

  public void add(final IGeometryNode node) {
    for (int i = this.geometryNodes.size() - 1; i > -1; --i) {
      final IGeometryNode geometryNode = this.geometryNodes.get(i);
      if (geometryNode.contains(node)) {
        geometryNode.add(node);
        return;
      }
      if (node.contains(geometryNode)) {
        this.geometryNodes.remove(geometryNode);
        node.add(geometryNode);
      }
    }
    this.geometryNodes.add(node);
  }
}
