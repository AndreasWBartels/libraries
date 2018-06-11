/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.commons.datasource.resource;

import static org.junit.Assert.*;

import org.junit.Test;

import net.anwiba.commons.lang.functional.ConversionException;
import net.anwiba.commons.utilities.io.url.IUrl;

@SuppressWarnings({ "unused", "nls" })
public class StringToUrlConverterTest {

  private final StringToUrlConverter converter = new StringToUrlConverter();

  private IUrl assertUrl(final String string) throws ConversionException {
    System.out.println(string);
    final IUrl url = this.converter.convert(string);
    assertNotNull(url);
    System.out.println(url);
    return url;
  }

  //https://anwiba.net/foo/bar/bar.xml?foo=foobar&bar=bar foo#fra
  @Test
  public void scheme1() throws ConversionException {
    final Object url = assertUrl("georss:https:///");
  }

  @Test
  public void schemeHost() throws ConversionException {
    final Object url = assertUrl("georss:https://host/");
  }

  @Test
  public void scheme2() throws ConversionException {
    final Object url = assertUrl("georss:https:/");
  }

  @Test
  public void schemeFragment1() throws ConversionException {
    final Object url = assertUrl("georss:https:///#foo");
  }

  @Test
  public void schemeHostFragment() throws ConversionException {
    final Object url = assertUrl("georss:https://host/#foo");
  }

  @Test
  public void schemeFragment2() throws ConversionException {
    final Object url = assertUrl("georss:https:/#foo");
  }

  @Test
  public void schemePath() throws ConversionException {
    final Object url = assertUrl("georss:https:///foo/bar");
  }

  @Test
  public void schemeHostPath() throws ConversionException {
    final Object url = assertUrl("georss:https://host/foo/bar");
  }

  @Test
  public void schemePathFragment() throws ConversionException {
    final Object url = assertUrl("georss:https:///foo/bar#foo");
  }

  @Test
  public void schemeHostPathFragment() throws ConversionException {
    final Object url = assertUrl("georss:https://host/foo/bar#foo");
  }

  @Test
  public void schemeQuery() throws ConversionException {
    final Object url = assertUrl("georss:https://?foo=bar&bar=foo");
  }

  @Test
  public void schemeHostQuery() throws ConversionException {
    final Object url = assertUrl("georss:https://host?foo=bar&bar=foo");
  }

  @Test
  public void schemeQueryFragment() throws ConversionException {
    final Object url = assertUrl("georss:https://?foo=bar&bar=foo#foo");
  }

  @Test
  public void schemeHostQueryFragment() throws ConversionException {
    final Object url = assertUrl("georss:https://host?foo=bar&bar=foo#foo");
  }

  @Test
  public void schemePathQuery() throws ConversionException {
    final Object url = assertUrl("georss:https:///foo/bar?foo=bar&bar=foo");
  }

  @Test
  public void schemeHostPathQuery() throws ConversionException {
    final Object url = assertUrl("georss:https://host/foo/bar?foo=bar&bar=foo");
  }

  @Test
  public void schemePathQueryFragment() throws ConversionException {
    final Object url = assertUrl("georss:https:///foo/bar?foo=bar&bar=foo#foo");
  }

  @Test
  public void schemePath2QueryFragment() throws ConversionException {
    final Object url = assertUrl("georss:https:///foo/bar/?foo=bar&bar=foo#foo");
  }

  @Test
  public void schemeHostPathQueryFragment() throws ConversionException {
    final Object url = assertUrl("georss:https://host/foo/bar?foo=bar&bar=foo#foo");
  }

  @Test
  public void schemeHostPortPathQueryFragment() throws ConversionException {
    final Object url = assertUrl("georss:https://host:80/foo/bar?foo=bar&bar=foo#foo");
  }

  @Test
  public void schemeHostPortPathQueryFragmentUrlValueDecoded() throws ConversionException {
    final Object url = assertUrl("georss:https://host:80/foo/bar?foo=http%3A%2F%2Fhost%3A6%2Ffo&bar=foo#foo");
  }

  @Test
  public void schemeHostPortPathQueryFragmentUrlValue() throws ConversionException {
    final Object url = assertUrl("georss:https://host:80/foo/bar?foo=http://host:6/fo&bar=foo#foo");
  }

  @Test
  public void schemeLoginHostPortPathQueryFragment() throws ConversionException {
    final Object url = assertUrl("georss:https://user:pass@host:80/foo/bar?foo=bar&bar=foo#foo");
  }

  @Test
  public void schemeLoginHostPortPath2QueryFragment() throws ConversionException {
    final Object url = assertUrl("georss:https://user:pass@host:80/foo/bar/?foo=bar&bar=foo#foo"); //$NON-NLS-1$
  }

  @Test
  public void schemeLoginHost2PortPathQueryFragment() throws ConversionException {
    final Object url = assertUrl("georss:https://user:pass@host.net:80/foo/bar?foo=bar&bar=foo#foo");
  }

  @Test
  public void schemeLoginHost3PortPathQueryFragment() throws ConversionException {
    final Object url = this.converter
        .convert("georss:https://user:pass@123.123.123.456:80/foo/bar?foo=bar&bar=foo#foo");
  }

  @Test
  public void schemeLoginHostPortPathQueryFragment0() throws ConversionException {
    final Object url = this.converter
        .convert("georss:https://user:pass@123.123.123.456:80/foäo/bar?foo=bar&bar20=foo#foo");
  }

  @Test
  public void schemeHostPathQueryFragment1() throws ConversionException {
    final Object url = assertUrl("https://host/foo/bar?foo=bar&bar=foo#foo");
  }

  @Test
  public void schemeHostPathQueryFragment2() throws ConversionException {
    final Object url = assertUrl("georss:atom:https://host/foo/bar?foo=bar&bar=fo o#fo%20o");
  }

  @Test
  public void port1() throws ConversionException {
    final Object url = assertUrl("https://host:/");
  }

  @Test(expected = ConversionException.class)
  public void port2() throws ConversionException {
    final Object url = assertUrl("https://host:a/");
  }

}
