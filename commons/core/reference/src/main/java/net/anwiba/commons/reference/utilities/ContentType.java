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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.parameter.IParameters;
import net.anwiba.commons.lang.parameter.Parameters;
import net.anwiba.commons.lang.parameter.ParametersBuilder;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.version.IVersion;
import net.anwiba.commons.version.Version;
import net.anwiba.commons.version.VersionUtilities;

public final class ContentType implements IContentType {

  // see: https://datatracker.ietf.org/doc/html/rfc2046
  // see: https://datatracker.ietf.org/doc/html/rfc2048
  
  private static enum State {
    primary, sub, version, parametername, parametervalue
  }

  private static class Parser {
    static IContentType parse(final String contentType)
        throws CreationException {
      String primaryType = "";
      String subType = "";
      String version = "";
      String parametername = "";
      String parametervalue = "";
      State state = State.primary;
      StringBuilder stringBuilder = new StringBuilder();
      ParametersBuilder parameters = new ParametersBuilder();
      for (char c : contentType.toCharArray()) {
        switch (c) {
          case ' ': {
            switch (state) {
              case primary: {
                throw new CreationException("Primary type is invalid.");
              }
              case sub: {
                throw new CreationException("Sub type is invalid.");
              }
              case version: {
                throw new CreationException("version is invalid.");
              }
              default: {
                stringBuilder.append(c);
                continue;
              }
            }
          }
          case '/': {
            switch (state) {
              case primary: {
                primaryType = stringBuilder.toString();
                stringBuilder = new StringBuilder();
                state = State.sub;
                continue;
              }
              case sub: {
                subType = stringBuilder.toString();
                stringBuilder = new StringBuilder();
                state = State.version;
                continue;
              }
              case version: {
                throw new CreationException("version is invalid.");
              }
              default: {
                stringBuilder.append(c);
                continue;
              }
            }
          }
          case ';': {
            switch (state) {
              case primary: {
                throw new CreationException("Primary type is invalid.");
              }
              case sub: {
                subType = stringBuilder.toString();
                stringBuilder = new StringBuilder();
                state = State.parametername;
                continue;
              }
              case version: {
                version = stringBuilder.toString();
                stringBuilder = new StringBuilder();
                state = State.parametername;
                continue;
              }
              case parametername: {
                throw new CreationException("Parameter name is invalid.");
              }
              case parametervalue: {
                parametervalue = stringBuilder.toString();
                stringBuilder = new StringBuilder();
                state = State.parametername;
                parameters.add(parametername, parametervalue);
                continue;
              }
            }
          }
          case '=': {
            switch (state) {
              case primary: {
                throw new CreationException("Sub type is invalid.");
              }
              case sub: {
                throw new CreationException("Primary type is invalid.");
              }
              case version: {
                throw new CreationException("version is invalid.");
              }
              case parametername: {
                parametername = stringBuilder.toString();
                stringBuilder = new StringBuilder();
                state = State.parametervalue;
                continue;
              }
              case parametervalue: {
                throw new CreationException("Parameter name is invalid.");
              }
            }
          }
          default: {
            switch (state) {
              case primary: {
                if (!isTokenChar(c)) {
                  throw new CreationException("Unable to find a sub type.");
                }
                stringBuilder.append(c);
                continue;
              }
              case sub: {
                if (!isTokenChar(c)) {
                  throw new CreationException("Sub type is invalid.");
                }
                stringBuilder.append(c);
                continue;
              }
              case version: {
                if (!isVersionChar(c)) {
                  throw new CreationException("Sub type is invalid.");
                }
                stringBuilder.append(c);
                continue;
              }
              default: {
                stringBuilder.append(c);
                continue;
              }
            }
          }
        }
      }
      switch (state) {
        case primary: {
          throw new CreationException("Primary type is invalid.");
        }
        case sub: {
          subType = stringBuilder.toString();
          break;
        }
        case version: {
          version = stringBuilder.toString();
        }
        case parametername: {
          if (!stringBuilder.toString().isEmpty()) {
            throw new CreationException("Missing parameter value.");
          }
          break;
        }
        case parametervalue: {
          parametervalue = stringBuilder.toString();
          parameters.add(parametername, parametervalue);
          break;
        }
      }
      if (primaryType.isEmpty()) {
        throw new CreationException("Primary type is invalid.");
      }
      if (subType.isEmpty()) {
        throw new CreationException("Sub type is invalid.");
      }
      return ContentType.create(primaryType,
          subType,
          StringUtilities.isNullOrEmpty(version) ? null : Version.of(version),
          parameters.build());
    }

