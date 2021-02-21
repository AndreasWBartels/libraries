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
package net.anwiba.spatial.coordinatereferencesystem.coordinatesystem;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import net.anwiba.spatial.coordinatereferencesystem.Authority;

public class AuthorityTest {

  @SuppressWarnings("boxing")
  @Test
  public void urn() {
    Authority authority;
    authority = Authority.valueOf("EPSG:3857");
    assertThat(authority.getName(), equalTo("EPSG"));
    assertThat(authority.getNumber(), equalTo(3857));
    authority = Authority.valueOf("urn:ogc:def:crs:EPSG:6.18.3:3857");
    assertThat(authority.getName(), equalTo("EPSG"));
    assertThat(authority.getNumber(), equalTo(3857));
    authority = Authority.valueOf("urn:ogc:def:crs:EPSG:6.18:3857");
    assertThat(authority.getName(), equalTo("EPSG"));
    assertThat(authority.getNumber(), equalTo(3857));
    authority = Authority.valueOf("urn:ogc:def:crs:EPSG:3857");
    assertThat(authority.getName(), equalTo("EPSG"));
    assertThat(authority.getNumber(), equalTo(3857));
    authority = Authority.valueOf("urn:ogc:def:crs:EPSG:23:3857");
    assertThat(authority.getName(), equalTo("EPSG"));
    assertThat(authority.getNumber(), equalTo(3857));
    authority = Authority.valueOf("urn:ogc:def:crs:EPSG::25832");
    assertThat(authority.getName(), equalTo("EPSG"));
    assertThat(authority.getNumber(), equalTo(25832));
    authority = Authority.valueOf("urn:x-ogc:def:crs:EPSG:31467");
    assertThat(authority.getName(), equalTo("EPSG"));
    assertThat(authority.getNumber(), equalTo(31467));
    authority = Authority.valueOf("http://www.opengis.net/gml/srs/epsg.xml#4326");
    assertThat(authority.getName(), equalTo("EPSG"));
    assertThat(authority.getNumber(), equalTo(4326));
    authority = Authority.valueOf("https://www.opengis.net/def/crs/epsg.xml#/0/6405");
    assertThat(authority.getName(), equalTo("EPSG"));
    assertThat(authority.getNumber(), equalTo(6405));
//    authority = Authority.valueOf("http://www.opengis.net/def/crs/EPSG/0/4326");
//    assertThat(authority.getName(), equalTo("EPSG"));
//    assertThat(authority.getNumber(), equalTo(4325));
//    authority = Authority.valueOf("http://www.opengis.net/def/crs/OGC/0/CRS84");
//    assertThat(authority.getName(), equalTo("EPSG"));
//    assertThat(authority.getNumber(), equalTo(4325));

  }

}
