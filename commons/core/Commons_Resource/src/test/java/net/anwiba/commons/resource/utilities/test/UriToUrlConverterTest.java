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
package net.anwiba.commons.resource.utilities.test;

import net.anwiba.commons.resource.utilities.UriToUrlConverter;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

import static org.junit.Assert.*;

public class UriToUrlConverterTest {

  final UriToUrlConverter converter = new UriToUrlConverter();

  @Test
  public void testNull() throws MalformedURLException {
    assertThat(this.converter.convert(null), nullValue());
  }

  @SuppressWarnings("nls")
  @Test
  public void testFile() throws MalformedURLException, URISyntaxException {
    final URL url = this.converter.convert(new URI("file:/C:/Progam%20Files/datei.txt"));
    assertThat(url.toString(), equalTo("file:/C:/Progam%20Files/datei.txt"));
  }

  @SuppressWarnings({ "nls" })
  @Test
  public void testHttp() throws MalformedURLException, URISyntaxException {
    final URL url =
        this.converter.convert(new URI("http://user:password@host.domain.local:2000/Progam%20Files/datei.txt"));
    assertThat(url.toString(), equalTo("http://user:password@host.domain.local:2000/Progam%20Files/datei.txt"));
  }

}