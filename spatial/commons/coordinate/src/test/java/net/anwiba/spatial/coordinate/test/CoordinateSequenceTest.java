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
// Copyright (c) 2006 by Andreas W. Bartels
package net.anwiba.spatial.coordinate.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.lang.functional.IStrategy;
import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.CoordinateSequenceFactory;
import net.anwiba.spatial.coordinate.ICoordinateSequence;
import net.anwiba.spatial.coordinate.ICoordinateSequenceFactory;

public class CoordinateSequenceTest {

  final ICoordinateSequenceFactory coordinateSequenceFactory = new CoordinateSequenceFactory();

  @Test
  public void testDimensionLowerTwo() {
    IStrategy<Exception, RuntimeException> strategie;
    Exception exception;

    strategie = new IStrategy<Exception, RuntimeException>() {

      @Override
      public Exception execute() {
        try {
          CoordinateSequenceTest.this.coordinateSequenceFactory.createEmptyCoordinateSequence(1, false);
        } catch (final Exception e) {
          return e;
        }
        return null;
      }
    };
    exception = strategie.execute();
    assertEquals(IllegalArgumentException.class, exception.getClass());
    assertEquals("Coordinatesequence dimension is lower than 2", exception.getMessage()); //$NON-NLS-1$

  }

  @Test
  public void testWithMValuedDimensionLowerTwo() {
    IStrategy<Exception, RuntimeException> strategie;
    Exception exception;

    strategie = new IStrategy<Exception, RuntimeException>() {

      @Override
      public Exception execute() {
        try {
          final double[][] coordinates = new double[2][];
          coordinates[0] = new double[0];
          coordinates[1] = new double[0];
          CoordinateSequenceTest.this.coordinateSequenceFactory.create(coordinates, true);
        } catch (final Exception e) {
          return e;
        }
        return null;
      }
    };
    exception = strategie.execute();
    assertEquals(IllegalArgumentException.class, exception.getClass());
    assertEquals("Coordinatesequence dimension is lower than 2", exception.getMessage()); //$NON-NLS-1$
  }

  @Test
  public void testOrdinateCountNotEqual() {
    final IStrategy<Exception, RuntimeException> strategie = new IStrategy<Exception, RuntimeException>() {

      @Override
      public Exception execute() {
        try {
          final double[][] coordinates = new double[2][];
          coordinates[0] = new double[0];
          coordinates[1] = new double[5];
          CoordinateSequenceTest.this.coordinateSequenceFactory.create(coordinates, false);
        } catch (final Exception e) {
          return e;
        }
        return null;
      }
    };
    final Exception exception = strategie.execute();
    assertEquals(IllegalArgumentException.class, exception.getClass());
    assertEquals("ordinates count are not equal", exception.getMessage()); //$NON-NLS-1$
  }

  @Test
  public void testNoMeasuredValues() {
    IStrategy<Exception, RuntimeException> strategie;
    Exception exception;

    strategie = new IStrategy<Exception, RuntimeException>() {

      @Override
      public Exception execute() {
        try {
          final double[][] coordinates = new double[2][];
          coordinates[0] = new double[0];
          coordinates[1] = new double[0];
          CoordinateSequenceTest.this.coordinateSequenceFactory.create(coordinates, false).getMeasuredValues();
        } catch (final Exception e) {
          return e;
        }
        return null;
      }
    };
    exception = strategie.execute();
    assertEquals(IllegalArgumentException.class, exception.getClass());
    assertEquals("Coordinatesequence has no measured values", exception.getMessage()); //$NON-NLS-1$
  }

  @Test
  public void testHasMeasuredValues() {
    final double[][] coordinates = new double[3][];
    coordinates[0] = new double[0];
    coordinates[1] = new double[0];
    coordinates[2] = new double[0];
    final double[] measuredValues = this.coordinateSequenceFactory.create(coordinates, true).getMeasuredValues();
    assertNotNull(measuredValues, "has no measured values"); //$NON-NLS-1$
  }

