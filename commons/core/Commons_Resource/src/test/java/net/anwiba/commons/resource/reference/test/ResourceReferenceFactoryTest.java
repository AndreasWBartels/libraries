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
package net.anwiba.commons.resource.reference.test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;

import net.anwiba.commons.reference.FileResourceReference;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.MemoryResourceReference;
import net.anwiba.commons.reference.ResourceReferenceFactory;
import net.anwiba.commons.reference.ResourceReferenceUtilities;
import net.anwiba.commons.reference.UriResourceReference;
import net.anwiba.commons.reference.UrlResourceReference;
import net.anwiba.commons.reference.utilities.IoUtilities;

@SuppressWarnings("nls")
public class ResourceReferenceFactoryTest {
  private final ResourceReferenceFactory factory = new ResourceReferenceFactory();

  @Test(expected = IllegalArgumentException.class)
  public void nullValueTest() throws Exception {
    this.factory.create((String) null);
    this.factory.create((File) null);
  }

  @Test
  public void dataResource() throws Exception {
    final IResourceReference resourceReference = this.factory.create(
        "data:image/png;charset=UTF-8;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAK3RFWHRDcmVhdGlvbiBUaW1lAERpIDE5IFNlcCAyMDE3IDExOjU3OjM3ICswMTAwN18QagAAAAd0SU1FB+EJEw0cEdpvFnsAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAAEZ0FNQQAAsY8L/GEFAAACEklEQVR42o2TPU/bUBSG74JYIhArMPAfkBhYmRAbiBV+ABNsfEggIWBDLEgIsSPCAiJBjR0bEzfGTojBNqhDC60aCqoqFKlqCyiQt+fcOFFM2qrDI/v6+rzv+bhXFAoVQaAK/pPa/xVRD3Zd4OKCKf8Tz4uKiFqwbZewtORgZibA7Kz/R6anPayvuzg7K9dFpMD5OXBwcAchTCJHnIbwez6y7u42YFn30jSSgeM8YmMjh8XFFPr7k4jF+P0NJiYUCnQwNhbH8rKO7e13YfArAc6Ce+A439HXp1NABkHwAtP8htZWE1NTx3Lf90ElNAhwcD7/E+l0EZr2BZubnHIW8/MKjo5ukEp9wPCwip4eE/v7l1DVG/p+S6ZlKSQM4zPGxy20tZ2gqytLbjYJuOjosNHZaVHNFtrbq32IxbLyW0uLJcvxvDKEolxhcFAPG2gRBeIkXDdyGjb0rWRuzqQMniC49kzmHsnkNdbWNNq0sbBg4PDwiiZTJZF4j5GRNDnr2Nkp0N5HKuFR9k42kWsJAmBlhR2OKeBaNovFGd7b2rKl8+7upVw3TcFxShgYSGBoKI1c7kd9zjUDTftEAjqlrpDoS/NB2tsrSvfVVUO6Nx5XFnPdXxgdNdDbq9DUStGDxA6q+hWTk6Z8VudcicAm8XiRjrRNGT5EBWouvv8cST0KaGws9Nx0mUTzVa78VeT1df4N8EUWrSNZCekAAAAASUVORK5CYII=");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(MemoryResourceReference.class));
  }

  @Test
  public void windowsDriveFileResource() throws Exception {
    final IResourceReference resourceReference = this.factory.create("c:/temp/hallo.txt");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(FileResourceReference.class));
  }

  @Test
  public void linuxDriveFileResource() throws Exception {
    final IResourceReference resourceReference = this.factory.create("/tmp/hallo.txt");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(FileResourceReference.class));
  }

  @Test
  public void relativeBackStepFileUrlResource() throws Exception {
    final String parent = new File(".").getAbsoluteFile().getParentFile().getName();
    final IResourceReference resourceReference = this.factory
        .create("file:../" + parent + "/src/test/resources/net/anwiba/commons/resource/provider/test/text.txt");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(UriResourceReference.class));
    assertThat(
        ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(),
        equalTo(
            new File("../" + parent + "/src/test/resources/net/anwiba/commons/resource/provider/test/text.txt")
                .toURI()
                .toURL()
                .toExternalForm()));
    assertConnection(resourceReference);
  }

  @Test
  @Ignore
  // TODO delete ignore
  public void relativeFileUrlResource() throws Exception {
    final IResourceReference resourceReference = this.factory
        .create("file:./src/main/java/net/anwiba/commons/resource/reference/IResourceReference.java");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(UriResourceReference.class));
    assertThat(
        ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(),
        equalTo(
            new File("./src/main/java/net/anwiba/commons/resource/reference/IResourceReference.java")
                .toURI()
                .toURL()
                .toExternalForm()));
    assertConnection(resourceReference);
  }

  @Test
  public void absoluteWindowsFileUrlResource() throws Exception {
    final IResourceReference resourceReference = this.factory
        .create("file:/g:/gisdata/projekte/Cadenza-Karte/CadenzaBundeslaender");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(UrlResourceReference.class));
    assertThat(
        ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(),
        equalTo(new File("/g:/gisdata/projekte/Cadenza-Karte/CadenzaBundeslaender").toURI().toURL().toExternalForm()));
  }

  // @Test
  // public void absoluteWindowsFileUrlResourceWithoutLeadingSlash() throws Exception {
  // final IResourceReference resourceReference =
  // this.factory.create("file:g:/gisdata/projekte/Cadenza-Karte/CadenzaBundeslaender");
  // assertThat(resourceReference, notNullValue());
  // assertThat(resourceReference, instanceOf(UrlResourceReference.class));
  // assertThat(ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(), equalTo(new File(
  // "/g:/gisdata/projekte/Cadenza-Karte/CadenzaBundeslaender").toURI().toURL().toExternalForm()));
  // }

  @Test
  public void relativeFileResource() throws Exception {
    final IResourceReference resourceReference = this.factory.create("file/hallo.txt");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(FileResourceReference.class));
    assertThat(
        ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(),
        equalTo(new File("file/hallo.txt").toURI().toURL().toExternalForm()));
  }

  @Test
  public void httpUrlResource() throws Exception {
    final IResourceReference resourceReference = this.factory.create("http://temp/hallo.txt");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(UrlResourceReference.class));
    assertThat(
        ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(),
        equalTo(new URL("http://temp/hallo.txt").toExternalForm()));
  }

  @Test
  public void httpsUrlResource() throws Exception {
    final IResourceReference resourceReference = this.factory.create("https://temp/hallo.txt");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(UrlResourceReference.class));
    assertThat(
        ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(),
        equalTo(new URL("https://temp/hallo.txt").toExternalForm()));
  }

  @Test
  public void fileResourceWithWhiteSpace() throws Exception {
    final IResourceReference resourceReference = this.factory
        .create("file:/C:/Program Files (x86)/data/./gisdata/Deutschland/Uebersicht-Landesweit/d2500_gk3.icat");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(UrlResourceReference.class));
    assertThat(
        ResourceReferenceUtilities.getUrl(resourceReference).toString(),
        equalTo(
            new URL("file:/C:/Program Files (x86)/data/./gisdata/Deutschland/Uebersicht-Landesweit/d2500_gk3.icat")
                .toString()));
    // assertConnection(resource);
  }

  @Test
  public void fileUrlResource() throws Exception {
    final IResourceReference resourceReference = this.factory.create("file://temp/hallo.txt");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(UrlResourceReference.class));
    assertThat(
        ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(),
        equalTo(new URL("file://temp/hallo.txt").toExternalForm()));
  }

  @Test
  public void UrlCaseSensitiveResource() throws Exception {
    final IResourceReference resourceReference = this.factory.create("HTTP://temp/hallo.txt");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(UrlResourceReference.class));
    assertThat(
        ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(),
        equalTo(new URL("HTTP://temp/hallo.txt").toExternalForm()));
  }

  @Test
  public void UncResource() throws Exception {
    final IResourceReference resourceReference = this.factory.create("//dumpsrv/export/Betroffeneflaeche.shp");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(FileResourceReference.class));
    assertThat(
        ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(),
        equalTo(new URL("file:/dumpsrv/export/Betroffeneflaeche.shp").toExternalForm()));
    assertThat(resourceReference.toString(), equalTo("/dumpsrv/export/Betroffeneflaeche.shp"));
  }

  @Test
  public void UncUrlResource() throws Exception {
    final IResourceReference resourceReference = this.factory.create("file:////dumpsrv/export/Betroffeneflaeche.shp");
    assertThat(resourceReference.toString(), equalTo("file:/dumpsrv/export/Betroffeneflaeche.shp"));
  }

  @Test
  @Ignore
  public void UncUrlResourceWithBackslashes() throws Exception {
    final IResourceReference resourceReference = this.factory.create("file:\\\\dumpsrv\\export\\Betroffeneflaeche.shp");
    assertThat(resourceReference.toString(), equalTo("file:/dumpsrv/export/Betroffeneflaeche.shp"));
  }

  private void assertConnection(final IResourceReference resourceReference) throws MalformedURLException, IOException {
    InputStream stream = null;
    try {
      stream = ResourceReferenceUtilities.getUrl(resourceReference).openStream();
      assertThat(stream, notNullValue());
    } finally {
      IoUtilities.close(stream);
    }
  }

}
