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

import net.anwiba.spatial.coordinate.CoordinateSequenceUtilities;
import net.anwiba.spatial.coordinate.calculator.CoordinateSequenceOrientationCalculator;
import net.anwiba.spatial.geometry.IGeometryFactoryProvider;
import net.anwiba.spatial.geometry.ILinearRing;
import net.anwiba.spatial.geometry.IPolygon;
import net.anwiba.spatial.geometry.internal.LinearRing;
import net.anwiba.spatial.geometry.polygon.ContainsLinearRingOperator;
import net.anwiba.spatial.geometry.utilities.GeometryUtilities;

public class GeometryNode extends AbstractGeometryNode implements IGeometryNode {

  private final ILinearRing geometry;
  private IPolygon polygon;
  private ContainsLinearRingOperator operator;
  private final IGeometryFactoryProvider factoryProvider;

  public GeometryNode(final IGeometryFactoryProvider factoryProvider, final ILinearRing ring) {
    this.factoryProvider = factoryProvider;
    this.geometry = ring;
  }

  @Override
  public boolean contains(final IGeometryNode node) {
    final IPolygon ownPolygon = asPolygon();
    final IPolygon nodePolygon = node.asPolygon();
    if (!this.geometry.getEnvelope().contains(nodePolygon.getEnvelope())) {
      return false;
    }
    if (GeometryUtilities.isRectangle(ownPolygon)) {
      return ownPolygon.getEnvelope().contains(nodePolygon.getEnvelope());
    }
    return contains(node.asLinearRing());
  }

  private boolean contains(final ILinearRing ring) {
    if (this.operator == null) {
      this.operator = new ContainsLinearRingOperator(this.geometry);
    }
    return this.operator.contains(ring);
  }

  @Override
  public ILinearRing asExteriorRing() {
    if (!CoordinateSequenceOrientationCalculator.isOrientationPositive(this.geometry.getCoordinateSequence())) {
      return this.geometry;
    }
    return this.factoryProvider.getGeometryFactory(this.geometry.getCoordinateReferenceSystem()).createLinearRing(
        CoordinateSequenceUtilities.reverse(this.geometry.getCoordinateSequence()));
  }

  @Override
  public ILinearRing asInnerRing() {
    if (CoordinateSequenceOrientationCalculator.isOrientationPositive(this.geometry.getCoordinateSequence())) {
      return this.geometry;
    }
    return this.factoryProvider.getGeometryFactory(this.geometry.getCoordinateReferenceSystem()).createLinearRing(
        CoordinateSequenceUtilities.reverse(this.geometry.getCoordinateSequence()));
  }

  @Override
  public ILinearRing asLinearRing() {
    return this.geometry;
  }

  @Override
  public IPolygon asPolygon() {
    if (this.polygon == null) {
      this.polygon = this.factoryProvider
          .getGeometryFactory(this.geometry.getCoordinateReferenceSystem())
          .createPolygon(this.geometry, new LinearRing[0]);
    }
    return this.polygon;
  }
}
