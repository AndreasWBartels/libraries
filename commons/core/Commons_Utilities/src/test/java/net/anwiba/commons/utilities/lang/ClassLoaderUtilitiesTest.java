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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import net.anwiba.commons.utilities.ArrayUtilities;
import net.anwiba.commons.utilities.io.TestJarProvider;
import net.anwiba.commons.utilities.lang.ClassLoaderUtilities;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;

public class ClassLoaderUtilitiesTest {

  public static final class PrivilegedURLClassLoaderAction implements PrivilegedAction<ClassLoader> {
    @Override
    public ClassLoader run() {
      return new URLClassLoader(new URL[0]);
    }
  }

  private static final class Contains<T> extends BaseMatcher<T> {

    private final T[] array;

    public Contains(final T[] array) {
      this.array = array;
    }

    @Override
    public void describeTo(final Description arg0) {
      // nothing to do
    }

    @Override
    public boolean matches(final Object value) {
      return ArrayUtilities.contains(this.array, value);
    }
  }

  private File jarFile;
  private File jarFolder;
  private URI jarFileUri;

  @Before
  public void setup() {
    this.jarFile = TestJarProvider.getJar(getClass().getClassLoader());
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
  public void addFileToClassPath() throws IOException {
    final ClassLoader classLoader = createClassLoader();
    ClassLoaderUtilities.addToClassPath(classLoader, this.jarFile);
    assertThat(this.jarFileUri, new Contains<>(ClassLoaderUtilities.getLibraries(classLoader)));
  }

  @Test
  public void addFolderToClassPath() throws IOException {
    final ClassLoader classLoader = createClassLoader();
    ClassLoaderUtilities.addToClassPath(classLoader, this.jarFolder);
    final URI[] libraries = ClassLoaderUtilities.getLibraries(classLoader);
    assertThat(new File(this.jarFolder, "test.jar").toURI(), new Contains<>(libraries)); //$NON-NLS-1$
  }

  @Test
  public void license() {
    final String license = ClassLoaderUtilities.getLicense(new File(this.jarFolder, "test.jar").toURI()); //$NON-NLS-1$
    assertNotNull(license);
  }

  private ClassLoader createClassLoader() {
    return AccessController.doPrivileged(new PrivilegedURLClassLoaderAction());
  }

}
