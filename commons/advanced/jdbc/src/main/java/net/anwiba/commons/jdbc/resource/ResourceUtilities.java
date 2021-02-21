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
package net.anwiba.commons.jdbc.resource;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiFunction;

public class ResourceUtilities {

  public static InputStream getInputStream(final BiFunction<Class, String, InputStream> helper,
      final Class clazz,
      final String resourceUrl) {
    return helper.apply(clazz, toRelativePathIfNot(clazz, resourceUrl));
  }

  private static String toRelativePathIfNot(final Class clazz, final String resourceUrl) {
    final Path resourcePath = Paths.get(resourceUrl).normalize();
    final Path packagePath = Paths.get("", clazz.getPackage().getName().split("\\."));
    if (resourcePath.startsWith(packagePath)) {
      return toString(packagePath.relativize(resourcePath).normalize());
    }
    return resourceUrl.startsWith("/") ? resourceUrl.substring(1) : resourceUrl;
  }

  private static String toString(final Path path) {
    String string = null;
    for (Path item : path) {
      if (string == null) {
        string = item.toString();
      } else {
        string += "/" + item.toString();
      }
    }
    return string;
  }
}