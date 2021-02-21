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
// Copyright (c) 2015 by Andreas W. Bartels

package net.anwiba.spatial.geometry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.jupiter.api.Test;

import net.anwiba.spatial.geometry.junit.GeometryAssert;

public class GeometySerializationTest {

  @Test
  public void point() throws IOException, ClassNotFoundException {
    assertSerializable(TestGeometryFactory.createPoint());
  }

  @Test
  public void multiPoint() throws IOException, ClassNotFoundException {
    assertSerializable(TestGeometryFactory.createMultiPoint());
  }

  @Test
  public void lineString() throws IOException, ClassNotFoundException {
    assertSerializable(TestGeometryFactory.createLineString());
  }

  @Test
  public void multiLineString() throws IOException, ClassNotFoundException {
    assertSerializable(TestGeometryFactory.createMultiLineString());
  }

  @Test
  public void linearRing() throws IOException, ClassNotFoundException {
    assertSerializable(TestGeometryFactory.createLinearRing());
  }

  @Test
  public void polygon() throws IOException, ClassNotFoundException {
    assertSerializable(TestGeometryFactory.createPolygonWithHoles());
  }

  @Test
  public void multiPolygon() throws IOException, ClassNotFoundException {
    assertSerializable(TestGeometryFactory.createMultiPolygonWithHoles());
  }

  public void assertSerializable(final IGeometry geometry) throws IOException, ClassNotFoundException {
    GeometryAssert.assertEquals(geometry, writeRead(geometry));
  }

  private <T extends Serializable> T writeRead(final T object) throws IOException, ClassNotFoundException {
    final byte[] array = write(object);
    return read(array);
  };

  private <T extends Serializable> byte[] write(final T object) throws IOException {
    try (final ByteArrayOutputStream memory = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(memory);) {
      out.writeObject(object);
      return memory.toByteArray();
    }
  };

  @SuppressWarnings("unchecked")
  private <T extends Serializable> T read(final byte[] array) throws IOException, ClassNotFoundException {
    try (final ByteArrayInputStream memory = new ByteArrayInputStream(array);
        final ObjectInputStream in = new ObjectInputStream(memory);) {
      return (T) in.readObject();
    }
  };

}