    private static final String TSPECIALS = "()<>@,;:/[]?=\\\"";

    private static boolean isTokenChar(final char c) {
      return ((c > 040) && (c < 0177)) && (TSPECIALS.indexOf(c) < 0);
    }

    private static boolean isVersionChar(final char c) {
      return Character.isDigit(c) || '.' == c;
    }
  }

  public static IOptional<ContentType, RuntimeException> getByValue(final String contentType) {
    try {
      IContentType type = Parser.parse(contentType);
      return Streams.of(types)
          .first(value -> value.toString()
              .equals(type.getPrimaryType()
                  + SEPARATOR
                  + type.getSecondaryType()
                  + toString(type.getVersion().get())
                  + toString(filter(type.getParameters()))));
    } catch (CreationException exception) {
      return Optional.empty();
    }

  }

  public static IOptional<IContentType, RuntimeException> getByFileExtension(final String extension) {
    return Streams.of(types)
        .filter(type -> type.hasFileExtension(extension))
        .first()
        .instanceOf(IContentType.class);
  }

  public static IOptional<IContentType, CreationException> parse(final String contentType) {
      if (contentType == null) {
        return Optional.empty(CreationException.class);
      }
      try {
        return Optional
            .of(CreationException.class ,Parser.parse(contentType));
      } catch (CreationException exception) {
        return Optional.failed(CreationException.class, exception);
      }
  }

  public static IOptional<IContentType, RuntimeException> from(final String contentType) {
    try {
      if (contentType == null) {
        return Optional.empty();
      }
      IContentType type = Parser.parse(contentType);
      return search(type);
    } catch (CreationException exception) {
      return Optional.empty();
//      return Optional.failed(RuntimeException.class,new IllegalArgumentException(exception.getMessage(), exception));
    }
  }

  private static IOptional<IContentType, RuntimeException> search(IContentType type) {
    return Optional
        .of(from(type.getPrimaryType().toString(),
            type.getSecondaryType().toString(),
            type.getVersion().get(),
            filter(type.getParameters())));
  }

  public static IContentType from(final String primaryType, final String subType) {
    return from(primaryType, subType, null, Parameters.empty());
  }

  public static IContentType from(final String primaryType,
      final String subType,
      final IVersion version,
      final IParameters parameters) {
    return Streams.of(types)
        .first(type -> type.toString().equals(primaryType + SEPARATOR + subType + toString(parameters)))
        .instanceOf(IContentType.class)
        .getOr(() -> {
          return create(primaryType, subType, version, parameters);
        });
  }

  private static IContentType create(final String primaryType,
      final String subType,
      final IVersion version,
      final IParameters parameters) {
    return new ContentType(new PrimaryType(primaryType), new SecondaryType(subType), version, parameters);
  }

  private static final Set<ContentType> types = new LinkedHashSet<>();
  final public static IContentType TEXT_PLAIN = add(PrimaryType.TEXT, SecondaryType.PLAIN, IFileExtensions.TXT);
  final public static IContentType TEXT_XML = add(PrimaryType.TEXT, SecondaryType.XML, IFileExtensions.XML);
  final public static IContentType TEXT_HTML =
      add(PrimaryType.TEXT, SecondaryType.HTML, IFileExtensions.HTML, IFileExtensions.HTM, IFileExtensions.SHTML);
  final public static IContentType TEXT_CSV = add(PrimaryType.TEXT, SecondaryType.CSV, IFileExtensions.CSV);

  //  application/gml+xml; version=3.2
  //  text/xml; subtype=gml/3.2.1
  //  text/xml; subtype=gml/3.1.1
  //  text/xml; subtype=gml/2.1.2
  //  application/vnd.ogc.gml/3.1.1
  //  application/vnd.ogc.se_inimage
  //  application/vnd.ogc.se_blank

  //  application/vnd.esri.wms_raw_xml
  //  application/vnd.esri.wms_featureinfo_xml

