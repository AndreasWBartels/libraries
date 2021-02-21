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
package net.anwiba.spatial.geometry.polygon;

import java.util.ArrayList;
import java.util.List;

import net.anwiba.spatial.geometry.IGeometryFactoryProvider;
import net.anwiba.spatial.geometry.ILinearRing;
import net.anwiba.spatial.geometry.IPolygon;
import net.anwiba.spatial.geometry.internal.LinearRing;
import net.anwiba.spatial.geometry.polygon.tree.GeometryNode;
import net.anwiba.spatial.geometry.polygon.tree.GeometryRootNode;
import net.anwiba.spatial.geometry.polygon.tree.IGeometryNode;

public abstract class AbstractPolygonBuilder implements IPolygonBuilder {

  protected final GeometryRootNode rootNode = new GeometryRootNode();
  protected final IGeometryFactoryProvider factoryProvider;

  public AbstractPolygonBuilder(final IGeometryFactoryProvider factoryProvider) {
    this.factoryProvider = factoryProvider;
  }

  @Override
  public void add(final ILinearRing ring) {
    this.rootNode.add(new GeometryNode(this.factoryProvider, ring));
  }

  protected void add(final PolygonConsumer consumer, final Iterable<IGeometryNode> geometryNodes) {
    for (final IGeometryNode geometryNode : geometryNodes) {
      consumer.add(createPolygon(geometryNode.asLinearRing(), geometryNode.getChildren()));
      for (final IGeometryNode node : geometryNode.getChildren()) {
        add(consumer, node.getChildren());
      }
    }
  }

  private IPolygon createPolygon(final ILinearRing shell, final List<IGeometryNode> children) {
    final List<ILinearRing> holes = new ArrayList<>();
    for (final IGeometryNode node : children) {
      holes.add(node.asLinearRing());
    }
    return this.factoryProvider.getGeometryFactory(shell.getCoordinateReferenceSystem()).createPolygon(
        shell,
        holes.toArray(new LinearRing[holes.size()]));
  }

  protected List<IPolygon> getPolygons() {
    final PolygonConsumer consumer = new PolygonConsumer();
    add(consumer, this.rootNode.getChildren());
    return consumer.getPolygons();
  }

}
