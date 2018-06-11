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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;

@SuppressWarnings("nls")
public class UriUtilities {

  private static ILogger logger = Logging.getLogger(UriUtilities.class.getName());
  public static final String ERROR_MESSAGE = "Found illegal pattern in resource location string: {0}"; //$NON-NLS-1$
  public final static UriToUrlConverter converter = new UriToUrlConverter();

  public static URI changeUriExtension(final URI base, final String descriptionFile, final String dataFile) {
    try {
      return new URI(
          base.getScheme(),
          base.getUserInfo(),
          base.getHost(),
          base.getPort(),
          createDataFilePath(base.getPath(), descriptionFile, dataFile),
          base.getQuery(),
          base.getFragment());
    } catch (final URISyntaxException exception) {
      throw new RuntimeException("Unreachable code reached"); //$NON-NLS-1$
    }
  }

  private static String createDataFilePath(final String path, final String descriptionFile, final String dataFile) {
    return StringUtilities.removeEqualEnd(path, descriptionFile) + dataFile;
  }

  public static String getNamenFromUriWithoutExtension(final URI uri) {
    if (uri == null) {
      return null;
    }
    final String name = getName(uri);
    final String subString = StringUtilities.getStringBeforLastChar(name, '.');
    return subString == null ? null : subString.toLowerCase();
  }

  public static String getName(final URI uri) {
    if (uri == null) {
      return null;
    }
    return StringUtilities.getStringAfterLastChar(uri.getPath(), '/');
  }

  public static String getQuery(final URI uri) {
    if (uri == null) {
      return null;
    }
    if (!uri.isOpaque()) {
      return uri.getQuery();
    }
    final String string = uri.toString();
    StringBuilder builder = null;
    for (final char c : string.toCharArray()) {
      switch (c) {
        case '?': {
          builder = new StringBuilder();
          continue;
        }
        default: {
          if (builder == null) {
            continue;
          }
          builder.append(c);
        }
      }
    }
    if (builder == null) {
      return null;
    }
    return builder.toString();
  }

  public static String getPath(final URI uri) {
    if (uri == null) {
      return null;
    }
    if (!uri.isOpaque()) {
      return uri.getPath();
    }
    if (uri.isAbsolute()) {
      if (uri.getSchemeSpecificPart() != null) {
        getPath(URI.create(uri.getSchemeSpecificPart()));
      }
    }
    final String string = uri.toString();
    StringBuilder builder = null;
    for (final char c : string.toCharArray()) {
      switch (c) {
        case ':': {
          builder = new StringBuilder();
          continue;
        }
        case '?': {
          if (builder == null) {
            return null;
          }
          return builder.toString();
        }
        default: {
          if (builder == null) {
            continue;
          }
          builder.append(c);
        }
      }
    }
    if (builder == null) {
      return null;
    }
    return builder.toString();
  }

  public static String getExtension(final URI uri) {
    if (uri == null) {
      return null;
    }
    if (!uri.isOpaque()) {
      final String subString = StringUtilities.getStringAfterLastChar(uri.getPath(), '.');
      return subString.toLowerCase();
    }
    final String string = uri.toString();
    StringBuilder builder = null;
    for (final char c : string.toCharArray()) {
      switch (c) {
        case '.': {
          builder = new StringBuilder();
          continue;
        }
        case '?': {
          if (builder == null) {
            return null;
          }
          return builder.toString().toLowerCase();
        }
        default: {
          if (builder == null) {
            continue;
          }
          builder.append(c);
        }
      }
    }
    if (builder == null) {
      return null;
    }
    return builder.toString().toLowerCase();
  }

  public static URI getParentUri(final URI base) {
    if (base == null) {
      return null;
    }
    try {
      final String path = getParentFromPath(base.getPath());
      return new URI(
          base.getScheme(),
          base.getUserInfo(),
          base.getHost(),
          base.getPort(),
          path,
          base.getQuery(),
          base.getFragment());
    } catch (final URISyntaxException exception) {
      throw new RuntimeException("Unreachable code reached", exception); //$NON-NLS-1$
    }
  }

  private static String getParentFromPath(final String path) {
    if (path == null || path.length() == 0) {
      return ""; //$NON-NLS-1$
    }
    if (path.equals("/")) { //$NON-NLS-1$
      return path;
    }
    final int index = path.lastIndexOf('/');
    if (index == 0) {
      return "/"; //$NON-NLS-1$
    }
    if (path.endsWith("/")) { //$NON-NLS-1$
      return getParentFromPath(path.substring(0, index - 1));
    }
    return path.substring(0, index);
  }

  public static boolean isChild(final String path) {
    return !(path == null
        || path.trim().length() == 0
        || path.trim().startsWith("/") //$NON-NLS-1$
        || path.trim().startsWith("\\") //$NON-NLS-1$
        || path.contains(":"));//$NON-NLS-1$
  }