  @Test
  public void testHasZValues() {
    double[][] coordinates = new double[3][];
    coordinates[0] = new double[0];
    coordinates[1] = new double[0];
    coordinates[2] = new double[0];
    double[] zValues = this.coordinateSequenceFactory.create(coordinates, false).getZValues();
    assertNotNull(zValues, "has no z values"); //$NON-NLS-1$

    coordinates = new double[4][];
    coordinates[0] = new double[0];
    coordinates[1] = new double[0];
    coordinates[2] = new double[0];
    coordinates[3] = new double[0];

    zValues = this.coordinateSequenceFactory.create(coordinates, false).getZValues();
    assertNotNull(zValues, "has no z values"); //$NON-NLS-1$
  }

  @Test
  public void testNoZValues() {
    IStrategy<Exception, RuntimeException> strategie;
    Exception exception;

    strategie = new IStrategy<Exception, RuntimeException>() {

      @Override
      public Exception execute() {
        try {
          final double[][] coordinates = new double[2][];
          coordinates[0] = new double[0];
          coordinates[1] = new double[0];
          CoordinateSequenceTest.this.coordinateSequenceFactory.create(coordinates, false).getZValues();
        } catch (final Exception e) {
          return e;
        }
        return null;
      }
    };
    exception = strategie.execute();
    assertEquals(IllegalArgumentException.class, exception.getClass());
    assertEquals("Coordinatesequence has no z values", exception.getMessage()); //$NON-NLS-1$
  }

  @Test
  public void testWithMAndNoZValues() {
    IStrategy<Exception, RuntimeException> strategie;
    Exception exception;

    strategie = new IStrategy<Exception, RuntimeException>() {

      @Override
      public Exception execute() {
        try {
          final double[][] coordinates = new double[3][];
          coordinates[0] = new double[0];
          coordinates[1] = new double[0];
          coordinates[2] = new double[0];
          CoordinateSequenceTest.this.coordinateSequenceFactory.create(coordinates, true).getZValues();
        } catch (final Exception e) {
          return e;
        }
        return null;
      }
    };
    exception = strategie.execute();
    assertEquals(IllegalArgumentException.class, exception.getClass());
    assertEquals("Coordinatesequence has no z values", exception.getMessage()); //$NON-NLS-1$
  }

  @Test
  public void testValues() {
    double[][] coordinates;

    coordinates = new double[4][];
    coordinates[0] = new double[] { 1, 2, 4 };
    coordinates[1] = new double[] { 3, 4, 5 };
    coordinates[2] = new double[] { 5, 6, 9 };
    coordinates[3] = new double[] { 7, 8, Double.NaN };

    final ICoordinateSequence coordinateSequence = this.coordinateSequenceFactory.create(coordinates, true);

    assertEquals(1d, coordinateSequence.getCoordinateN(0).getXValue(), 0);
    assertEquals(2d, coordinateSequence.getCoordinateN(1).getXValue(), 0);
    assertEquals(3d, coordinateSequence.getCoordinateN(0).getYValue(), 0);
    assertEquals(4d, coordinateSequence.getCoordinateN(1).getYValue(), 0);
    assertEquals(5d, coordinateSequence.getCoordinateN(0).getZValue(), 0);
    assertEquals(6d, coordinateSequence.getCoordinateN(1).getZValue(), 0);
    assertTrue(coordinateSequence.isMeasured());
    assertEquals(7d, coordinateSequence.getCoordinateN(0).getMeasuredValue(), 0);
    assertEquals(8d, coordinateSequence.getCoordinateN(1).getMeasuredValue(), 0);
    assertEquals(3, coordinateSequence.getNumberOfCoordinates());
    assertEquals(new Coordinate(1, 3, 5, 7), coordinateSequence.getCoordinateN(0));
    assertTrue(coordinateSequence.getCoordinateN(0).isMeasured());
    assertTrue(coordinateSequence.getCoordinateN(1).isMeasured());
    assertTrue(coordinateSequence.getCoordinateN(2).isMeasured());

  }

  @Test
  public void testIsClosed() {
    assertFalse(this.coordinateSequenceFactory.create(new double[] {}, new double[] {}).isClosed());
    assertFalse(this.coordinateSequenceFactory.create(new double[] { 1, 1, 2 }, new double[] { 1, 2, 2 }).isClosed());
    assertTrue(this.coordinateSequenceFactory.create(new double[] { 1, 1, 1 }, new double[] { 1, 2, 1 }).isClosed());
  }
}
