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
package net.anwiba.spatial.coordinatereferencesystem.axis;

import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinatereferencesystem.axis.AxisSorterFactory;
import net.anwiba.spatial.coordinatereferencesystem.axis.IAxisSorter;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Axis;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.AxisOrientation;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

import static org.junit.Assert.*;

public class AxisSorterFactoryTest {

  @Test
  public void noneOrder() throws Exception {
    final IAxisSorter sorter = new AxisSorterFactory().createSorter(new Axis[0]);
    final ICoordinate coordinate = new Coordinate(0, 1);
    assertThat(sorter.sort(coordinate), equalTo(coordinate));
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalArgumentExceptionThrowing() throws Exception {
    final IAxisSorter sorter = new AxisSorterFactory().createSorter(new Axis[1]);
    final ICoordinate coordinate = new Coordinate(0, 1);
    assertThat(sorter.sort(coordinate), equalTo(coordinate));
  }

  @Test
  public void eastNorthOrder() throws Exception {
    final IAxisSorter sorter = new AxisSorterFactory().createSorter(new Axis[] { new Axis("x", AxisOrientation.EAST), //$NON-NLS-1$
        new Axis("y", AxisOrientation.NORTH) }); //$NON-NLS-1$
    final ICoordinate coordinate = new Coordinate(0, 1);
    assertThat(sorter.sort(coordinate), equalTo(coordinate));
  }

  @Test
  public void eastNorthUpOrder() throws Exception {
    final IAxisSorter sorter = new AxisSorterFactory().createSorter(new Axis[] { new Axis("x", AxisOrientation.EAST), //$NON-NLS-1$
        new Axis("y", AxisOrientation.NORTH), //$NON-NLS-1$
        new Axis("z", AxisOrientation.UP) }); //$NON-NLS-1$
    final ICoordinate coordinate = new Coordinate(0, 1, 2, false);
    assertThat(sorter.sort(coordinate), equalTo(coordinate));
  }

  @Test
  public void eastNorthUpMeasuredOrder() throws Exception {
    final IAxisSorter sorter = new AxisSorterFactory().createSorter(new Axis[] { new Axis("x", AxisOrientation.EAST), //$NON-NLS-1$
        new Axis("y", AxisOrientation.NORTH), //$NON-NLS-1$
        new Axis("z", AxisOrientation.UP) }); //$NON-NLS-1$
    final ICoordinate coordinate = new Coordinate(0, 1, 2, 3);
    assertThat(sorter.sort(coordinate), equalTo(coordinate));
  }

  @Test
  public void northEastOrder() throws Exception {
    final IAxisSorter sorter = new AxisSorterFactory().createSorter(new Axis[] { new Axis("y", AxisOrientation.NORTH), //$NON-NLS-1$
        new Axis("x", AxisOrientation.EAST) }); //$NON-NLS-1$
    final ICoordinate expectedCoordinate = new Coordinate(0, 1);
    assertThat(sorter.sort(new Coordinate(1, 0)), equalTo(expectedCoordinate));
  }

  @Test
  public void northEastUpOrder() throws Exception {
    final IAxisSorter sorter = new AxisSorterFactory().createSorter(new Axis[] { new Axis("y", AxisOrientation.NORTH), //$NON-NLS-1$
        new Axis("x", AxisOrientation.EAST), //$NON-NLS-1$
        new Axis("z", AxisOrientation.UP) }); //$NON-NLS-1$
    final ICoordinate expectedCoordinate = new Coordinate(0, 1, 2, false);
    assertThat(sorter.sort(new Coordinate(1, 0, 2, false)), equalTo(expectedCoordinate));
  }

  @Test
  public void upNorthEastOrder() throws Exception {
    final IAxisSorter sorter = new AxisSorterFactory().createSorter(new Axis[] { new Axis("z", AxisOrientation.UP), //$NON-NLS-1$
        new Axis("y", AxisOrientation.NORTH), //$NON-NLS-1$
        new Axis("x", AxisOrientation.EAST) }); //$NON-NLS-1$
    final ICoordinate expectedCoordinate = new Coordinate(0, 1, 2, false);
    assertThat(sorter.sort(new Coordinate(2, 1, 0, false)), equalTo(expectedCoordinate));
  }

  @Test
  public void upNorthEastMeasuredOrder() throws Exception {
    final IAxisSorter sorter = new AxisSorterFactory().createSorter(new Axis[] { new Axis("z", AxisOrientation.UP), //$NON-NLS-1$
        new Axis("y", AxisOrientation.NORTH), //$NON-NLS-1$
        new Axis("x", AxisOrientation.EAST) }); //$NON-NLS-1$
    final ICoordinate expectedCoordinate = new Coordinate(0, 1, 2, 3);
    assertThat(sorter.sort(new Coordinate(2, 1, 0, 3)), equalTo(expectedCoordinate));
  }

  @Test
  public void invertNoneOrder() throws Exception {
    final IAxisSorter sorter = new AxisSorterFactory().createSorter(new Axis[0]);
    final ICoordinate coordinate = new Coordinate(0, 1);
    assertThat(sorter.sort(coordinate), equalTo(coordinate));
  }

  @Test(expected = IllegalArgumentException.class)
  public void invertIllegalArgumentExceptionThrowing() throws Exception {
    final IAxisSorter sorter = new AxisSorterFactory().createInvertSorter(new Axis[1]);
    final ICoordinate coordinate = new Coordinate(0, 1);
    assertThat(sorter.sort(coordinate), equalTo(coordinate));
  }

  @Test
  public void invertEastNorthOrder() throws Exception {
    final IAxisSorter sorter =
        new AxisSorterFactory().createInvertSorter(new Axis[] { new Axis("x", AxisOrientation.EAST), //$NON-NLS-1$
            new Axis("y", AxisOrientation.NORTH) }); //$NON-NLS-1$
    final ICoordinate coordinate = new Coordinate(0, 1);
    assertThat(sorter.sort(coordinate), equalTo(coordinate));
  }

  @Test
  public void invertEastNorthUpOrder() throws Exception {
    final IAxisSorter sorter =
        new AxisSorterFactory().createInvertSorter(new Axis[] { new Axis("x", AxisOrientation.EAST), //$NON-NLS-1$
            new Axis("y", AxisOrientation.NORTH), //$NON-NLS-1$
            new Axis("z", AxisOrientation.UP) }); //$NON-NLS-1$
    final ICoordinate coordinate = new Coordinate(0, 1, 2, false);
    assertThat(sorter.sort(coordinate), equalTo(coordinate));
  }

  @Test
  public void invertEastNorthUpMeasuredOrder() throws Exception {
    final IAxisSorter sorter =
        new AxisSorterFactory().createInvertSorter(new Axis[] { new Axis("x", AxisOrientation.EAST), //$NON-NLS-1$
            new Axis("y", AxisOrientation.NORTH), //$NON-NLS-1$
            new Axis("z", AxisOrientation.UP) }); //$NON-NLS-1$
    final ICoordinate coordinate = new Coordinate(0, 1, 2, 3);
    assertThat(sorter.sort(coordinate), equalTo(coordinate));
  }

  @Test
  public void invertNorthEastOrder() throws Exception {
    final IAxisSorter sorter =
        new AxisSorterFactory().createInvertSorter(new Axis[] { new Axis("y", AxisOrientation.NORTH), //$NON-NLS-1$
            new Axis("x", AxisOrientation.EAST) }); //$NON-NLS-1$
    final ICoordinate expectedCoordinate = new Coordinate(1, 0);
    assertThat(sorter.sort(new Coordinate(0, 1)), equalTo(expectedCoordinate));
  }

  @Test
  public void invertNorthEastUpOrder() throws Exception {
    final IAxisSorter sorter =
        new AxisSorterFactory().createInvertSorter(new Axis[] { new Axis("y", AxisOrientation.NORTH), //$NON-NLS-1$
            new Axis("x", AxisOrientation.EAST), //$NON-NLS-1$
            new Axis("z", AxisOrientation.UP) }); //$NON-NLS-1$
    final ICoordinate expectedCoordinate = new Coordinate(1, 0, 2, false);
    assertThat(sorter.sort(new Coordinate(0, 1, 2, false)), equalTo(expectedCoordinate));
  }

  @Test
  public void invertUpNorthEastOrder() throws Exception {
    final IAxisSorter sorter =
        new AxisSorterFactory().createInvertSorter(new Axis[] { new Axis("z", AxisOrientation.UP), //$NON-NLS-1$
            new Axis("y", AxisOrientation.NORTH), //$NON-NLS-1$
            new Axis("x", AxisOrientation.EAST) }); //$NON-NLS-1$
    final ICoordinate expectedCoordinate = new Coordinate(2, 1, 0, false);
    assertThat(sorter.sort(new Coordinate(0, 1, 2, false)), equalTo(expectedCoordinate));
  }

  @Test
  public void invertUpNorthEastMeasuredOrder() throws Exception {
    final IAxisSorter sorter =
        new AxisSorterFactory().createInvertSorter(new Axis[] { new Axis("z", AxisOrientation.UP), //$NON-NLS-1$
            new Axis("y", AxisOrientation.NORTH), //$NON-NLS-1$
            new Axis("x", AxisOrientation.EAST) }); //$NON-NLS-1$
    final ICoordinate expectedCoordinate = new Coordinate(2, 1, 0, 3);
    assertThat(sorter.sort(new Coordinate(0, 1, 2, 3)), equalTo(expectedCoordinate));
  }

  @Test
  public void otherNorthEastOtherOrder() throws Exception {
    final IAxisSorter sorter = new AxisSorterFactory().createSorter(new Axis[] { new Axis("n", AxisOrientation.OTHER), //$NON-NLS-1$
        new Axis("x", AxisOrientation.EAST), //$NON-NLS-1$
        new Axis("y", AxisOrientation.NORTH), //$NON-NLS-1$
        new Axis("m", AxisOrientation.OTHER) }); //$NON-NLS-1$
    final ICoordinate expectedCoordinate = new Coordinate(0, 1, 2, 3);
    assertThat(sorter.sort(new Coordinate(2, 0, 1, 3)), equalTo(expectedCoordinate));
  }

  @Test
  public void invertOtherNorthEastOtherOrder() throws Exception {
    final IAxisSorter sorter =
        new AxisSorterFactory().createInvertSorter(new Axis[] { new Axis("n", AxisOrientation.OTHER), //$NON-NLS-1$
            new Axis("x", AxisOrientation.EAST), //$NON-NLS-1$
            new Axis("y", AxisOrientation.NORTH), //$NON-NLS-1$
            new Axis("m", AxisOrientation.OTHER) }); //$NON-NLS-1$
    final ICoordinate expectedCoordinate = new Coordinate(2, 0, 1, 3);
    assertThat(sorter.sort(new Coordinate(0, 1, 2, 3)), equalTo(expectedCoordinate));
  }
}
