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
package net.anwiba.commons.image.imagen;

import java.io.IOException;
import java.util.Set;

import org.eclipse.imagen.media.codec.ImageCodec;
import org.eclipse.imagen.media.codec.SeekableStream;
import org.eclipse.imagen.media.codec.TIFFDirectory;

import net.anwiba.commons.image.IImageDirectory;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceHandler;
import net.anwiba.commons.thread.cancel.ICanceler;

public class ImagenImageDirectoryReader {

  private final InputStreamConnectorFactory inputStreamConnectorFactory;

  public ImagenImageDirectoryReader(final IResourceReferenceHandler resourceReferenceHandler) {
    this.inputStreamConnectorFactory = new InputStreamConnectorFactory(resourceReferenceHandler);
  }

  public IImageDirectory read(final ICanceler canceler, final IResourceReference resourceReference)
      throws CanceledException,
      IOException {
    ISeekableStreamConnector connector = this.inputStreamConnectorFactory.create(resourceReference);
    try (final SeekableStream seekableStream = connector.connect()) {
      String[] decoderNames = ImageCodec.getDecoderNames(seekableStream);
      if (decoderNames != null && decoderNames.length > 0) {
        if (Set.of(decoderNames).contains("jpeg") || Set.of(decoderNames).contains("tiff")) {
          seekableStream.seek(0);
          TIFFDirectory directory = new TIFFDirectory(seekableStream, 0);
          return new IImageDirectory() {

//            @Override
//            public int[] getTagIdentifier() {
//              return directory.getTags();
//            }
//
//            @Override
//            public Object getValue(final int tagIdentifier) {
//              TIFFField field = directory.getField(tagIdentifier);
//              int type = field.getType();
//              int count = field.getCount();
//              switch (type) {
//                case TIFFField.TIFF_ASCII: {
//                  List<String> strings = new ArrayList<>(count);
//                  for (int j = 0; j < count; j++) {
//                    strings.add(field.getAsString(j));
//                  }
//                  return strings.toArray(String[]::new);
//                }
//                case TIFFField.TIFF_BYTE: {
//                  return field.getAsBytes();
//                }
//                case TIFFField.TIFF_DOUBLE: {
//                  if (count == 1) {
//                    return field.getAsDouble(0);
//                  }
//                  return field.getAsDoubles();
//                }
//                case TIFFField.TIFF_FLOAT: {
//                  if (count == 1) {
//                    return field.getAsFloat(0);
//                  }
//                  return field.getAsFloats();
//                }
//                case TIFFField.TIFF_LONG: {
//                  if (count == 1) {
//                    return field.getAsLong(0);
//                  }
//                  return field.getAsLongs();
//                }
//                case TIFFField.TIFF_SHORT: {
//                  return field.getAsShorts();
//                }
//                case TIFFField.TIFF_RATIONAL: {
//                  if (count == 1) {
//                    return field.getAsRational(0);
//                  }
//                  return field.getAsRationals();
//                }
//                case TIFFField.TIFF_SBYTE: {
//                  return field.getAsBytes();
//                }
//                case TIFFField.TIFF_SLONG: {
//                  if (count == 1) {
//                    return field.getAsLong(0);
//                  }
//                  return field.getAsLongs();
//                }
//                case TIFFField.TIFF_SRATIONAL: {
//                  if (count == 1) {
//                    return field.getAsRational(0);
//                  }
//                  return field.getAsRationals();
//                }
//                case TIFFField.TIFF_SSHORT: {
//                  return field.getAsShorts();
//                }
//                case TIFFField.TIFF_UNDEFINED: {
//                  return field.getAsBytes();
//                }
//              }
//              return field.getAsBytes();
//            }
          };
        }
      }
    }
    return new IImageDirectory() {
    };
  }

  public boolean isSupported(final SeekableStream inputStream) {
    String[] decoderNames = ImageCodec.getDecoderNames(inputStream);
    if (decoderNames != null && decoderNames.length > 0) {
      if (Set.of(decoderNames).contains("jpeg")) {
        return true;
      }
      if (Set.of(decoderNames).contains("tiff")) {
        return true;
      }
    }
    return false;
  }

}
