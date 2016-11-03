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

import net.anwiba.commons.resource.reference.FileResourceReference;
import net.anwiba.commons.resource.reference.IResourceReference;
import net.anwiba.commons.resource.reference.ResourceReferenceFactory;
import net.anwiba.commons.resource.reference.ResourceReferenceUtilities;
import net.anwiba.commons.resource.reference.UriResourceReference;
import net.anwiba.commons.resource.reference.UrlResourceReference;
import net.anwiba.commons.resource.utilities.IoUtilities;

import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("nls")
public class ResourceReferenceFactoryTest {
  private final ResourceReferenceFactory factory = new ResourceReferenceFactory();

  @Test(expected = IllegalArgumentException.class)
  public void nullValueTest() throws Exception {
    this.factory.create((String) null);
    this.factory.create((File) null);
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
    final IResourceReference resourceReference = this.factory.create("file:../"
        + parent
        + "/src/test/resources/net/anwiba/commons/resource/provider/test/text.txt");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(UriResourceReference.class));
    assertThat(ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(), equalTo(new File("../"
        + parent
        + "/src/test/resources/net/anwiba/commons/resource/provider/test/text.txt").toURI().toURL().toExternalForm()));
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
    assertThat(ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(), equalTo(new File(
        "./src/main/java/net/anwiba/commons/resource/reference/IResourceReference.java")
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
    assertThat(ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(), equalTo(new File(
        "/g:/gisdata/projekte/Cadenza-Karte/CadenzaBundeslaender").toURI().toURL().toExternalForm()));
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
    assertThat(ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(), equalTo(new URL(
        "http://temp/hallo.txt").toExternalForm()));
  }

  @Test
  public void httpsUrlResource() throws Exception {
    final IResourceReference resourceReference = this.factory.create("https://temp/hallo.txt");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(UrlResourceReference.class));
    assertThat(ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(), equalTo(new URL(
        "https://temp/hallo.txt").toExternalForm()));
  }

  @Test
  public void fileResourceWithWhiteSpace() throws Exception {
    final IResourceReference resourceReference = this.factory
        .create("file:/C:/Program Files (x86)/data/./gisdata/Deutschland/Uebersicht-Landesweit/d2500_gk3.icat");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(UrlResourceReference.class));
    assertThat(ResourceReferenceUtilities.getUrl(resourceReference).toString(), equalTo(new URL(
        "file:/C:/Program Files (x86)/data/./gisdata/Deutschland/Uebersicht-Landesweit/d2500_gk3.icat").toString()));
    // assertConnection(resource);
  }

  @Test
  public void fileUrlResource() throws Exception {
    final IResourceReference resourceReference = this.factory.create("file://temp/hallo.txt");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(UrlResourceReference.class));
    assertThat(ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(), equalTo(new URL(
        "file://temp/hallo.txt").toExternalForm()));
  }

  @Test
  public void UrlCaseSensitiveResource() throws Exception {
    final IResourceReference resourceReference = this.factory.create("HTTP://temp/hallo.txt");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(UrlResourceReference.class));
    assertThat(ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(), equalTo(new URL(
        "HTTP://temp/hallo.txt").toExternalForm()));
  }

  @Test
  public void UncResource() throws Exception {
    final IResourceReference resourceReference = this.factory.create("//dumpsrv/export/Betroffeneflaeche.shp");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(FileResourceReference.class));
    assertThat(ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(), equalTo(new URL(
        "file:/dumpsrv/export/Betroffeneflaeche.shp").toExternalForm()));
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