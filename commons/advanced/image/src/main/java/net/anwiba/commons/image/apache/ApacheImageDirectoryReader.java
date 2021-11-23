/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
package net.anwiba.commons.image.apache;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageParser;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata.ImageMetadataItem;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata.TiffMetadataItem;

import net.anwiba.commons.image.IImageDirectory;
import net.anwiba.commons.image.IImageDirectoryItem;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceHandler;
import net.anwiba.commons.reference.utilities.FileUtilities;
import net.anwiba.commons.thread.cancel.ICanceler;

public class ApacheImageDirectoryReader {

  private final ByteSourceConnectorFactory inputStreamConnectorFactory;

  public ApacheImageDirectoryReader(final IResourceReferenceHandler resourceReferenceHandler) {
    this.inputStreamConnectorFactory = new ByteSourceConnectorFactory(resourceReferenceHandler);
  }

  public IImageDirectory read(final ICanceler canceler, final IResourceReference resourceReference)
      throws CanceledException,
      IOException {
    IByteSourceConnector connector = this.inputStreamConnectorFactory.create(resourceReference);
    final ByteSource byteSource = connector.connect();
    ImageParser imageParser = getImageParser(byteSource).get();
    try {
      ImageMetadata metadata = imageParser.getMetadata(byteSource);
      List<IImageDirectoryItem> items =
          new LinkedHashSet<>(metadata.getItems()).stream()
              .map(this::convert)
              .filter(i -> i != null)
              .collect(Collectors.toList());
      return new IImageDirectory() {

        @Override
        public List<IImageDirectoryItem> getItems() {
          return items;
        }
      };
    } catch (ImageReadException exception) {
      return new IImageDirectory() {
      };
    }
  }

  public boolean isSupported(final IResourceReference resourceReference) {
    IByteSourceConnector connector = this.inputStreamConnectorFactory.create(resourceReference);
    try {
      final ByteSource byteSource = connector.connect();
      return getImageParser(byteSource).isAccepted();
    } catch (IOException exception) {
      return false;
    }
  }

  private static IOptional<ImageParser, RuntimeException>
      getImageParser(final ByteSource byteSource) throws IOException {
    try {
      return getImageParser(Imaging.guessFormat(byteSource));
    } catch (ImageReadException exception) {
      throw new IOException(exception.getMessage(), exception);
    }
  }

  private static IOptional<ImageParser, RuntimeException> getImageParser(
      final ImageFormat... formats) {
    final Set<ImageFormats> supportedTypes = Set.of(ImageFormats.TIFF, ImageFormats.JBIG2, ImageFormats.JPEG);
    for (ImageFormat format : formats) {
      if (supportedTypes.contains(format)) {
        final ImageParser[] imageParsers = ImageParser.getAllImageParsers();
        for (final ImageParser imageParser : imageParsers) {
          if (imageParser.canAcceptType(format)) {
            return Optional.of(imageParser);
          }
        }
      }
    }
    return Optional.empty();
  }

  private static ImageFormat guessFormat(final String fileName) {
    String lowerCase = fileName.toLowerCase();
    return Streams.of(ImageFormats.values())
        .first(f -> Objects.equals(f.getExtension().toLowerCase(), FileUtilities.getExtension(lowerCase)))
        .get();
  }

  private IImageDirectoryItem convert(final ImageMetadataItem item) {
    if (item instanceof TiffMetadataItem) {
      TiffMetadataItem tiffMetadataItem = (TiffMetadataItem) item;
      String name = tiffMetadataItem.getKeyword();
      String text = tiffMetadataItem.getText();
      int tag = tiffMetadataItem.getTiffField().getTag();
      return new IImageDirectoryItem() {

        @Override
        public int getTag() {
          return tag;
        }

        @Override
        public String getName() {
          return name;
        }

        @Override
        public String getValue() {
          return text;
        }
      };
    }
    return null;
  }
}
