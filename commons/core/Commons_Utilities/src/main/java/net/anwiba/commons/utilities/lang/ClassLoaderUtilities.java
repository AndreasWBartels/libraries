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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.resource.utilities.FileUtilities;
import net.anwiba.commons.resource.utilities.IFileExtensions;
import net.anwiba.commons.resource.utilities.IoUtilities;
import net.anwiba.commons.resource.utilities.UriUtilities;
import net.anwiba.commons.resource.utilities.UrlToUriConverter;
import net.anwiba.commons.utilities.ArrayUtilities;

public class ClassLoaderUtilities {

  private static ILogger logger = Logging.getLogger(ClassLoaderUtilities.class.getName());
  private static Map<URI, Manifest> manifests = new HashMap<>();

  public static void addToClassPath(final ClassLoader classLoader, final String name) throws IOException {
    addToClassPath(classLoader, new File(name));
  }

  public static void addToClassPath(final ClassLoader classLoader, final File path) throws IOException {
    if (path.isDirectory()) {
      final File[] files = listJarFiles(path);
      if (files != null && files.length != 0) {
        for (final File file : files) {
          addToClassPath(classLoader, file);
        }
        return;
      }
    }
    addToClassPath(classLoader, path.toURI().toURL());
  }

  private static File[] listJarFiles(final File path) {
    final File[] files = path.listFiles(new FileFilter() {

      @Override
      public boolean accept(final File file) {
        if (file.isDirectory()) {
          return false;
        }
        final String extension = FileUtilities.getExtension(file);
        return IFileExtensions.JAR.equalsIgnoreCase(extension) || IFileExtensions.ZIP.equalsIgnoreCase(extension);
      }
    });
    return files;
  }