  public static URI setPath(final URI base, final String path) {
    try {
      if (base == null) {
        return new URI(path);
      }
      return new URI(
          base.getScheme(),
          base.getUserInfo(),
          base.getHost(),
          base.getPort(),
          path,
          base.getQuery(),
          base.getFragment());
    } catch (final URISyntaxException exception) {
      throw new RuntimeException("Unreachable code reached", exception); //$NON-NLS-1$
    }
  }

  public static URI concat(final URI base, final String text) {
    try {
      if (base == null) {
        return new URI(concat("", text)); //$NON-NLS-1$
      }
      return new URI(
          base.getScheme(),
          base.getUserInfo(),
          base.getHost(),
          base.getPort(),
          concat(base.getPath(), text),
          base.getQuery(),
          base.getFragment());
    } catch (final URISyntaxException exception) {
      throw new RuntimeException("Unreachable code reached", exception); //$NON-NLS-1$
    }
  }

  private static String concat(final String path, final String child) {
    if (child == null || child.trim().length() == 0) {
      return path;
    }
    return path + "/" + child; //$NON-NLS-1$
  }

  public static boolean isFileUri(final URI uri) {
    return uri != null && (uri.getScheme() == null || uri.getScheme().equalsIgnoreCase("file")); //$NON-NLS-1$
  }

  public static boolean canRead(final URI uri) {
    try {
      if (UriUtilities.isFileUri(uri)) {
        return new File(uri).canRead();
      }
      try (InputStream stream = openInputStream(uri)) {
        return stream != null;
      }
    } catch (final IOException exception) {
      return false;
    }
  }

  public static boolean canWrite(final URI uri) {
    try {
      if (UriUtilities.isFileUri(uri)) {
        return new File(uri).canWrite();
      }
      try (OutputStream stream = openOutputStream(uri)) {
        return true;
      }
    } catch (final IOException exception) {
      return false;
    }
  }

  public static boolean exist(final URI uri) {
    if (isFileUri(uri)) {
      return new File(uri).exists();
    }
    try {
      final URLConnection connection = converter.convert(uri).openConnection();
      connection.connect();
      return true;
    } catch (final Throwable exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      return false;
    }
  }

  public static InputStream openInputStream(final URI uri) throws IOException {
    if (UriUtilities.isFileUri(uri)) {
      return new FileInputStream(new File(uri));
    }
    return converter.convert(uri).openStream();
  }

  public static OutputStream openOutputStream(final URI uri) throws IOException {
    if (UriUtilities.isFileUri(uri)) {
      return new FileOutputStream(new File(uri));
    }
    try {
      final URLConnection connection = converter.convert(uri).openConnection();
      return connection.getOutputStream();
    } catch (final MalformedURLException exception) {
      throw new IOException(exception);
    }
  }

  public static long getContentLength(final URI uri) {
    try {
      if (UriUtilities.isFileUri(uri)) {
        return new File(uri).length();
      }
      final URLConnection connection = converter.convert(uri).openConnection();
      return connection.getContentLength();
    } catch (final IOException exception) {
      logger.log(ILevel.WARNING, exception.getLocalizedMessage());
      return -1l;
    }
  }

  public static void assertMatchUrlPathPattern(final String pathString) {
    final Pattern pattern = Pattern.compile("^\\p{Alnum}*\\:(/|\\\\){1,2}\\p{Alnum}*");//Entspricht Pattern: ^\p{Alnum}*\:(/|\){1,2}\p{Alnum}* //$NON-NLS-1$
    final Matcher matcher = pattern.matcher(pathString);
    if (!matcher.find()) {
      throw new IllegalArgumentException(MessageFormat.format(ERROR_MESSAGE, pathString));
    }
  }

  public static boolean isFileUrl(final String pathString) {
    final String string = pathString.toLowerCase();
    if (string.startsWith("file:")) {
      final String filePath = pathString.substring(pathString.indexOf(':') + 1, pathString.length());
      if (filePath.startsWith("./") || filePath.startsWith("../")) {
        return true;
      }
      try {
        assertMatchUrlPathPattern(string);
      } catch (final IllegalArgumentException exception) {
        return false;
      }
      return true;
    }
    return false;
  }

  public static boolean isHttpUrl(final String pathString) {
    final String string = pathString.toLowerCase();
    try {
      assertMatchUrlPathPattern(string);
    } catch (final IllegalArgumentException exception) {
      return false;
    }
    return string.startsWith("http://") || string.startsWith("https://");
  }

  public static URI create(final String urn) {
    if (isFileUrl(urn)) {
      return !URI.create(urn).isOpaque() ? URI.create(urn) : new File(URI.create(urn).getSchemeSpecificPart()).toURI();
    }
    if (isHttpUrl(urn)) {
      return URI.create(urn);
    }
    return new File(urn).toURI();
  }
}
