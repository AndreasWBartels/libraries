/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
 

package net.anwiba.spatial.geometry.polygon;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import net.anwiba.spatial.coordinatereferencesystem.ICoordinateReferenceSystem;
import net.anwiba.spatial.geometry.IGeometryFactory;
import net.anwiba.spatial.geometry.IGeometryFactoryProvider;
import net.anwiba.spatial.geometry.ILinearRing;
import net.anwiba.spatial.geometry.IMultiPolygon;
import net.anwiba.spatial.geometry.TestGeometryFactory;
import net.anwiba.spatial.geometry.polygon.MultiPolygonBuilder;
import net.anwiba.spatial.geometry.utilities.GeometryUtilities;

import org.junit.Test;

public class MultiPolygonBuilderTest {

  private final IGeometryFactoryProvider factoryProvider = new IGeometryFactoryProvider() {

    @Override
    public IGeometryFactory getGeometryFactory(final ICoordinateReferenceSystem coordinateReferenceSystem) {
      return GeometryUtilities.getDefaultGeometryFactory();
    }
  };

  @Test
  public void testShellHole() {
    final ILinearRing shell = TestGeometryFactory.createShellLinearRing();
    final ILinearRing hole = TestGeometryFactory.createHoleLinearRing();
    final MultiPolygonBuilder builder = new MultiPolygonBuilder(this.factoryProvider);
    builder.add(shell);
    builder.add(hole);
    final IMultiPolygon multiPolygon = builder.build();
    assertThat(multiPolygon, notNullValue());
    assertThat(multiPolygon.getNumberOfGeometries(), equalTo(1));
    assertThat(multiPolygon.getGeometryN(0).getOuterRing(), equalTo(shell));
    assertThat(multiPolygon.getGeometryN(0).getNumberOfInnerRings(), equalTo(1));
    assertThat(multiPolygon.getGeometryN(0).getInnerRingN(0), equalTo(hole));
  }

  @Test
  public void testShellRevertedHole() {
    final ILinearRing shell = TestGeometryFactory.createShellLinearRing();
    final ILinearRing hole = TestGeometryFactory.createRevertedHoleLinearRing();
    final MultiPolygonBuilder builder = new MultiPolygonBuilder(this.factoryProvider);
    builder.add(shell);
    builder.add(hole);
    final IMultiPolygon multiPolygon = builder.build();
    assertThat(multiPolygon, notNullValue());
    assertThat(multiPolygon.getNumberOfGeometries(), equalTo(1));
    assertThat(multiPolygon.getGeometryN(0).getOuterRing(), equalTo(shell));
    assertThat(multiPolygon.getGeometryN(0).getNumberOfInnerRings(), equalTo(1));
    assertThat(multiPolygon.getGeometryN(0).getInnerRingN(0), equalTo(hole));
  }

  @Test
  public void testHolesShell() {
    final ILinearRing shell = TestGeometryFactory.createShellLinearRing();
    final ILinearRing hole = TestGeometryFactory.createHoleLinearRing();
    final MultiPolygonBuilder builder = new MultiPolygonBuilder(this.factoryProvider);
    builder.add(hole);
    builder.add(shell);
    final IMultiPolygon multiPolygon = builder.build();
    assertThat(multiPolygon, notNullValue());
    assertThat(multiPolygon.getNumberOfGeometries(), equalTo(1));
    assertThat(multiPolygon.getGeometryN(0).getOuterRing(), equalTo(shell));
    assertThat(multiPolygon.getGeometryN(0).getNumberOfInnerRings(), equalTo(1));
    assertThat(multiPolygon.getGeometryN(0).getInnerRingN(0), equalTo(hole));
  }