  public static void addToClassPath(final ClassLoader classLoader, final URI uri) throws IOException {
    addToClassPath(classLoader, uri.toURL());
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static void addToClassPath(final ClassLoader classLoader, final URL url) throws IOException {
    if (!(classLoader instanceof URLClassLoader)) {
      throw new IOException("Error, could not add URL to system classloader"); //$NON-NLS-1$
    }
    final URLClassLoader sysloader = (URLClassLoader) classLoader;
    final Class sysclass = URLClassLoader.class;
    try {
      AccessController.doPrivileged(new PrivilegedAction() {
        @Override
        public Object run() {
          try {
            final Class[] parameters = new Class[]{ URL.class };
            Method method;
            method = sysclass.getDeclaredMethod("addURL", parameters); //$NON-NLS-1$
            method.setAccessible(true);
            method.invoke(sysloader, new Object[]{ url });
          } catch (final SecurityException exception) {
            throw new RuntimeException(exception);
          } catch (final NoSuchMethodException exception) {
            throw new RuntimeException(exception);
          } catch (final IllegalArgumentException exception) {
            throw new RuntimeException(exception);
          } catch (final IllegalAccessException exception) {
            throw new RuntimeException(exception);
          } catch (final InvocationTargetException exception) {
            throw new RuntimeException(exception);
          }
          return null;
        }
      });
    } catch (final RuntimeException t) {
      throw new IOException("Error, could not add URL to system classloader", t); //$NON-NLS-1$
    }
  }

  public static String getClassPath(final ClassLoader classLoader) {
    final URI[] classPathList = getClassPathUris(classLoader);
    final StringBuilder builder = new StringBuilder();
    for (final URI url : classPathList) {
      builder.append(" "); //$NON-NLS-1$
      builder.append(url.toString());
    }
    return builder.toString();
  }

  public static Manifest getManifest(final URI uri) {
    if (uri == null) {
      return null;
    }
    Manifest manifest;
    if ((manifest = manifests.get(uri)) != null) {
      return manifest;
    }
    try (JarInputStream jarInputStream = new JarInputStream(uri.toURL().openStream())) {
      if ((manifest = jarInputStream.getManifest()) != null) {
        manifests.put(uri, manifest);
      }
      return manifest;
    } catch (final IOException exception) {
      return null;
    }
  }

  private static Set<String> licenseFileNames = new HashSet<>();
  static {
    licenseFileNames.add("META-INF/LICENSE.txt"); //$NON-NLS-1$
    licenseFileNames.add("META-INF/LICENSE"); //$NON-NLS-1$
    licenseFileNames.add("META-INF/COPYING"); //$NON-NLS-1$
    licenseFileNames.add("license/LICENSE.txt"); //$NON-NLS-1$
    licenseFileNames.add("license/LICENSE"); //$NON-NLS-1$
    licenseFileNames.add("license.html"); //$NON-NLS-1$
    licenseFileNames.add("LICENSE.txt"); //$NON-NLS-1$
    licenseFileNames.add("LICENSE"); //$NON-NLS-1$
    licenseFileNames.add("COPYING"); //$NON-NLS-1$
  }

  public static String getLicense(final URI uri) {
    if (uri == null) {
      return null;
    }
    return getFirstFile(uri, licenseFileNames);
  }

  private static Set<String> readMeFileNames = new HashSet<>();
  static {
    readMeFileNames.add("META-INF/README.txt"); //$NON-NLS-1$
    readMeFileNames.add("META-INF/README"); //$NON-NLS-1$
    readMeFileNames.add("META-INF/NOTICE.txt"); //$NON-NLS-1$
    readMeFileNames.add("META-INF/NOTICE"); //$NON-NLS-1$
    readMeFileNames.add("license/README.txt"); //$NON-NLS-1$
    readMeFileNames.add("license/README"); //$NON-NLS-1$
    readMeFileNames.add("README.txt"); //$NON-NLS-1$
    readMeFileNames.add("README"); //$NON-NLS-1$
    readMeFileNames.add("NOTICE.txt"); //$NON-NLS-1$
    readMeFileNames.add("NOTICE"); //$NON-NLS-1$
  }

  public static String getReadMe(final URI uri) {
    if (uri == null) {
      return null;
    }
    return getFirstFile(uri, readMeFileNames);
  }

  private static Set<String> authorsNames = new HashSet<>();
  static {
    authorsNames.add("META-INF/AUTHORS.txt"); //$NON-NLS-1$
    authorsNames.add("META-INF/AUTHORS"); //$NON-NLS-1$
    authorsNames.add("AUTHORS.txt"); //$NON-NLS-1$
    authorsNames.add("AUTHORS"); //$NON-NLS-1$
  }

  public static String getAuthors(final URI uri) {
    if (uri == null) {
      return null;
    }
    return getFirstFile(uri, authorsNames);
  }

  private static Set<String> newsNames = new HashSet<>();
  static {
    newsNames.add("META-INF/NEWS.txt"); //$NON-NLS-1$
    newsNames.add("META-INF/NEWS"); //$NON-NLS-1$
    newsNames.add("NEWS.txt"); //$NON-NLS-1$
    newsNames.add("NEWS"); //$NON-NLS-1$
  }

  public static String getNews(final URI uri) {
    if (uri == null) {
      return null;
    }
    return getFirstFile(uri, newsNames);
  }

  private static String getFirstFile(final URI uri, final Set<String> fileNames) {
    try (JarInputStream jarInputStream = new JarInputStream(uri.toURL().openStream())) {
      try {
        JarEntry entry = jarInputStream.getNextJarEntry();
        while (entry != null) {
          if (entry.isDirectory()) {
            entry = jarInputStream.getNextJarEntry();
            continue;
          }
          final String name = entry.getName();
          if (fileNames.contains(name)) {
            return IoUtilities.toString(jarInputStream, "UTF-8", entry.getSize()); //$NON-NLS-1$
          }
          entry = jarInputStream.getNextJarEntry();
        }
      } catch (final NullPointerException exception) {
        // nothing to do
      }
      return null;
    } catch (final IOException exception) {
      return null;
    }
  }

  public static boolean contains(final URI library, final String resource) {
    if (library == null) {
      return false;
    }
    try (JarInputStream jarInputStream = new JarInputStream(library.toURL().openStream())) {
      try {
        JarEntry entry = jarInputStream.getNextJarEntry();
        while (entry != null) {
          if (entry.isDirectory()) {
            entry = jarInputStream.getNextJarEntry();
            continue;
          }
          final String name = entry.getName();
          if (resource.equals(name)) {
            return true;
          }
          entry = jarInputStream.getNextJarEntry();
        }
      } catch (final NullPointerException exception) {
        // nothing to do
      }
      return false;
    } catch (final IOException exception) {
      return false;
    }
  }

  public static URI[] getLibraries(final Manifest manifest, final String parent) {
    logger.log(ILevel.DEBUG, "parent: " + parent); //$NON-NLS-1$
    final Attributes attributes = manifest.getMainAttributes();
    final String classPath = attributes.getValue("Class-Path"); //$NON-NLS-1$
    if (classPath == null) {
      return new URI[0];
    }
    final List<URI> uris = new ArrayList<>();
    final StringTokenizer stringTokenizer = new StringTokenizer(classPath, " "); //$NON-NLS-1$
    while (stringTokenizer.hasMoreTokens()) {
      final String string = stringTokenizer.nextToken();
      try {
        logger.log(ILevel.DEBUG, "child: " + string); //$NON-NLS-1$
        final URI uri = URI.create(string);
        if (!uri.isAbsolute()) {
          try {
            final File file = new File(
                parent == null ? new File(System.getProperty("user.dir")) : new File(new URI(parent)), //$NON-NLS-1$
                string);
            logger.log(ILevel.DEBUG, "add none absolute file: " + file.getCanonicalFile().toURI()); //$NON-NLS-1$
            uris.add(file.getCanonicalFile().toURI());
          } catch (final IOException exception) {
            logger.log(ILevel.ALL, exception.getMessage(), exception);
            uris.add(new URI(string));
          }
          continue;
        }
        if ("file".equalsIgnoreCase(uri.getScheme())) { //$NON-NLS-1$
          try {
            logger.log(ILevel.DEBUG, "add file: " + string); //$NON-NLS-1$
            uris.add(new File(uri).getCanonicalFile().toURI());
          } catch (final IOException exception) {
            logger.log(Level.ALL, exception.getMessage(), exception);
            uris.add(new URI(string));
          }
          continue;
        }
        logger.log(ILevel.DEBUG, "add: " + string); //$NON-NLS-1$
        uris.add(uri);
      } catch (final URISyntaxException e) {
        logger.log(Level.ALL, e.getMessage(), e);
      }
    }
    return uris.toArray(new URI[uris.size()]);
  }

  public static URI[] getClassPathUris(final ClassLoader classLoader) {
    logger.log(ILevel.DEBUG, classLoader.getClass().toString());
    if (!(classLoader instanceof URLClassLoader)) {
      return new URI[0];
    }
    final URLClassLoader sysloader = (URLClassLoader) classLoader;
    final URI[] classPathList = ArrayUtilities.convert(new IConverter<URL, URI, RuntimeException>() {

      UrlToUriConverter converter = new UrlToUriConverter();

      @SuppressWarnings("synthetic-access")
      @Override
      public URI convert(final URL url) {
        try {
          final URI converted = this.converter.convert(url);
          logger.log(ILevel.DEBUG, MessageFormat.format("url {0} uri {1}", url, converted)); //$NON-NLS-1$
          return converted;
        } catch (final URISyntaxException exception) {
          logger.log(ILevel.DEBUG, exception.getLocalizedMessage(), exception);
          return null;
        }
      }
    }, sysloader.getURLs(), URI.class);
    return ArrayUtilities.normalize(classPathList);
  }

  public static URI[] getLibraries(final ClassLoader classLoader) {
    final URI[] classPathUrls = getClassPathUris(classLoader);
    if (classPathUrls.length == 0) {
      return classPathUrls;
    }
    final List<URI> libraryList = new ArrayList<>();
    for (final URI uri : classPathUrls) {
      logger.log(ILevel.DEBUG, uri.toString());
      final String string = uri.toString();
      final int length = string.length();
      if (string.substring(length - 4 < 0 ? 0 : length - 4, length).equalsIgnoreCase("." + IFileExtensions.JAR)) { //$NON-NLS-1$
        logger.log(ILevel.DEBUG, "add: " + uri.toString()); //$NON-NLS-1$
        libraryList.add(uri);
      }
    }
    final List<URI> subLibraryList = new ArrayList<>();
    for (final URI uri : libraryList) {
      final Manifest manifest = getManifest(uri);
      if (manifest == null) {
        continue;
      }
      final URI[] classPaths = getLibraries(manifest, getParent(uri));
      for (final URI classPath : classPaths) {
        logger.log(ILevel.DEBUG, classPath.toString());
        if (libraryList.contains(classPath)) {
          continue;
        }
        if (classPath.isAbsolute() && "file".equalsIgnoreCase(classPath.getScheme()) && !new File(classPath).exists()) { //$NON-NLS-1$
          continue;
        }
        logger.log(ILevel.DEBUG, "add: " + classPath.toString()); //$NON-NLS-1$
        subLibraryList.add(classPath);
      }
    }
    libraryList.addAll(subLibraryList);
    return libraryList.toArray(new URI[libraryList.size()]);
  }

  public static String getParent(final URI uri) {
    if (uri.isAbsolute()) {
      if ("file".equalsIgnoreCase(uri.getScheme())) { //$NON-NLS-1$
        return new File(uri).getParentFile().toURI().toASCIIString();
      }
    }
    return UriUtilities.getParentUri(uri).toASCIIString();
  }

  public static File getParentFile(final String folder) {
    final String classpath = System.getProperty("java.class.path"); //$NON-NLS-1$
    final StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);
    if (tokenizer.hasMoreTokens()) {
      final String pathName = tokenizer.nextToken();
      final File path = new File(pathName);
      return path.getParentFile();
    }
    if (folder != null) {
      return new File(new File(System.getProperty("user.home")), folder); //$NON-NLS-1$
    }
    return new File(System.getProperty("user.dir")); //$NON-NLS-1$
  }

  public static File getFile(final String name, final String folder) {
    final String classpath = System.getProperty("java.class.path"); //$NON-NLS-1$
    final StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);
    if (tokenizer.hasMoreTokens()) {
      final String pathName = tokenizer.nextToken();
      final File path = new File(pathName);
      if (new File(path.getParentFile(), name).exists()) {
        return new File(path.getParentFile(), name);
      }
    }
    final File file = new File(new File(System.getProperty("user.dir")), name); //$NON-NLS-1$
    if (file.exists()) {
      return file;
    }
    if (folder != null) {
      return new File(new File(new File(System.getProperty("user.home")), folder), name); //$NON-NLS-1$
    }
    return file;
  }

  public static File getFile(final String name) {
    return getFile(name, null);
  }

}
