/*
 * #%L
 * anwiba commons core
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
package net.anwiba.commons.reference.utilities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import org.junit.jupiter.api.Test;

public class UriUtilitiesTest {

  @Test
  public void testGetParentUri() throws Exception {
    assertThat(UriUtilities.getParentUri(new URI("http://harrie/base/file.test")), //$NON-NLS-1$
        equalTo(new URI("http://harrie/base"))); //$NON-NLS-1$
    assertThat(UriUtilities.getParentUri(new URI("http://harrie/base")), equalTo(new URI("http://harrie/"))); //$NON-NLS-1$ //$NON-NLS-2$
    assertThat(
        UriUtilities.getParentUri(new URI("http://localhost/jgisshell/config/script-module-configuration.xml")), //$NON-NLS-1$
        equalTo(new URI("http://localhost/jgisshell/config"))); //$NON-NLS-1$
    assertThat(UriUtilities.getParentUri(new URI("http://harrie/")), equalTo(new URI("http://harrie/"))); //$NON-NLS-1$ //$NON-NLS-2$
    assertThat(UriUtilities.getParentUri(new URI("file:/harrie/base")), equalTo(new URI("file:/harrie"))); //$NON-NLS-1$ //$NON-NLS-2$
    assertThat(UriUtilities.getParentUri(new URI("file:/harrie")), equalTo(new URI("file:/"))); //$NON-NLS-1$ //$NON-NLS-2$
    assertThat(UriUtilities.getParentUri(new URI("/harrie/base")), equalTo(new URI("/harrie"))); //$NON-NLS-1$ //$NON-NLS-2$
    assertThat(UriUtilities.getParentUri(new URI("/harrie/base/")), equalTo(new URI("/harrie"))); //$NON-NLS-1$ //$NON-NLS-2$
    assertThat(UriUtilities.getParentUri(new URI("/")), equalTo(new URI("/"))); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Test
  public void testIsChild() {
    assertTrue(UriUtilities.isChild("test.html")); //$NON-NLS-1$
    assertTrue(UriUtilities.isChild("./test.html")); //$NON-NLS-1$
    assertTrue(UriUtilities.isChild("../test.html")); //$NON-NLS-1$
    assertFalse(UriUtilities.isChild(null));
    assertFalse(UriUtilities.isChild("")); //$NON-NLS-1$
    assertFalse(UriUtilities.isChild("/")); //$NON-NLS-1$
    assertFalse(UriUtilities.isChild("file:/")); //$NON-NLS-1$
    assertFalse(UriUtilities.isChild("file:/harrie")); //$NON-NLS-1$
    assertFalse(UriUtilities.isChild("http://harrie/test.html")); //$NON-NLS-1$
  }

  @Test
  public void testConcat() throws Exception {
    assertThat(UriUtilities.concat(null, "file.test"), //$NON-NLS-1$
        equalTo(new URI("/file.test"))); //$NON-NLS-1$
    assertThat(UriUtilities.concat(new URI(""), "file.test"), //$NON-NLS-1$ //$NON-NLS-2$
        equalTo(new URI("/file.test"))); //$NON-NLS-1$
    assertThat(UriUtilities.concat(new URI("http://harrie/base"), "file.test"), //$NON-NLS-1$ //$NON-NLS-2$
        equalTo(new URI("http://harrie/base/file.test"))); //$NON-NLS-1$
    assertThat(UriUtilities.concat(new URI("http://harrie/base"), ""), //$NON-NLS-1$ //$NON-NLS-2$
        equalTo(new URI("http://harrie/base"))); //$NON-NLS-1$
    assertThat(UriUtilities.concat(new URI("http://harrie/base"), " "), //$NON-NLS-1$ //$NON-NLS-2$
        equalTo(new URI("http://harrie/base"))); //$NON-NLS-1$
    assertThat(UriUtilities.concat(new URI("http://harrie/base"), null), //$NON-NLS-1$
        equalTo(new URI("http://harrie/base"))); //$NON-NLS-1$
    assertThat(
        UriUtilities.concat(new URI("http://localhost/jgisshell/config"), "../config/script-module-configuration.xml"), //$NON-NLS-1$ //$NON-NLS-2$
        equalTo(new URI("http://localhost/jgisshell/config/../config/script-module-configuration.xml"))); //$NON-NLS-1$
  }

  @Test
  public void testIsFileUrlString() throws Exception {
    assertTrue(UriUtilities.isFileUrl("file:./")); //$NON-NLS-1$
    assertTrue(UriUtilities.isFileUrl("file:../")); //$NON-NLS-1$
    assertTrue(UriUtilities.isFileUrl("file:/test.html")); //$NON-NLS-1$
    assertFalse(UriUtilities.isFileUrl("test.html")); //$NON-NLS-1$
  }

  @Test
  public void testIsHttpUrlString() throws Exception {
    assertTrue(UriUtilities.isHttpUrl("http://test.html")); //$NON-NLS-1$
    assertFalse(UriUtilities.isHttpUrl("test.html")); //$NON-NLS-1$
  }

}