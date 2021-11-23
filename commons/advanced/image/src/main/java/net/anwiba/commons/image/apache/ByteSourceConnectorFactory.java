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
import java.net.URISyntaxException;

import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.common.bytesource.ByteSourceArray;
import org.apache.commons.imaging.common.bytesource.ByteSourceFile;
import org.apache.commons.imaging.common.bytesource.ByteSourceInputStream;

import net.anwiba.commons.lang.exception.CreationException;
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

public class ByteSourceConnectorFactory {

  private final IResourceReferenceHandler resourceReferenceHandler;

  public ByteSourceConnectorFactory(final IResourceReferenceHandler resourceReferenceHandler) {
    this.resourceReferenceHandler = resourceReferenceHandler;
  }

  public IByteSourceConnector create(final IResourceReference reference) {
    return () -> connect(reference);
  }

  private ByteSource connect(final IResourceReference resourceReference) throws IOException {
    try {
      return resourceReference.accept(new IResourceReferenceVisitor<ByteSource, IOException>() {

        @Override
        public ByteSource visitFileResource(final FileResourceReference fileResourceReference)
            throws IOException {
          return new ByteSourceFile(fileResourceReference.getFile());
        }

        @Override
        public ByteSource visitUrlResource(final UrlResourceReference urlResourceReference) throws IOException {
          if (ByteSourceConnectorFactory.this.resourceReferenceHandler.isFileSystemResource(urlResourceReference)) {
            return openAsFileIfPossible(urlResourceReference);
          }
          return new ByteSourceInputStream(
              ByteSourceConnectorFactory.this.resourceReferenceHandler.openInputStream(urlResourceReference),
              ByteSourceConnectorFactory.this.resourceReferenceHandler.getFileName(urlResourceReference));
        }

        @Override
        public ByteSource visitUriResource(final UriResourceReference uriResourceReference) throws IOException {
          if (ByteSourceConnectorFactory.this.resourceReferenceHandler.isFileSystemResource(uriResourceReference)) {
            return openAsFileIfPossible(uriResourceReference);
          }
          return new ByteSourceInputStream(
              ByteSourceConnectorFactory.this.resourceReferenceHandler.openInputStream(uriResourceReference),
              ByteSourceConnectorFactory.this.resourceReferenceHandler.getFileName(uriResourceReference));
        }

        @Override
        public ByteSource visitMemoryResource(final MemoryResourceReference memoryResourceReference)
            throws IOException {
          return new ByteSourceArray(memoryResourceReference.getBuffer());
        }

        @Override
        public ByteSource visitPathResource(final PathResourceReference pathResourceReference)
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

  private ByteSource openAsFileIfPossible(final IResourceReference reference) throws IOException {
    try {
      return new ByteSourceFile(ByteSourceConnectorFactory.this.resourceReferenceHandler.getFile(reference));
    } catch (URISyntaxException e) {
      return new ByteSourceInputStream(
          ByteSourceConnectorFactory.this.resourceReferenceHandler.openInputStream(reference),
          this.resourceReferenceHandler.getFileName(reference));
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