  final public static IContentType APPLICATION_OCTET_STREAM = add(PrimaryType.APPLICATION, SecondaryType.OCTET_STREAM);
  final public static IContentType APPLICATION_FORM_URLENCODED =
      add(PrimaryType.APPLICATION, SecondaryType.FORM_URLENCODED);
  final public static IContentType APPLICATION_XML =
      add(PrimaryType.APPLICATION, SecondaryType.XML, IFileExtensions.XML);
  final public static IContentType APPLICATION_XHTML_XML =
      add(PrimaryType.APPLICATION, SecondaryType.XHTML_XML, IFileExtensions.XHTML, IFileExtensions.XML);
  final public static IContentType APPLICATION_GML_XML =
      add(PrimaryType.APPLICATION, SecondaryType.GML_XML, IFileExtensions.GML, IFileExtensions.XML);
  final public static IContentType APPLICATION_OGC_SE_XML = add(PrimaryType.APPLICATION,
      SecondaryType.OGC_SE_XML,
      IFileExtensions.SE,
      IFileExtensions.SE_XML,
      IFileExtensions.XML);
  final public static IContentType APPLICATION_OGC_SLD_XML = add(PrimaryType.APPLICATION,
      SecondaryType.OGC_SLD_XML,
      IFileExtensions.SLD,
      IFileExtensions.XML);
  final public static IContentType APPLICATION_OGC_WFS_XML = add(PrimaryType.APPLICATION,
      SecondaryType.OGC_WFS_XML,
      IFileExtensions.XML);
  final public static IContentType APPLICATION_OGC_WMTS_XML = add(PrimaryType.APPLICATION,
      SecondaryType.OGC_WMTS_XML,
      IFileExtensions.XML);
  final public static IContentType APPLICATION_OGC_WMS_XML = add(PrimaryType.APPLICATION,
      SecondaryType.OGC_WMS_XML,
      IFileExtensions.XML);
  final public static IContentType APPLICATION_JSON =
      add(PrimaryType.APPLICATION, SecondaryType.JSON, IFileExtensions.JSON);
  final public static IContentType APPLICATION_GEOJSON =
      add(PrimaryType.APPLICATION, SecondaryType.GEOJSON, IFileExtensions.GEOJSON, IFileExtensions.JSON);
  final public static IContentType APPLICATION_GPX =
      add(PrimaryType.APPLICATION, SecondaryType.GPX, IFileExtensions.GPX);
  final public static IContentType APPLICATION_PDF =
      add(PrimaryType.APPLICATION, SecondaryType.PDF, IFileExtensions.PDF);
  final public static IContentType APPLICATION_ZIP =
      add(PrimaryType.APPLICATION, SecondaryType.ZIP, IFileExtensions.ZIP);

  final public static IContentType IMAGE_BMP = add(PrimaryType.IMAGE, SecondaryType.BMP, IFileExtensions.BMP);
  final public static IContentType IMAGE_GIF = add(PrimaryType.IMAGE, SecondaryType.GIF, IFileExtensions.GIF);
  final public static IContentType IMAGE_JPEG =
      add(PrimaryType.IMAGE, SecondaryType.JPEG, IFileExtensions.JPG, IFileExtensions.JPEG, IFileExtensions.JPE);
  final public static IContentType IMAGE_TIFF =
      add(PrimaryType.IMAGE, SecondaryType.TIFF, IFileExtensions.TIFF, IFileExtensions.TIF);
  final public static IContentType IMAGE_PNG =
      add(PrimaryType.IMAGE, SecondaryType.PNG, IFileExtensions.PNG, IFileExtensions.PNG32);
  final public static IContentType IMAGE_PNG32 =
      add(PrimaryType.IMAGE, SecondaryType.PNG32, IFileExtensions.PNG32);
  final public static IContentType IMAGE_SVG = add(PrimaryType.IMAGE, SecondaryType.SVG, IFileExtensions.SVG);

  //  image/vnd.jpeg-png<
  //  image/vnd.jpeg-png8
  //  image/png8
  //  image/png24
  //  image/png32
  //  image/svg+xml

  final public static IContentType X_GIS_SHAPEFILE =
      add(PrimaryType.X_GIS, SecondaryType.X_SHAPEFILE, IFileExtensions.SHP);

  private static final String SEPARATOR = "/";

  private final IPrimaryType primaryType;
  private final ISecondaryType secondaryType;
  private final String[] fileExtensions;
  private final IParameters parameters;
  private final IVersion version;

