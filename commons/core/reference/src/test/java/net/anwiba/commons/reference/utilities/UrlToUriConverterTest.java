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

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.Test;

public class UrlToUriConverterTest {

  final UrlToUriConverter converter = new UrlToUriConverter();

  @SuppressWarnings({ "nls", "deprecation" })
  @Test
  public void testFile() throws MalformedURLException, URISyntaxException {
    final URI uri = this.converter.convert(new URL("file:/C:/Progam Files/datei.txt"));
    assertThat(uri.toString(), equalTo("file:/C:/Progam%20Files/datei.txt"));
    final File file = new File(uri);
    final URI uri2 = file.toURI();
    assertThat(uri2.toString(), equalTo("file:/C:/Progam%20Files/datei.txt"));
    assertThat(uri2.toURL().toString(), equalTo("file:/C:/Progam%20Files/datei.txt"));
    final URL url = file.toURL();
    assertThat(url.toString(), equalTo("file:/C:/Progam Files/datei.txt"));
  }

  @SuppressWarnings("nls")
  @Test
  public void testHttp() throws MalformedURLException, URISyntaxException {
    final URI uri =
        this.converter.convert(new URL("http://user:password@host.domain.local:2000/Progam Files/datei.txt"));
    assertThat(uri.toString(), equalTo("http://user:password@host.domain.local:2000/Progam%20Files/datei.txt"));
  }

  @Test
  public void testNull() throws URISyntaxException {
    assertThat(this.converter.convert(null), nullValue());
  }

}