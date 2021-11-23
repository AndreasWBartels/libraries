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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.ZonedDateTime;

import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

import net.anwiba.commons.version.IVersion;
import net.anwiba.commons.version.ProductState;
import net.anwiba.commons.version.ReleaseState;
import net.anwiba.commons.version.Version;
import net.anwiba.commons.version.VersionUtilities;

public class VersionTest {
  @Test
  public void testVersion() {
    final ZonedDateTime date = ZonedDateTime
        .of(2018, 9, 2, 20, 14, 0, 0, Clock.systemUTC().getZone());
    final IVersion version = new Version(1, 2, ReleaseState.MILESTONE, 9, ProductState.STABLE, date, 121);
    assertThat(date, new IsEqual<>(version.getDate()));
    assertThat(ReleaseState.MILESTONE, new IsEqual<>(version.getReleaseState()));
    assertThat(ProductState.STABLE, new IsEqual<>(version.getProductState()));
    assertEquals(1, version.getMajor());
    assertEquals(2, version.getMinor());
    assertEquals(9, version.getStep());
    assertEquals(121, version.getBuildCount());
  }

  @Test
  public void parseToTextVersion() {
    final ZonedDateTime date = ZonedDateTime
        .of(2018, 9, 2, 20, 14, 0, 0, Clock.systemUTC().getZone());
    final IVersion version = VersionUtilities.valueOf(
        VersionUtilities.getText(new Version(1, 2, ReleaseState.MILESTONE, 9, ProductState.STABLE, date, 121)));
    assertThat(ReleaseState.MILESTONE, new IsEqual<>(version.getReleaseState()));
    assertThat(ProductState.STABLE, new IsEqual<>(version.getProductState()));
    assertEquals(1, version.getMajor());
    assertEquals(2, version.getMinor());
    assertEquals(9, version.getStep());
  }

  @Test
  public void parseToLongTextVersion() {
    final ZonedDateTime date = ZonedDateTime
        .of(2018, 9, 2, 20, 14, 0, 0, Clock.systemUTC().getZone());
    final IVersion version = VersionUtilities.valueOf(
        VersionUtilities.getTextLong(new Version(1, 2, ReleaseState.MILESTONE, 9, ProductState.STABLE, date, 121)));
    assertThat(version.getDate(), new IsEqual<>(date));
    assertThat(ReleaseState.MILESTONE, new IsEqual<>(version.getReleaseState()));
    assertThat(ProductState.STABLE, new IsEqual<>(version.getProductState()));
    assertEquals(1, version.getMajor());
    assertEquals(2, version.getMinor());
    assertEquals(9, version.getStep());
    assertEquals(121, version.getBuildCount());
  }

  @Test
  public void parse() {
    final IVersion version = VersionUtilities.valueOf("4.4.0-RC1");
    assertEquals(4, version.getMajor());
    assertEquals(4, version.getMinor());
    assertThat(ReleaseState.RELEASECANDIDATE, new IsEqual<>(version.getReleaseState()));
    assertEquals(1, version.getStep());
    assertEquals(0, version.getBuildCount());
  }
}
