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
package net.anwiba.commons.image.imageio;

import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.eclipse.imagen.media.codec.ByteArraySeekableStream;

import net.anwiba.commons.image.IImageContainer;
import net.anwiba.commons.image.IImageContainerSettings;
import net.anwiba.commons.image.ImageUtilities;
import net.anwiba.commons.image.awt.BufferedImageContainerFactory;
import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.logging.ILevel;
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

public class ImageIoImageContainerFactory {

  private static net.anwiba.commons.logging.ILogger logger =
      net.anwiba.commons.logging.Logging.getLogger(ImageIoImageContainerFactory.class);
  private final RenderingHints hints;
  final BufferedImageContainerFactory bufferedImageContainerFactory;
  private final IResourceReferenceHandler resourceReferenceHandler;

  public ImageIoImageContainerFactory(final RenderingHints hints,
      final IResourceReferenceHandler resourceReferenceHandler) {
    this.hints = hints;
    this.resourceReferenceHandler = resourceReferenceHandler;
    this.bufferedImageContainerFactory = new BufferedImageContainerFactory(hints);
  }

  public IImageContainer create(final IImageInputStreamConnector imageInputStreamConnector) throws IOException {
    IImageIoImageContainerSettings imageIoSettings = IImageIoImageContainerSettings.getSettings(this.hints);
    IImageContainerSettings settings = IImageContainerSettings.getSettings(this.hints);
    try (ImageInputStream imageInputStream = imageInputStreamConnector.connect()) {
      final IObjectList<ImageReader> imageReaders =
          Streams.of(IOException.class, ImageIO.getImageReaders(imageInputStream)).asObjectList();
      if (imageReaders.isEmpty()) {
        imageInputStream.close();
        logger.log(ILevel.WARNING, "missing reader");
        logger.log(ILevel.DEBUG, "missing reader", new IOException("missing reader"));
        return this.bufferedImageContainerFactory.create(ImageUtilities.create(100, 100));
      }
      final ImageReader imageReader = imageIoSettings.getImageReader(imageReaders);
      imageReader.addIIOReadWarningListener(
          (source, warning) -> settings.getImageContainerListener().eventOccurred(warning, null, MessageType.WARNING));
      imageReader.setInput(imageInputStream);
      final int index = imageReader.getMinIndex();
      final int width = imageReader.getWidth(index);
      final int height = imageReader.getHeight(index);
      final Iterator<ImageTypeSpecifier> imageTypes = imageReader.getImageTypes(index);
      final ImageTypeSpecifier imageType =
          imageIoSettings.getImageTypeSpecifier(Streams.of(IOException.class, imageTypes).asObjectList());

//      IIOMetadata imageMetadata = imageReader.getImageMetadata(index);
//      String[] extraMetadataFormatNames =
//          Optional.of(imageMetadata.getExtraMetadataFormatNames()).getOr(() -> new String[0]);
//
//      for (String name : extraMetadataFormatNames) {
//        IIOMetadataFormat metadataFormat = imageMetadata.getMetadataFormat(name);
//        Node asTree = imageMetadata.getAsTree(name);
//        NamedNodeMap attributes = asTree.getAttributes();
//        attributes.getLength();
//      }
//
//      String[] metadataFormatNames = Optional.of(imageMetadata.getMetadataFormatNames()).getOr(() -> new String[0]);
//      for (String name : metadataFormatNames) {
//        IIOMetadataFormat metadataFormat = imageMetadata.getMetadataFormat(name);
//        Node asTree = imageMetadata.getAsTree(name);
//        NamedNodeMap attributes = asTree.getAttributes();
//        attributes.getLength();
//      }

      final ColorModel colorModel = imageType.getColorModel();
      final ColorSpace colorSpace = colorModel.getColorSpace();
      int numColorComponents = colorSpace.getNumComponents();
      int numBands = imageType.getNumBands();
      final int colorSpaceType = colorSpace.getType();
      final ImageIoImageMetadata metadata = new ImageIoImageMetadata(
          index,
          width,
          height,
          numColorComponents,
          numBands,
          colorSpaceType,
          colorModel.getTransferType(),
          colorModel.getTransparency(),
          imageType);
      imageReader.setInput(null);
      imageReader.dispose();
      return new ImageIoImageContainer(this.hints, metadata, imageInputStreamConnector);
    } catch (final IOException exception) {
      logger.log(ILevel.WARNING, exception.getMessage());
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      settings.getImageContainerListener().eventOccurred(exception.getMessage(), exception, MessageType.ERROR);
      return this.bufferedImageContainerFactory.create(ImageUtilities.create(100, 100));
    }
  }

  public boolean isSupported(final IImageInputStreamConnector imageInputStreamProvider) {
    IImageIoImageContainerSettings imageIoSettings = IImageIoImageContainerSettings.getSettings(this.hints);
    if (!imageIoSettings.isEnabled()) {
      return false;
    }
    IImageContainerSettings settings = IImageContainerSettings.getSettings(this.hints);
    try (ImageInputStream imageInputStream = imageInputStreamProvider.connect()) {
      final IObjectList<ImageReader> imageReaders =
          Streams.of(IOException.class, ImageIO.getImageReaders(imageInputStream)).asObjectList();
      return !imageReaders.isEmpty();
    } catch (final IOException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
      settings.getImageContainerListener().eventOccurred(exception.getMessage(), exception, MessageType.ERROR);
      return false;
    }
  }

  public IImageInputStreamConnector createInputStreamConnector(final IResourceReference reference) {
    return () -> connect(reference);
  }

  private ImageInputStream connect(final IResourceReference resourceReference) throws IOException {
    try {
      return resourceReference.accept(new IResourceReferenceVisitor<ImageInputStream, IOException>() {

        @Override
        public ImageInputStream visitFileResource(final FileResourceReference fileResourceReference)
            throws IOException {
          return new FileImageInputStream(fileResourceReference.getFile());
        }

        @Override
        public ImageInputStream visitUrlResource(final UrlResourceReference urlResourceReference) throws IOException {
          if (ImageIoImageContainerFactory.this.resourceReferenceHandler.isFileSystemResource(resourceReference)) {
            return openAsFileIfPossible(resourceReference);
          }
          return new MemoryCacheImageInputStream(
              ImageIoImageContainerFactory.this.resourceReferenceHandler.openInputStream(resourceReference,
                  value -> value != null && value.startsWith("image")));
        }

        @Override
        public ImageInputStream visitUriResource(final UriResourceReference uriResourceReference) throws IOException {
          if (ImageIoImageContainerFactory.this.resourceReferenceHandler.isFileSystemResource(resourceReference)) {
            return openAsFileIfPossible(resourceReference);
          }
          return new MemoryCacheImageInputStream(
              ImageIoImageContainerFactory.this.resourceReferenceHandler.openInputStream(resourceReference,
                  value -> value != null && value.startsWith("image")));
        }

        @Override
        public ImageInputStream visitMemoryResource(final MemoryResourceReference memoryResourceReference)
            throws IOException {
          return new SeekableImageInputStream(
              new ByteArraySeekableStream(memoryResourceReference.getBuffer()));
        }

        @Override
        public ImageInputStream visitPathResource(final PathResourceReference pathResourceReference)
            throws IOException {
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

  private ImageInputStream openAsFileIfPossible(final IResourceReference reference) throws IOException {
    try {
      return new FileImageInputStream(this.resourceReferenceHandler.getFile(reference));
    } catch (URISyntaxException e) {
      return new MemoryCacheImageInputStream(
          this.resourceReferenceHandler.openInputStream(reference));
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
