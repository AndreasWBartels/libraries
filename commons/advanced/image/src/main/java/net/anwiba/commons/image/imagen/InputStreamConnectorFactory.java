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
package net.anwiba.commons.image.imagen;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.imagen.media.codec.ByteArraySeekableStream;
import org.eclipse.imagen.media.codec.FileSeekableStream;
import org.eclipse.imagen.media.codec.MemoryCacheSeekableStream;
import org.eclipse.imagen.media.codec.SeekableStream;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.reference.FileResourceReference;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceHandler;
import net.anwiba.commons.reference.IResourceReferenceVisitor;
import net.anwiba.commons.reference.MemoryResourceReference;
import net.anwiba.commons.reference.PathResourceReference;
import net.anwiba.commons.reference.URIResourceReference;
import net.anwiba.commons.reference.URLResourceReference;
import net.anwiba.commons.reference.UniformResourceLocatorReference;
import net.anwiba.commons.reference.url.IUrl;
import net.anwiba.commons.reference.url.builder.UrlBuilder;
import net.anwiba.commons.reference.url.parser.UrlParser;

public class InputStreamConnectorFactory {

  private final IResourceReferenceHandler resourceReferenceHandler;

  public InputStreamConnectorFactory(final IResourceReferenceHandler resourceReferenceHandler) {
    this.resourceReferenceHandler = resourceReferenceHandler;
  }

  public ISeekableStreamConnector create(final IResourceReference reference) {
    return () -> connect(reference);
  }

  private SeekableStream connect(final IResourceReference resourceReference) throws IOException {
    try {
      return resourceReference.accept(new IResourceReferenceVisitor<SeekableStream, IOException>() {

        @Override
        public SeekableStream visitUrlResource(final UniformResourceLocatorReference urlResourceReference) throws IOException  {
          throw new UnsupportedOperationException();
        }

        @Override
        public SeekableStream visitFileResource(final FileResourceReference fileResourceReference)
            throws IOException {
          return new FileSeekableStream(fileResourceReference.getFile());
        }

        @Override
        public SeekableStream visitURLResource(final URLResourceReference urlResourceReference) throws IOException {
          if (InputStreamConnectorFactory.this.resourceReferenceHandler.isFileSystemResource(resourceReference)) {
            return openAsFileIfPossible(resourceReference);
          }
          return new MemoryCacheSeekableStream(
              InputStreamConnectorFactory.this.resourceReferenceHandler.openInputStream(resourceReference,
                  value -> value != null && value.startsWith("image")));
        }

        @Override
        public SeekableStream visitURIResource(final URIResourceReference uriResourceReference) throws IOException {
          if (InputStreamConnectorFactory.this.resourceReferenceHandler.isFileSystemResource(resourceReference)) {
            return openAsFileIfPossible(resourceReference);
          }
          return new MemoryCacheSeekableStream(
              InputStreamConnectorFactory.this.resourceReferenceHandler.openInputStream(resourceReference,
                  value -> value != null && value.startsWith("image")));
        }

        @Override
        public SeekableStream visitMemoryResource(final MemoryResourceReference memoryResourceReference)
            throws IOException {
          return new ByteArraySeekableStream(memoryResourceReference.getBuffer());
        }

        @Override
        public SeekableStream visitPathResource(final PathResourceReference pathResourceReference)
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

  private SeekableStream openAsFileIfPossible(final IResourceReference reference) throws IOException {
    try {
      return new FileSeekableStream(InputStreamConnectorFactory.this.resourceReferenceHandler.getFile(reference));
    } catch (URISyntaxException e) {
      return new MemoryCacheSeekableStream(
          InputStreamConnectorFactory.this.resourceReferenceHandler.openInputStream(reference));
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