  private final static IContentType
      add(final IPrimaryType primaryType, final ISecondaryType secondaryType, final String... fileExtensions) {
    ContentType type = new ContentType(primaryType, secondaryType, fileExtensions);
    types.add(type);
    return type;
  }

  ContentType(final IPrimaryType primaryType, final ISecondaryType secondaryType, final String... fileExtensions) {
    this(primaryType, secondaryType, null, Parameters.empty(), fileExtensions);
  }

  ContentType(final IPrimaryType primaryType,
      final ISecondaryType secondaryType,
      final IVersion version,
      final IParameters parameters,
      final String... fileExtensions) {
    this.primaryType = primaryType;
    this.secondaryType = secondaryType;
    this.version = version;
    this.parameters = parameters;
    this.fileExtensions = fileExtensions.length == 0 ? new String[] { "" } : fileExtensions;
  }

  @Override
  public String getName() {
    return this.primaryType + SEPARATOR + this.secondaryType;
  }

  @Override
  public IPrimaryType getPrimaryType() {
    return this.primaryType;
  }

  @Override
  public ISecondaryType getSecondaryType() {
    return this.secondaryType;
  }

  @Override
  public IOptional<IVersion, RuntimeException> getVersion() {
    return Optional.of(this.version);
  }

  @Override
  public IParameters getParameters() {
    return this.parameters;
  }

  public IOptional<String, RuntimeException> getDefaultFileExtension() {
    return this.fileExtensions.length > 0
        ? Optional.of(this.fileExtensions[0])
        : Optional.empty();
  }

  public List<String> getFileExtensions() {
    return List.of(this.fileExtensions);
  }

  public boolean hasFileExtension(final String extension) {
    if (extension == null) {
      return false;
    }
    return Streams.of(this.fileExtensions)
        .first(type -> type.equalsIgnoreCase(extension))
        .isAccepted();
  }

  public static IOptional<String, RuntimeException> getDefaultFileExtension(IContentType contentType) {
    IOptional<IContentType, RuntimeException> optional = search(contentType);
    if (optional.isEmpty()) {
      return Optional.empty();
    }
    return ((ContentType)optional.get()).getDefaultFileExtension();
  }
  
  public static List<String> getFileExtensions(IContentType contentType) {
    IOptional<IContentType, RuntimeException> optional = search(contentType);
    if (optional.isEmpty()) {
      return List.of();
    }
    return ((ContentType)optional.get()).getFileExtensions();
  }
  
  public static boolean hasFileExtension(IContentType contentType, final String extension) {
    IOptional<IContentType, RuntimeException> optional = search(contentType);
    if (optional.isEmpty()) {
      return false;
    }
    return ((ContentType)optional.get()).hasFileExtension(extension);
  }

  @Override
  public String toString() {
    return this.primaryType
        + SEPARATOR
        + this.secondaryType
        + toString(this.version)
        + toString(this.parameters);
  }

  static String toString(final IParameters parameters) {
    if (parameters.isEmpty()) {
      return "";
    }
    return "; " + String.join("; ", parameters.stream().convert(p -> p.toString()).asList());
  }

  static String toString(final IVersion version) {
    if (version == null) {
      return "";
    }
    return SEPARATOR + VersionUtilities.getTextShort(version);
  }

  //  application/gml+xml; version=3.2
  //  text/xml; subtype=gml/3.2.1
  private final static Set<String> acceptedParameterNames = Set.of("subtype", "version");

  static IParameters filter(final IParameters parameters) {
    ParametersBuilder builder = Parameters.builder();
    parameters
        .toSortedByName()
        .toLowerCase()
        .stream()
        .filter(p -> acceptedParameterNames.contains(p.getName().toLowerCase()))
        .foreach(p -> builder.add(p.getName().toLowerCase(), p.getValue()));
    return builder.build();
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.primaryType, this.secondaryType, this.version, this.parameters);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof IContentType)) {
      return false;
    }
    IContentType other = (IContentType) obj;
    return Objects.equals(this.primaryType, other.getPrimaryType())
        && Objects.equals(this.secondaryType, other.getSecondaryType())
        && Objects.equals(this.version, other.getVersion().get())
        && Objects.equals(this.parameters, other.getParameters());
  }

}
