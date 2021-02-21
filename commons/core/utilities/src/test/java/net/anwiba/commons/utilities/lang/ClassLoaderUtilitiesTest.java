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
package net.anwiba.commons.utilities.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.PrivilegedAction;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.anwiba.commons.utilities.io.TestJarProvider;

public class ClassLoaderUtilitiesTest {

  public static final class PrivilegedURLClassLoaderAction implements PrivilegedAction<ClassLoader> {
    @Override
    public ClassLoader run() {
      return new URLClassLoader(new URL[0]);
    }
  }

  private File jarFile;
  private File jarFolder;
  private URI jarFileUri;

  @BeforeEach
  public void setup() {
    this.jarFile = TestJarProvider.getJar(this.getClass(), "test.jar");
    this.jarFolder = this.jarFile.getParentFile();
    this.jarFileUri = this.jarFile.toURI();
  }

  @Test
  public void manifest() throws Exception {
    final Manifest manifest = createManifest(this.jarFileUri);
    assertTrue(manifest != null);
  }

  private Manifest createManifest(final URI manifestUri) {
    return ClassLoaderUtilities.getManifest(manifestUri);
  }

  private Manifest createManifest() {
    final Manifest manifest = new Manifest();
    final Attributes attributes = manifest.getMainAttributes();
    attributes.put(new Attributes.Name("Class-Path"), "lib/harry.jar lib/hirsch.jar"); //$NON-NLS-1$ //$NON-NLS-2$
    return manifest;
  }

  @Test
  public void libraries() throws Exception {
    final Manifest manifest = createManifest();
    final URI[] libraries = ClassLoaderUtilities.getLibraries(manifest, new File("/parent").toURI().toString()); //$NON-NLS-1$
    assertTrue(libraries.length == 2);
    assertTrue("file".equals(libraries[0].getScheme())); //$NON-NLS-1$
  }

  @Test
  public void getParent() throws Exception {
    assertEquals("file:/parent", ClassLoaderUtilities.getParent(new URI("file:/parent/child"))); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("/parent", ClassLoaderUtilities.getParent(new URI("/parent/child"))); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("file:/parent", ClassLoaderUtilities.getParent(new File("/parent/child").toURI())); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Test
  public void license() {
    final String license = ClassLoaderUtilities.getLicense(new File(this.jarFolder, "test.jar").toURI()); //$NON-NLS-1$
    assertNotNull(license);
  }

}
