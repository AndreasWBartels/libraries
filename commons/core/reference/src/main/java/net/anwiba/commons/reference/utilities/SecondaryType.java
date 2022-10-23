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

public final class SecondaryType implements ISecondaryType {

  private static  final Set<ISecondaryType> types = new LinkedHashSet<>();

  public static final ISecondaryType CSV = add("cvs");
  public static final ISecondaryType CSS = add("css");
  public static final ISecondaryType HTML = add("html");
  public static final ISecondaryType XHTML_XML = add("xhtml+xml");
  public static final ISecondaryType PLAIN = add("plain");
  public static final ISecondaryType QML = add("qml+csv");

  public static final ISecondaryType OCTET_STREAM = add("octet-stream");
  public static final ISecondaryType FORM_URLENCODED = add("x-www-form-urlencoded");
  public static final ISecondaryType DOCX = add("vnd.openxmlformats-officedocument.wordprocessingml.document");
  public static final ISecondaryType GPX = add("gpx+xml");
  public static final ISecondaryType JSON = add("json");
  public static final ISecondaryType GEOJSON = add("vnd.geo+json");
  public static final ISecondaryType JAVASCRIPT = add("javascript");
  public static final ISecondaryType KML = add("vnd.google-earth.kml+xml");
  public static final ISecondaryType MS_EXCEL = add("msexcel");
  public static final ISecondaryType MS_EXCEL_2007 = add("vnd.openxmlformats-officedocument.spreadsheetml.sheet");
  public static final ISecondaryType MS_POWERPOINT = add("mspowerpoint");
  public static final ISecondaryType MS_WORD = add("msword");
  public static final ISecondaryType PDF = add("pdf");
  public static final ISecondaryType RTF = add("rtf");
  public static final ISecondaryType GML_XML = add("gml+xml");

  public static final ISecondaryType OGC_SE_XML = add("vnd.ogc.se_xml");
  public static final ISecondaryType OGC_SLD_XML = add("vnd.ogc.sld+xml");
  public static final ISecondaryType OGC_WFS_XML = add("vnd.ogc.wfs_xml");
  public static final ISecondaryType OGC_WMS_XML = add("vnd.ogc.wms_xml");
  public static final ISecondaryType OGC_WMTS_XML = add("vnd.ogc.wmts_xml");

  public static final ISecondaryType XML = add("xml");
  public static final ISecondaryType ZIP = add("zip");

  public static final ISecondaryType BMP = add("bmp");
  public static final ISecondaryType GIF = add("gif");
  public static final ISecondaryType JPEG = add("jpeg");
  public static final ISecondaryType PNG = add("png");
  public static final ISecondaryType PNG32 = add("png32");
  public static final ISecondaryType SVG = add("svg+xml");
  public static final ISecondaryType TIFF = add("tiff");

  public static final ISecondaryType MPEG = add("mpeg");
  public static final ISecondaryType MP4 = add("mp4");

  public static final ISecondaryType X_WAV = add("x-wav");
  public static final ISecondaryType WAV = add("wav");
  public static final ISecondaryType ACC = add("acc");
  public static final ISecondaryType FLAC = add("flac");

  public static final ISecondaryType FORM_DATA = add("form-data");

  public static final ISecondaryType X_SHAPEFILE = add("x-shapefile");

  private final String value;

  private static final ISecondaryType add(String name) {
    SecondaryType type = new SecondaryType(name);
    types.add(type);
    return type;
  }

  SecondaryType(final String value) {
    this.value = value.toLowerCase();
  }

  public static final ISecondaryType getByName(final String name) {
    return types.stream()
        .filter(type -> type.toString().equalsIgnoreCase(name))
        .findAny()
        .orElseThrow(
            () -> new IllegalArgumentException(MessageFormat.format("Unknown secondary content type {0}", name)));
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
    if (!(obj instanceof ISecondaryType)) {
      return false;
    }
    ISecondaryType other = (ISecondaryType) obj;
    return Objects.equals(toString(), other.toString());
  }
}
