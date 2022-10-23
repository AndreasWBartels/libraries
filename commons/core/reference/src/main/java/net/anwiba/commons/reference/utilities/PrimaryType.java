/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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

import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class PrimaryType implements IPrimaryType {

  private static final Set<IPrimaryType> types = new LinkedHashSet<>();
  public static final IPrimaryType APPLICATION = add("application");
  public static final IPrimaryType AUDIO = add("audio");
  public static final IPrimaryType EXAMPLE = add("example");
  public static final IPrimaryType IMAGE = add("image");
  public static final IPrimaryType MESSAGE = add("message");
  public static final IPrimaryType MODEL = add("model");
  public static final IPrimaryType MULTIPART = add("multipart");
  public static final IPrimaryType TEXT = add("text");
  public static final IPrimaryType VIDEO = add("video");
  public static final IPrimaryType X_GIS = add("x-gis");

  private final String value;

  private static final IPrimaryType add(String name) {
    PrimaryType type = new PrimaryType(name);
    types.add(type);
    return type;
  }

  PrimaryType(final String value) {
    this.value = value.toLowerCase();
  }

  public static IPrimaryType getByName(final String name) {
    return types.stream()
        .filter(type -> type.toString().equalsIgnoreCase(name))
        .findAny()
        .orElseThrow(
            () -> new IllegalArgumentException(MessageFormat.format("Unknown primary content type {0}", name)));
  }

  @Override
  public String toString() {
    return this.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.value);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof IPrimaryType)) {
      return false;
    }
    IPrimaryType other = (IPrimaryType) obj;
    return Objects.equals(toString(), other.toString());
  }
}
