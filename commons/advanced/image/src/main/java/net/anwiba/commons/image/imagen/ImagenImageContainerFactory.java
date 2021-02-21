/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import org.eclipse.imagen.media.codec.ByteArraySeekableStream;
import org.eclipse.imagen.media.codec.FileSeekableStream;
import org.eclipse.imagen.media.codec.ImageCodec;
import org.eclipse.imagen.media.codec.MemoryCacheSeekableStream;
import org.eclipse.imagen.media.codec.SeekableStream;
import org.eclipse.imagen.media.codec.TIFFDirectory;
import org.eclipse.imagen.media.codec.TIFFField;
import org.eclipse.imagen.media.codecimpl.TIFFImage;
import org.eclipse.imagen.media.codecimpl.TIFFImageDecoder;

import net.anwiba.commons.image.IImageContainer;
import net.anwiba.commons.image.IImageContainerSettings;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.reference.FileResourceReference;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceHandler;
import net.anwiba.commons.reference.IResourceReferenceVisitor;
import net.anwiba.commons.reference.MemoryResourceReference;
import net.anwiba.commons.reference.PathResourceReference;
import net.anwiba.commons.reference.UriResourceReference;
import net.anwiba.commons.reference.UrlResourceReference;
import net.anwiba.commons.utilities.io.url.IUrl;
import net.anwiba.commons.utilities.io.url.UrlBuilder;
import net.anwiba.commons.utilities.io.url.parser.UrlParser;

public class ImagenImageContainerFactory {

  private final RenderingHints hints;
  private final IResourceReferenceHandler resourceReferenceHandler;

  public ImagenImageContainerFactory(final RenderingHints hints,
      final IResourceReferenceHandler resourceReferenceHandler) {
    this.hints = hints;
    this.resourceReferenceHandler = resourceReferenceHandler;
  }

  public IImageContainer create(final ISeekableStreamConnector seekableStreamConnector) {
    return new ImagenImageContainer(this.hints, null, seekableStreamConnector);
  }

  public IImageContainer create(final BufferedImage image) {
    return new RenderedImageContainer(this.hints, image);
  }

  public boolean isSupported(final SeekableStream inputStream) {
    IImagenImageContainerSettings imagenSettings = IImagenImageContainerSettings.getSettings(this.hints);
    if (!imagenSettings.isEnabled()) {
      return false;
    }
    IImageContainerSettings settings = IImageContainerSettings.getSettings(this.hints);
    try {
      String[] decoderNames = ImageCodec.getDecoderNames(inputStream);
      if (decoderNames != null && decoderNames.length > 0) {
        if (Set.of(decoderNames).contains("jpeg")) {
          return false;
        }
        if (Set.of(decoderNames).contains("tiff")) {
          inputStream.seek(0);
          TIFFDirectory directory = new TIFFDirectory(inputStream, 0);

          TIFFField sfield = directory.getField(TIFFImageDecoder.TIFF_SAMPLES_PER_PIXEL);
          int samplesPerPixel = sfield == null ? 1 : (int) sfield.getAsLong(0);
          TIFFField planarConfigurationField = directory.getField(TIFFImageDecoder.TIFF_PLANAR_CONFIGURATION);
          char[] planarConfiguration =
              planarConfigurationField == null ? new char[] { 1 } : planarConfigurationField.getAsChars();
          if (planarConfiguration[0] != 1 && samplesPerPixel != 1) {
            return false;
          }

          TIFFField bitsField = directory.getField(TIFFImageDecoder.TIFF_BITS_PER_SAMPLE);
          char[] bitsPerSample = null;
          if (bitsField != null) {
            bitsPerSample = bitsField.getAsChars();
          } else {
            bitsPerSample = new char[] { 1 };
            // Ensure that all samples have the same bit depth.
            for (int i = 1; i < bitsPerSample.length; i++) {
              if (bitsPerSample[i] != bitsPerSample[0]) {
                return false;
              }
            }
          }
          int sampleSize = bitsPerSample[0];

          TIFFField sampleFormatField = directory.getField(TIFFImageDecoder.TIFF_SAMPLE_FORMAT);
          char[] sampleFormat = null;
          if (sampleFormatField != null) {
            sampleFormat = sampleFormatField.getAsChars();
            // Check that all the samples have the same format
            for (int l = 1; l < sampleFormat.length; l++) {
              if (sampleFormat[l] != sampleFormat[0]) {
                return false;
              }
            }
          }

          TIFFField photoInterpField = directory.getField(TIFFImageDecoder.TIFF_PHOTOMETRIC_INTERPRETATION);

          // Set the photometric interpretation variable.
          int photometricType = -1;
          if (photoInterpField != null) {
            // Set the variable from the photometric interpretation field.
            photometricType = (int) photoInterpField.getAsLong(0);
          }

          if (photometricType == 5 && sampleSize == 8 && samplesPerPixel == 5) {
            return false;
          }

          TIFFField compField = directory.getField(TIFFImageDecoder.TIFF_COMPRESSION);
          int compression = compField == null ? TIFFImage.COMP_NONE : compField.getAsInt(0);
          switch (compression) {
            case TIFFImage.COMP_JPEG_OLD:
            case TIFFImage.COMP_JPEG_TTN2: {
              return false;
            }
          }
          return true;
        }
        return true;
      }
      return false;
    } catch (RuntimeException exception) {
      settings.getImageContainerListener().eventOccurred(exception.getMessage(), exception, MessageType.ERROR);
      return false;
    } catch (IOException exception) {
      settings.getImageContainerListener().eventOccurred(exception.getMessage(), exception, MessageType.ERROR);
      return false;
    }
  }

