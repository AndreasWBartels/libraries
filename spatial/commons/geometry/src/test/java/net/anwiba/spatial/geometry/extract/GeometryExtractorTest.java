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
// Copyright (c) 2010 by Andreas W. Bartels
package net.anwiba.spatial.geometry.extract;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.anwiba.spatial.geometry.IGeometry;
import net.anwiba.spatial.geometry.IGeometryCollection;
import net.anwiba.spatial.geometry.TestGeometryFactory;

public class GeometryExtractorTest {

  GeometryReferenceFactory factory = new GeometryReferenceFactory();

  @Test
  public void extract_neutral() {
    final IGeometry geometry = TestGeometryFactory.createPoint();
    final GeometryExtractor extractor = new GeometryExtractor(geometry);
    assertThat(geometry, equalTo(extractor.extract(this.factory.create())));
  }

  @Test
  public void extract_coordinate() {
    final IGeometry geometry = TestGeometryFactory.createLineString();
    final GeometryExtractor extractor = new GeometryExtractor(geometry);
    assertEquals(geometry.getCoordinateN(1),
        extractor.extract(this.factory.create(1)).getCoordinateN(0));
  }

  @Test
  public void extract_basegeometry() {
    final IGeometryCollection geometry = TestGeometryFactory.createGeometryCollection();
    final GeometryExtractor extractor = new GeometryExtractor(geometry);
    assertThat(geometry.getGeometryN(1), sameInstance(extractor.extract(this.factory.create(1))));
  }

}
