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
package net.anwiba.commons.utilities.resource.test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import net.anwiba.commons.reference.FileResourceReference;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.ResourceReferenceFactory;
import net.anwiba.commons.reference.ResourceReferenceUtilities;
import net.anwiba.commons.reference.UrlResourceReference;
import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.commons.utilities.OperationSystemUtilities;

import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("nls")
public class ResourceFactoryTest {
  private final ResourceReferenceFactory factory = new ResourceReferenceFactory();

  @Test
  public void UncResource() throws Exception {
    final IResourceReference resourceReference = this.factory.create("//dumpsrv/export/Betroffeneflaeche.shp");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(FileResourceReference.class));
    if (OperationSystemUtilities.isUnix()) {
      assertThat(ResourceReferenceUtilities.getUrl(resourceReference).toExternalForm(), equalTo(new URL(
          "file:/dumpsrv/export/Betroffeneflaeche.shp").toExternalForm()));
      assertThat(resourceReference.toString(), equalTo("/dumpsrv/export/Betroffeneflaeche.shp"));
      return;
    }
    assertThat(resourceReference.toString(), equalTo("\\\\dumpsrv\\export\\Betroffeneflaeche.shp"));
    // assertConnection(resource);
  }

  @Test
  public void UncUrlResource() throws Exception {
    final IResourceReference resourceReference = this.factory.create("file://dumpsrv/export/Betroffeneflaeche.shp");
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(UrlResourceReference.class));
    assertThat(resourceReference.toString(), equalTo("file://dumpsrv/export/Betroffeneflaeche.shp"));
    if (OperationSystemUtilities.isUnix()) {
      return;
    }
    // assertConnection(resource);
  }

  @Test
  public void UncUrlResourceDoubleSlash() throws Exception {
    final IResourceReference resourceReference = this.factory.create("file:////dumpsrv/export/Betroffeneflaeche.shp");
    if (OperationSystemUtilities.isUnix()) {
      assertThat(resourceReference.toString(), equalTo("file:/dumpsrv/export/Betroffeneflaeche.shp"));
      return;
    }
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(UrlResourceReference.class));
    assertThat(resourceReference.toString(), equalTo("file://dumpsrv/export/Betroffeneflaeche.shp"));
    // assertConnection(resource);
  }

  @Test
  @Ignore
  public void UncUrlResourceWithBackslashes() throws Exception {
    final IResourceReference resourceReference = this.factory.create("file:\\\\dumpsrv\\export\\Betroffeneflaeche.shp");
    if (OperationSystemUtilities.isUnix()) {
      assertThat(resourceReference.toString(), equalTo("file:/dumpsrv/export/Betroffeneflaeche.shp"));
      return;
    }
    assertThat(resourceReference, notNullValue());
    assertThat(resourceReference, instanceOf(UrlResourceReference.class));
    assertThat(resourceReference.toString(), equalTo("file://dumpsrv/export/Betroffeneflaeche.shp"));
    // assertConnection(resource);
  }

  @SuppressWarnings("unused")
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