/*
 * #%L
 * anwiba spatial
 * %%
 * Copyright (C) 2007 - 2019 Andreas Bartels
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
// Copyright (c) 2007 by Andreas W. Bartels
package net.anwiba.spatial.coordinatereferencesystem;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anwiba.commons.utilities.regex.tokenizer.RegExpUtilities;

public class Authority implements Serializable {

  // http://www.opengis.net/gml/srs/epsg.xml#4326
  // http://www.opengis.net/def/crs/[epsg|ogc]/0/{code}
  private static final Pattern URL_PATTERN =
      Pattern.compile("^http[s]?://www\\.opengis\\.net/(gml|def)/(srs|crs)/([a-z]+)\\.xml#(/[0-9]+/)?([0-9]+)$");
  private static final Pattern URN_PATTERN = Pattern.compile("^urn:.*:crs:([A-Z]+)(:?([0-9]*)(\\.[0-9]+)*):([0-9]+)$");
  private static final Pattern AUTHORITY_PATTERN = Pattern.compile("^([^:]+):([0-9]+)$"); //$NON-NLS-1$
  private static final Pattern NUMBER_PATTERN = Pattern.compile("^[0-9]+$"); //$NON-NLS-1$

  private static final long serialVersionUID = -1;

  public static final String EPSG = "EPSG"; //$NON-NLS-1$
  public static final String SHAPEFILE = "SHAPEFILE"; //$NON-NLS-1$
  public static final String UNKNOWN = "UNKNOWN"; //$NON-NLS-1$
  private final String name;
  private final int number;

  public Authority(final String name, final int number) {
    this.name = name;
    this.number = number;
  }

  public String getName() {
    return this.name == null ? UNKNOWN : this.name;
  }

  public int getNumber() {
    return this.number;
  }

  public String getValue() {
    return this.number == -1 ? getName() : getName() + ":" + this.number; //$NON-NLS-1$
  }

  public static Authority valueOf(final String value) {
    if (isUrn(value)) {
      final Matcher matcher = URN_PATTERN.matcher(value);
      if (matcher.find(0)) {
        final String[] groups = RegExpUtilities.getGroups(matcher); // 8
        return new Authority(groups[1], Integer.valueOf(groups[groups.length - 1]).intValue());
      }
      return null;
    }
    if (isUrl(value.toLowerCase())) {
      final Matcher matcher = URL_PATTERN.matcher(value.toLowerCase());
      if (matcher.find(0)) {
        final String[] groups = RegExpUtilities.getGroups(matcher); // 8
        return new Authority(groups[3].toUpperCase(),
            Integer.valueOf(groups[groups.length - 1]).intValue());
      }
      return null;
    }
    if (isInteger(value)) {
      final int code = Integer.valueOf(value).intValue();
      return new Authority("EPSG", code); //$NON-NLS-1$
    }
    final StringTokenizer tokenizer = new StringTokenizer(value, ":"); //$NON-NLS-1$
    if (tokenizer.countTokens() != 2) {
      return null;
    }
    final String name = tokenizer.nextToken();
    final String codeString = tokenizer.nextToken();
    if (!isInteger(codeString)) {
      return null;
    }
    final int code = Integer.valueOf(codeString).intValue();
    return new Authority(name, code);
  }

  public static boolean isUrl(final String value) {
    return URL_PATTERN.matcher(value).matches(); // $NON-NLS-1$
  }

  public static boolean isUrn(final String value) {
    return URN_PATTERN.matcher(value).matches(); // $NON-NLS-1$
  }

  private static boolean isInteger(final String value) {
    return NUMBER_PATTERN.matcher(value).matches();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
    result = prime * result + this.number;
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Authority)) {
      return false;
    }
    final Authority other = (Authority) obj;
    return Objects.equals(this.name, other.name) && this.number == other.number;
  }

}
