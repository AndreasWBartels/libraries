/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.commons.version.test;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import net.anwiba.commons.version.IVersion;
import net.anwiba.commons.version.ProductState;
import net.anwiba.commons.version.ReleaseState;
import net.anwiba.commons.version.Version;

import org.hamcrest.core.IsEqual;
import org.junit.Test;

public class VersionTest {
  @Test
  public void testVersion() {
    final Date date = Calendar.getInstance().getTime();
    final IVersion version = new Version(
        1,
        2,
        ReleaseState.MILESTONE,
        9,
        ProductState.STABLE,
        date,
        121);
    assertThat(date, new IsEqual<>(version.getDate()));
    assertThat(ReleaseState.MILESTONE, new IsEqual<>(version.getReleaseState()));
    assertThat(ProductState.STABLE, new IsEqual<>(version.getProductState()));
    assertEquals(1, version.getMajor());
    assertEquals(2, version.getMinor());
    assertEquals(9, version.getStep());
    assertEquals(121, version.getBuildCount());
  }
}
