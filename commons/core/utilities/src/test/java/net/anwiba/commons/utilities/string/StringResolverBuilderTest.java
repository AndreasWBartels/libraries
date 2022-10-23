/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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
package net.anwiba.commons.utilities.string;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.lang.functional.ResolvingException;

public class StringResolverBuilderTest {

  @Test
  public void user_password() throws ResolvingException {
    IStringResolver resolver = new StringResolverBuilder()
        .converterPattern(".+:?/+((?<username>.+)|()):((?<password>.+)|())@.*")
        .add("username", "username")
        .add("password", "xxxxxxxx")
        .build();
    assertEquals(
        "http://host/",
        resolver.resolve("http://host/"));
    assertEquals(
        "http://username:@host/",
        resolver.resolve("http://foo:@host/"));
    assertEquals(
        "http://:xxxxxxxx@host/",
        resolver.resolve("http://:bar@host/"));
    assertEquals(
        "http://username:xxxxxxxx@host/",
        resolver.resolve("http://foo:bar@host/"));
    assertEquals(
        "https://username:xxxxxxxx@host/path?foo=http://username:xxxxxxxx@host/",
        resolver.resolve("https://foo:bar@host/path?foo=http://foo:bar@host/"));
  }

  @Test
  public void password() throws ResolvingException {
    IStringResolver resolver = new StringResolverBuilder()
        .converterPattern(".+:?/+((?<username>.+)|()):((?<password>.+)|())@.*")
        .add("password", "xxxxxxxx")
        .build();
    assertEquals(
        "https://host/",
        resolver.resolve("https://host/"));
    assertEquals(
        "https://foo:@host/",
        resolver.resolve("https://foo:@host/"));
    assertEquals(
        "https://:xxxxxxxx@host/",
        resolver.resolve("https://:bar@host/"));
    assertEquals(
        "https://foo:xxxxxxxx@host/path?foo=http://foo:xxxxxxxx@host/",
        resolver.resolve("https://foo:bar@host/path?foo=http://foo:bar@host/"));
  }

  @Test
  public void no_username() throws ResolvingException {
    IStringResolver resolver = new StringResolverBuilder()
        .converterPattern(".+:?/+(.+):((?<password>.+)|())@.*")
        .add("password", "xxxxxxxx")
        .build();
    assertEquals(
        "https://host/",
        resolver.resolve("https://host/"));
    assertEquals(
        "https://foo:@host/",
        resolver.resolve("https://foo:@host/"));
    assertEquals(
        "https://:xxxxxxxx@host/",
        resolver.resolve("https://:bar@host/"));
    assertEquals(
        "https://foo:xxxxxxxx@host/path?foo=http://foo:xxxxxxxx@host/",
        resolver.resolve("https://foo:bar@host/path?foo=http://foo:bar@host/"));
  }
}
