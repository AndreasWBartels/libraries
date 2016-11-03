/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
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
package net.anwiba.commons.resource.reference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.resource.utilities.IoUtilities;
import net.anwiba.commons.resource.utilities.UriToUrlConverter;
import net.anwiba.commons.resource.utilities.UrlStreamConnector;
import net.anwiba.commons.resource.utilities.UrlToUriConverter;

public class ResourceReferenceHandler implements IResourceReferenceHandler {
  private static final String ENCODING = "UTF-8"; //$NON-NLS-1$
  final IStreamConnector<URI> connector;
  final UrlToUriConverter urlToUriConverter = new UrlToUriConverter();
  final UriToUrlConverter uriToUrlConverter = new UriToUrlConverter();
  final IResourceReferenceFactory factory = new ResourceReferenceFactory();

  public ResourceReferenceHandler() {
    this(new UrlStreamConnector());
  }

  public ResourceReferenceHandler(final IStreamConnector<URI> connector) {
    this.connector = connector;
  }

  @Override
  public File getFile(final IResourceReference resourceReference) throws URISyntaxException {
    return ResourceReferenceUtilities.getFile(resourceReference);
  }

  @Override
  public URL getUrl(final IResourceReference resourceReference) throws MalformedURLException {
    return ResourceReferenceUtilities.getUrl(resourceReference);
  }

  @Override
  public URI getUri(final IResourceReference resourceReference) throws URISyntaxException {
    return ResourceReferenceUtilities.getUri(resourceReference);
  }

  @Override
  public String getExtension(final IResourceReference resourceReference) {
    if (!(resourceReference instanceof FileResourceReference) && isFileSystemResource(resourceReference)) {
      try {
        return getExtension(this.factory.create(getFile(resourceReference)));
      } catch (final URISyntaxException exception) {
        // nothing to do
      }
    }
    return ResourceReferenceUtilities.getExtension(resourceReference);
  }

  @Override
  public OutputStream openOnputStream(final IResourceReference resourceReference) throws IOException {
    if (resourceReference == null) {
      throw new IllegalArgumentException();
    }
    if (!(resourceReference instanceof FileResourceReference) && isFileSystemResource(resourceReference)) {
      try {
        return openOnputStream(this.factory.create(getFile(resourceReference)));
      } catch (final URISyntaxException exception) {
        // nothing to do
      }
    }
    return resourceReference.accept(new IResourceReferenceVisitor<OutputStream, IOException>() {

      @Override
      public OutputStream visitUrlResource(final UrlResourceReference urlResourceReference) throws IOException {
        try {
          return ResourceReferenceHandler.this.connector.openOutputStream(getUri(urlResourceReference));
        } catch (final URISyntaxException exception) {
          throw new IOException(exception);
        }
      }

      @Override
      public OutputStream visitUriResource(final UriResourceReference uriResourceReference) throws IOException {
        return ResourceReferenceHandler.this.connector.openOutputStream(uriResourceReference.getUri());
      }

      @Override
      public OutputStream visitFileResource(final FileResourceReference fileResourceReference) throws IOException {
        return new FileOutputStream(fileResourceReference.getFile());
      }

      @Override
      public OutputStream visitMemoryResource(final MemoryResourceReference memoryResourceReference) throws IOException {
        throw new IOException("not yet supported for InMemoryResources resources"); //$NON-NLS-1$
      }
    });
  }