  @Test
  public void testRevertedHolesShell() {
    final ILinearRing shell = TestGeometryFactory.createShellLinearRing();
    final ILinearRing hole = TestGeometryFactory.createRevertedHoleLinearRing();
    final MultiPolygonBuilder builder = new MultiPolygonBuilder(this.factoryProvider);
    builder.add(hole);
    builder.add(shell);
    final IMultiPolygon multiPolygon = builder.build();
    assertThat(multiPolygon, notNullValue());
    assertThat(multiPolygon.getNumberOfGeometries(), equalTo(1));
    assertThat(multiPolygon.getGeometryN(0).getOuterRing(), equalTo(shell));
    assertThat(multiPolygon.getGeometryN(0).getNumberOfInnerRings(), equalTo(1));
    assertThat(multiPolygon.getGeometryN(0).getInnerRingN(0), equalTo(hole));
  }

  @Test
  public void testShellNeigbourHole() {
    final ILinearRing shell = TestGeometryFactory.createShellLinearRing();
    final ILinearRing neigbour = TestGeometryFactory.createNeighbourShellLinearRing();
    final ILinearRing hole = TestGeometryFactory.createRevertedHoleLinearRing();
    final MultiPolygonBuilder builder = new MultiPolygonBuilder(this.factoryProvider);
    builder.add(shell);
    builder.add(neigbour);
    builder.add(hole);
    final IMultiPolygon multiPolygon = builder.build();
    assertThat(multiPolygon, notNullValue());
    assertThat(multiPolygon.getNumberOfGeometries(), equalTo(2));
    assertThat(multiPolygon.getGeometryN(0).getOuterRing(), equalTo(shell));
    assertThat(multiPolygon.getGeometryN(1).getOuterRing(), equalTo(neigbour));
  }

  @Test
  public void testShellHoleNeigbour() {
    final ILinearRing shell = TestGeometryFactory.createShellLinearRing();
    final ILinearRing neigbour = TestGeometryFactory.createNeighbourShellLinearRing();
    final ILinearRing hole = TestGeometryFactory.createRevertedHoleLinearRing();
    final MultiPolygonBuilder builder = new MultiPolygonBuilder(this.factoryProvider);
    builder.add(shell);
    builder.add(hole);
    builder.add(neigbour);
    final IMultiPolygon multiPolygon = builder.build();
    assertThat(multiPolygon, notNullValue());
    assertThat(multiPolygon.getNumberOfGeometries(), equalTo(2));
    assertThat(multiPolygon.getGeometryN(0).getOuterRing(), equalTo(shell));
    assertThat(multiPolygon.getGeometryN(1).getOuterRing(), equalTo(neigbour));
  }

  @Test
  public void testHoleShellNeigbour() {
    final ILinearRing shell = TestGeometryFactory.createShellLinearRing();
    final ILinearRing neigbour = TestGeometryFactory.createNeighbourShellLinearRing();
    final ILinearRing hole = TestGeometryFactory.createRevertedHoleLinearRing();
    final MultiPolygonBuilder builder = new MultiPolygonBuilder(this.factoryProvider);
    builder.add(hole);
    builder.add(shell);
    builder.add(neigbour);
    final IMultiPolygon multiPolygon = builder.build();
    assertThat(multiPolygon, notNullValue());
    assertThat(multiPolygon.getNumberOfGeometries(), equalTo(2));
    assertThat(multiPolygon.getGeometryN(0).getOuterRing(), equalTo(shell));
    assertThat(multiPolygon.getGeometryN(1).getOuterRing(), equalTo(neigbour));
  }

  @Test
  public void testNeigbourShell() {
    final ILinearRing shell = TestGeometryFactory.createShellLinearRing();
    final ILinearRing neigbour = TestGeometryFactory.createNeighbourShellLinearRing();
    final MultiPolygonBuilder builder = new MultiPolygonBuilder(this.factoryProvider);
    builder.add(neigbour);
    builder.add(shell);
    final IMultiPolygon multiPolygon = builder.build();
    assertThat(multiPolygon, notNullValue());
    assertThat(multiPolygon.getNumberOfGeometries(), equalTo(2));
    assertThat(multiPolygon.getGeometryN(0).getOuterRing(), equalTo(neigbour));
    assertThat(multiPolygon.getGeometryN(1).getOuterRing(), equalTo(shell));
  }

}
