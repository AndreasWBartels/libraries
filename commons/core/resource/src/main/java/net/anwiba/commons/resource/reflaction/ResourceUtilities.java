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
package net.anwiba.commons.resource.reflaction;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

import net.anwiba.commons.lang.functional.IBiFunction;

public class ResourceUtilities {

  public static URL getUrl(final IBiFunction<Class, String, URL, IOException> helper,
      final Class clazz,
      final String resourceUrl)
      throws IOException {
    if (resourceUrl.startsWith("file:") || resourceUrl.startsWith("http:") || resourceUrl.startsWith("https:")) {
      return new URL(resourceUrl);
    }
    URL resource = helper.execute(clazz, resourceUrl);
    if (resource != null) {
      return resource;
    }
    final String absoluteResourceUrl = createResourceUrl(createRootPath(clazz), resourceUrl);
    resource = helper.execute(clazz, absoluteResourceUrl);
    if (resource != null) {
      return resource;
    }
    return getUrl(clazz, absoluteResourceUrl);
  }

  private static String createRootPath(final Class<?> clazz) {
    return clazz.getPackage().getName().replace('.', '/');
  }

  private static String createResourceUrl(final String pathRoot, final String path) {
    final String resource = MessageFormat.format("{0}/{1}", pathRoot, path);
    return toAbsolutePathIfNot(Paths.get(resource).normalize().toString()); // $NON-NLS-1$
  }

  public static URL getUrl(final Class clazz, final String resourceUrl) throws IOException {
    if (resourceUrl.startsWith("file:") || resourceUrl.startsWith("http:") || resourceUrl.startsWith("https:")) {
      return new URL(resourceUrl);
    }
    URL resource = null;
    resource = getByPath(clazz, toRelativePathIfNot(clazz, resourceUrl));
    if (resource != null) {
      return resource;
    }
    resource = getByPath(clazz, toAbsolutePathIfNot(Paths.get(resourceUrl).normalize().toString()));
    if (resource != null) {
      return resource;
    }
    resource = getByPath(clazz, toAbsolutePathIfNot(resourceUrl));
    if (resource != null) {
      return resource;
    }
    throw new FileNotFoundException(resourceUrl + " on " + clazz.getName());
  }

  private static String toRelativePathIfNot(final Class clazz, final String resourceUrl) {
    final Path resourcePath = Paths.get(resourceUrl).normalize();
    final Path packagePath = Paths.get("", clazz.getPackage().getName().split("\\."));
    if (resourcePath.startsWith(packagePath)) {
      return packagePath.relativize(resourcePath).normalize().toString();
    }
    return isAbsolute(resourceUrl) ? resourceUrl.substring(1) : resourceUrl;
  }

  private static String toAbsolutePathIfNot(final String resourcePath) {
    return isAbsolute(resourcePath) ? resourcePath : "/" + resourcePath;
  }

  private static boolean isAbsolute(final String value) {
    return value.startsWith("/"); //$NON-NLS-1$
  }

  private static URL getByPath(final Class clazz, final String resourcePath) {
    URL resource = null;
    resource = clazz.getResource(resourcePath);
    if (resource != null) {
      return resource;
    }
    resource = clazz.getClassLoader().getResource(resourcePath);
    if (resource != null) {
      return resource;
    }
    resource = ClassLoader.getPlatformClassLoader().getResource(resourcePath);
    if (resource != null) {
      return resource;
    }
    resource = ClassLoader.getSystemClassLoader().getResource(resourcePath);
    if (resource != null) {
      return resource;
    }
    return null;
  }

}