  @Override
  public InputStream openInputStream(final IResourceReference resourceReference) throws IOException {
    if (resourceReference == null) {
      throw new IllegalArgumentException();
    }
    if (!(resourceReference instanceof FileResourceReference) && isFileSystemResource(resourceReference)) {
      try {
        return openInputStream(this.factory.create(getFile(resourceReference)));
      } catch (final URISyntaxException exception) {
        // nothing to do
      }
    }
    return resourceReference.accept(new IResourceReferenceVisitor<InputStream, IOException>() {

      @Override
      public InputStream visitUrlResource(final UrlResourceReference urlResourceReference) throws IOException {
        try {
          return ResourceReferenceHandler.this.connector.openInputStream(getUri(urlResourceReference));
        } catch (final URISyntaxException exception) {
          throw new IOException(exception);
        }
      }

      @Override
      public InputStream visitUriResource(final UriResourceReference uriResourceReference) throws IOException {
        return ResourceReferenceHandler.this.connector.openInputStream(uriResourceReference.getUri());
      }

      @Override
      public InputStream visitFileResource(final FileResourceReference fileResourceReference) throws IOException {
        return new FileInputStream(fileResourceReference.getFile());
      }

      @Override
      public InputStream visitMemoryResource(final MemoryResourceReference memoryResourceReference) throws IOException {
        return new ByteArrayInputStream(memoryResourceReference.getBuffer());
      }

    });
  }

  @Override
  public boolean exsits(final IResourceReference resourceReference) {
    if (resourceReference == null) {
      return false;
    }
    if (!(resourceReference instanceof FileResourceReference) && isFileSystemResource(resourceReference)) {
      try {
        return exsits(this.factory.create(getFile(resourceReference)));
      } catch (final URISyntaxException exception) {
        // nothing to do
      }
    }
    return resourceReference.accept(new IResourceReferenceVisitor<Boolean, RuntimeException>() {

      @Override
      public Boolean visitUrlResource(final UrlResourceReference urlResourceReference) {
        try {
          return Boolean.valueOf(ResourceReferenceHandler.this.connector.exist(getUri(urlResourceReference)));
        } catch (final URISyntaxException exception) {
          return Boolean.FALSE;
        }
      }

      @Override
      public Boolean visitUriResource(final UriResourceReference uriResourceReference) {
        return Boolean.valueOf(ResourceReferenceHandler.this.connector.exist(uriResourceReference.getUri()));
      }

      @Override
      public Boolean visitFileResource(final FileResourceReference fileResourceReference) {
        return Boolean.valueOf(fileResourceReference.getFile().exists());
      }

      @Override
      public Boolean visitMemoryResource(final MemoryResourceReference memoryResourceReference) {
        return Boolean.TRUE;
      }
    }).booleanValue();
  }

  @Override
  public boolean canRead(final IResourceReference resourceReference) {
    if (resourceReference == null) {
      return false;
    }
    if (!(resourceReference instanceof FileResourceReference) && isFileSystemResource(resourceReference)) {
      try {
        return canRead(this.factory.create(getFile(resourceReference)));
      } catch (final URISyntaxException exception) {
        // nothing to do
      }
    }
    return resourceReference.accept(new IResourceReferenceVisitor<Boolean, RuntimeException>() {

      @Override
      public Boolean visitUrlResource(final UrlResourceReference urlResourceReference) {
        try {
          return Boolean.valueOf(ResourceReferenceHandler.this.connector.canRead(getUri(urlResourceReference)));
        } catch (final URISyntaxException exception) {
          return Boolean.FALSE;
        }
      }

      @Override
      public Boolean visitUriResource(final UriResourceReference uriResourceReference) {
        return Boolean.valueOf(ResourceReferenceHandler.this.connector.canRead(uriResourceReference.getUri()));
      }

      @Override
      public Boolean visitFileResource(final FileResourceReference fileResourceReference) {
        return Boolean.valueOf(fileResourceReference.getFile().canRead());
      }

      @Override
      public Boolean visitMemoryResource(final MemoryResourceReference memoryResourceReference) {
        return Boolean.TRUE;
      }
    }).booleanValue();
  }