  public ISeekableStreamConnector createInputStreamConnector(final IResourceReference reference) {
    return () -> connect(reference);
  }

  private SeekableStream connect(final IResourceReference resourceReference) throws IOException {
    try {
      return resourceReference.accept(new IResourceReferenceVisitor<SeekableStream, IOException>() {

        @Override
        public SeekableStream visitFileResource(final FileResourceReference fileResourceReference) throws IOException {
          return new FileSeekableStream(fileResourceReference.getFile());
        }

        @Override
        public SeekableStream visitUrlResource(final UrlResourceReference urlResourceReference) throws IOException {
          if (ImagenImageContainerFactory.this.resourceReferenceHandler.isFileSystemResource(resourceReference)) {
            return openAsFileIfPossible(resourceReference);
          }
          return new MemoryCacheSeekableStream(
              ImagenImageContainerFactory.this.resourceReferenceHandler.openInputStream(resourceReference,
                  value -> value != null && value.startsWith("image")));
        }

        @Override
        public SeekableStream visitUriResource(final UriResourceReference uriResourceReference) throws IOException {
          if (ImagenImageContainerFactory.this.resourceReferenceHandler.isFileSystemResource(resourceReference)) {
            return openAsFileIfPossible(resourceReference);
          }
          return new MemoryCacheSeekableStream(
              ImagenImageContainerFactory.this.resourceReferenceHandler.openInputStream(resourceReference,
                  value -> value != null && value.startsWith("image")));
        }

        @Override
        public SeekableStream visitMemoryResource(final MemoryResourceReference memoryResourceReference)
            throws IOException {
          return new ByteArraySeekableStream(memoryResourceReference.getBuffer());
        }

        @Override
        public SeekableStream visitPathResource(final PathResourceReference pathResourceReference) throws IOException {
          return visitFileResource(new FileResourceReference(pathResourceReference.getPath().toFile()));
        }

      });
    } catch (final IOException e) {
      throw new IOException(
          String.format("Failed reading the provided resource reference: %s",
              toPrintableString(resourceReference)),
          e);
    }
  }

  private SeekableStream openAsFileIfPossible(final IResourceReference reference) throws IOException {
    try {
      return new FileSeekableStream(ImagenImageContainerFactory.this.resourceReferenceHandler.getFile(reference));
    } catch (URISyntaxException e) {
      return new MemoryCacheSeekableStream(
          ImagenImageContainerFactory.this.resourceReferenceHandler.openInputStream(reference));
    }
  }

  private String toPrintableString(final IResourceReference resourceReference) {
    final String string = this.resourceReferenceHandler.toString(resourceReference);
    try {
      final IUrl url = new UrlParser().parse(string);
      if (url.getPassword() != null) {
        return new UrlBuilder(url).setPassword("**********").build().toString();
      }
      return new UrlBuilder(url).build().toString();
    } catch (final CreationException exception) {
      return string;
    }
  }
}
