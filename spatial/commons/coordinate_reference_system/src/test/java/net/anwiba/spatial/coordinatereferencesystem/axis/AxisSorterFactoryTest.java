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
package net.anwiba.spatial.coordinatereferencesystem.axis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.anwiba.spatial.coordinate.Coordinate;
import net.anwiba.spatial.coordinate.ICoordinate;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.Axis;
import net.anwiba.spatial.coordinatereferencesystem.coordinatesystem.AxisOrientation;

public class AxisSorterFactoryTest {

  @Test
  public void noneOrder() throws Exception {
    final IAxisSorter sorter = new AxisSorterFactory().createSorter(new Axis[0]);
    final ICoordinate coordinate = new Coordinate(0, 1);
    assertThat(sorter.sort(coordinate), equalTo(coordinate));
  }

  @Test
  public void illegalArgumentExceptionThrowing() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> {
      final IAxisSorter sorter = new AxisSorterFactory().createSorter(new Axis[1]);
      final ICoordinate coordinate = new Coordinate(0, 1);
      assertThat(sorter.sort(coordinate), equalTo(coordinate));
    });
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

  @Test
  public void invertIllegalArgumentExceptionThrowing() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> {
      final IAxisSorter sorter = new AxisSorterFactory().createInvertSorter(new Axis[1]);
      final ICoordinate coordinate = new Coordinate(0, 1);
      assertThat(sorter.sort(coordinate), equalTo(coordinate));
    });
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