  @Override
  public boolean canWrite(final IResourceReference resourceReference) {
    if (resourceReference == null) {
      return false;
    }
    if (!(resourceReference instanceof FileResourceReference) && isFileSystemResource(resourceReference)) {
      try {
        return canWrite(this.factory.create(getFile(resourceReference)));
      } catch (final URISyntaxException exception) {
        // nothing to do
      }
    }
    return resourceReference.accept(new IResourceReferenceVisitor<Boolean, RuntimeException>() {

      @Override
      public Boolean visitUrlResource(final UrlResourceReference urlResourceReference) {
        try {
          return Boolean.valueOf(ResourceReferenceHandler.this.connector.canWrite(getUri(urlResourceReference)));
        } catch (final URISyntaxException exception) {
          return Boolean.FALSE;
        }
      }

      @Override
      public Boolean visitUriResource(final UriResourceReference uriResourceReference) {
        return Boolean.valueOf(ResourceReferenceHandler.this.connector.canWrite(uriResourceReference.getUri()));
      }

      @Override
      public Boolean visitFileResource(final FileResourceReference fileResourceReference) {
        return Boolean.valueOf(fileResourceReference.getFile().canRead());
      }

      @Override
      public Boolean visitMemoryResource(final MemoryResourceReference memoryResourceReference) {
        return Boolean.FALSE;
      }
    }).booleanValue();
  }

  @Override
  public boolean hasLocation(final IResourceReference resourceReference) {
    return ResourceReferenceUtilities.hasLocation(resourceReference);
  }

  @Override
  public String getContent(final IResourceReference resourceReference) throws IOException {
    try (InputStream inputStream = openInputStream(resourceReference)) {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      IoUtilities.pipe(inputStream, outputStream);
      return outputStream.toString(ENCODING);
    }
  }

  @Override
  public boolean isFileSystemResource(final IResourceReference resourceReference) {
    return ResourceReferenceUtilities.isFileSystemResource(resourceReference);
  }

  @Override
  public long getContentLength(final IResourceReference resourceReference) {
    if (resourceReference == null) {
      return 0;
    }
    if (!(resourceReference instanceof FileResourceReference) && isFileSystemResource(resourceReference)) {
      try {
        return getContentLength(this.factory.create(getFile(resourceReference)));
      } catch (final URISyntaxException exception) {
        // nothing to do
      }
    }
    return resourceReference.accept(new IResourceReferenceVisitor<Long, RuntimeException>() {

      @Override
      public Long visitUrlResource(final UrlResourceReference urlResourceReference) {
        try {
          return Long.valueOf(ResourceReferenceHandler.this.connector.getContentLength(getUri(urlResourceReference)));
        } catch (final IOException | URISyntaxException exception) {
          return Long.valueOf(-1l);
        }
      }

      @Override
      public Long visitUriResource(final UriResourceReference uriResourceReference) throws RuntimeException {
        try {
          return Long.valueOf(ResourceReferenceHandler.this.connector.getContentLength(uriResourceReference.getUri()));
        } catch (final IOException exception) {
          return Long.valueOf(-1l);
        }
      }

      @Override
      public Long visitFileResource(final FileResourceReference fileResourceReference) {
        return Long.valueOf(fileResourceReference.getFile().length());
      }

      @Override
      public Long visitMemoryResource(final MemoryResourceReference memoryResourceReference) {
        return Long.valueOf(memoryResourceReference.getBuffer().length);
      }
    }).longValue();
  }

  @Override
  public boolean canDelete(final IResourceReference resourceReference) {
    return canWrite(resourceReference);
  }

  @SuppressWarnings("nls")
  @Override
  public void delete(final IResourceReference resourceReference) throws IOException {
    if (resourceReference == null) {
      throw new IllegalArgumentException();
    }
    final File file = ifFile(resourceReference).getOrThrow(
        () -> new IOException("not yet supported for '" + toString(resourceReference) + "' resources"));
    Files.deleteIfExists(file.toPath());
  }

  protected IOptional<File, IOException> ifFile(final IResourceReference resourceReference) throws IOException {
    return Optional
        .<IResourceReference, IOException> create(resourceReference)
        .accept(r -> isFileSystemResource(resourceReference))
        .convert(r -> {
          try {
            return getFile(resourceReference);
          } catch (final URISyntaxException exception) {
            throw new IOException();
          }
        });
  }

  @Override
  public String toString(final IResourceReference resourceReference) {
    return ResourceReferenceUtilities.toString(resourceReference);
  }
}