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
package net.anwiba.commons.reference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;

import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.reference.io.IRandomInputAccess;
import net.anwiba.commons.reference.io.IRandomOutputAccess;
import net.anwiba.commons.reference.io.IStreamConnector;
import net.anwiba.commons.reference.io.RandomFileInputAccess;
import net.anwiba.commons.reference.io.RandomFileOutputAccess;
import net.anwiba.commons.reference.utilities.ContentType;
import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.commons.reference.utilities.PathUtilities;
import net.anwiba.commons.reference.utilities.UriToUrlConverter;
import net.anwiba.commons.reference.utilities.UrlStreamConnector;
import net.anwiba.commons.reference.utilities.UrlToUriConverter;

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
  public Path getPath(final IResourceReference resourceReference) throws URISyntaxException {
    return ResourceReferenceUtilities.getPath(resourceReference);
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
    if (!(resourceReference instanceof FileResourceReference || resourceReference instanceof PathResourceReference)
        && isFileSystemResource(resourceReference)) {
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
    if (!(resourceReference instanceof FileResourceReference || resourceReference instanceof PathResourceReference)
        && isFileSystemResource(resourceReference)) {
      try {
        return openOnputStream(this.factory.create(getFile(resourceReference)));
      } catch (final URISyntaxException exception) {
        // nothing to do
      }
    }
    return resourceReference.accept(new IResourceReferenceVisitor<OutputStream, IOException>() {

      @Override
      public OutputStream visitUrlResource(final UniformResourceLocatorReference urlResourceReference) throws IOException {
        throw new UnsupportedOperationException();
      }

      @Override
      public OutputStream visitURLResource(final URLResourceReference urlResourceReference) throws IOException {
        try {
          return ResourceReferenceHandler.this.connector.openOutputStream(getUri(urlResourceReference));
        } catch (final URISyntaxException exception) {
          throw new IOException(exception);
        }
      }

      @Override
      public OutputStream visitURIResource(final URIResourceReference uriResourceReference) throws IOException {
        return ResourceReferenceHandler.this.connector.openOutputStream(uriResourceReference.getUri());
      }

      @Override
      public OutputStream visitFileResource(final FileResourceReference fileResourceReference) throws IOException {
        return new FileOutputStream(fileResourceReference.getFile());
      }

      @Override
      public OutputStream visitMemoryResource(final MemoryResourceReference memoryResourceReference)
          throws IOException {
        throw new IOException("not yet supported for InMemoryResources resources"); //$NON-NLS-1$
      }

      @Override
      public OutputStream visitPathResource(final PathResourceReference pathResourceReference) throws IOException {
        return Files.newOutputStream(pathResourceReference.getPath());
      }
    });
  }

  @Override
  public InputStream openInputStream(final IResourceReference resourceReference) throws IOException {
    return openInputStream(resourceReference, s -> true);
  }

  @Override
  public InputStream openInputStream(
      final IResourceReference resourceReference,
      final IAcceptor<String> contentTypeAcceptor)
      throws IOException {
    if (resourceReference == null) {
      throw new IllegalArgumentException();
    }
    if (!(resourceReference instanceof FileResourceReference || resourceReference instanceof PathResourceReference)
        && isFileSystemResource(resourceReference)) {
      try {
        return openInputStream(this.factory.create(getFile(resourceReference)), contentTypeAcceptor);
      } catch (final URISyntaxException exception) {
        // nothing to do
      }
    }
    return resourceReference.accept(new IResourceReferenceVisitor<InputStream, IOException>() {

      @Override
      public InputStream visitUrlResource(final UniformResourceLocatorReference urlResourceReference) throws IOException {
        throw new UnsupportedOperationException();
      }

      @Override
      public InputStream visitURLResource(final URLResourceReference urlResourceReference) throws IOException {
        try {
          return ResourceReferenceHandler.this.connector
              .openInputStream(getUri(urlResourceReference), contentTypeAcceptor);
        } catch (final URISyntaxException exception) {
          throw new IOException(exception);
        }
      }

      @Override
      public InputStream visitURIResource(final URIResourceReference uriResourceReference) throws IOException {
        return ResourceReferenceHandler.this.connector
            .openInputStream(uriResourceReference.getUri(), contentTypeAcceptor);
      }

      @Override
      public InputStream visitFileResource(final FileResourceReference fileResourceReference) throws IOException {
        final String contentType = Files.probeContentType(fileResourceReference.getFile().toPath());
        if (!contentTypeAcceptor.accept(contentType)) {
          throw new IOException("Unexcepted mime type '" + contentType + "'"); //$NON-NLS-1$//$NON-NLS-2$
        }
        return new FileInputStream(fileResourceReference.getFile());
      }

      @Override
      public InputStream visitMemoryResource(final MemoryResourceReference memoryResourceReference) throws IOException {
        final String contentType = memoryResourceReference.getContentType();
        if (!contentTypeAcceptor.accept(contentType)) {
          throw new IOException("Unexcepted mime type '" + contentType + "'"); //$NON-NLS-1$//$NON-NLS-2$
        }
        return new ByteArrayInputStream(memoryResourceReference.getBuffer());
      }

      @Override
      public InputStream visitPathResource(final PathResourceReference pathResourceReference) throws IOException {
        final String contentType = Files.probeContentType(pathResourceReference.getPath());
        if (!contentTypeAcceptor.accept(contentType)) {
          throw new IOException("Unexcepted mime type '" + contentType + "'"); //$NON-NLS-1$//$NON-NLS-2$
        }
        return Files.newInputStream(pathResourceReference.getPath());
      }

    });
  }

  @Override
  public boolean exists(final IResourceReference resourceReference) {
    if (resourceReference == null) {
      return false;
    }
    return resourceReference.accept(new IResourceReferenceVisitor<Boolean, RuntimeException>() {

      @Override
      public Boolean visitUrlResource(final UniformResourceLocatorReference urlResourceReference) {
        return Boolean.FALSE;
      }

      @Override
      public Boolean visitURLResource(final URLResourceReference urlResourceReference) {
        try {
          if (isFileSystemResource(urlResourceReference)) {
            File file = getFile(urlResourceReference);
            return file.exists();
          }
          return Boolean.valueOf(ResourceReferenceHandler.this.connector.exist(getUri(urlResourceReference)));
        } catch (URISyntaxException e) {
          return Boolean.FALSE;
        }
      }

      @Override
      public Boolean visitURIResource(final URIResourceReference uriResourceReference) {
        try {
          if (isFileSystemResource(uriResourceReference)) {
            File file = getFile(uriResourceReference);
            return file.exists();
          }
          return Boolean.valueOf(ResourceReferenceHandler.this.connector.exist(getUri(uriResourceReference)));
        } catch (URISyntaxException e) {
          return Boolean.FALSE;
        }
      }

      @Override
      public Boolean visitFileResource(final FileResourceReference fileResourceReference) {
        final File file = fileResourceReference.getFile();
        return Files.exists(file.toPath());
      }

      @Override
      public Boolean visitMemoryResource(final MemoryResourceReference memoryResourceReference) {
        return Boolean.TRUE;
      }

      @Override
      public Boolean visitPathResource(final PathResourceReference pathResourceReference) throws RuntimeException {
        return Files.exists(pathResourceReference.getPath());
      }
    }).booleanValue();
  }

  @Override
  public boolean canRead(final IResourceReference resourceReference) {
    if (resourceReference == null) {
      return false;
    }
    if (!(resourceReference instanceof FileResourceReference || resourceReference instanceof PathResourceReference)
        && isFileSystemResource(resourceReference)) {
      try {
        return canRead(this.factory.create(getFile(resourceReference)));
      } catch (final URISyntaxException exception) {
        // nothing to do
      }
    }
    return resourceReference.accept(new IResourceReferenceVisitor<Boolean, RuntimeException>() {

      @Override
      public Boolean visitUrlResource(final UniformResourceLocatorReference urlResourceReference) {
        return Boolean.FALSE;
      }

      @Override
      public Boolean visitURLResource(final URLResourceReference urlResourceReference) {
        try {
          return Boolean.valueOf(ResourceReferenceHandler.this.connector.canRead(getUri(urlResourceReference)));
        } catch (final URISyntaxException exception) {
          return Boolean.FALSE;
        }
      }

      @Override
      public Boolean visitURIResource(final URIResourceReference uriResourceReference) {
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

      @Override
      public Boolean visitPathResource(final PathResourceReference pathResourceReference) throws RuntimeException {
        return Files.isReadable(pathResourceReference.getPath());
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
      public Boolean visitUrlResource(final UniformResourceLocatorReference urlResourceReference) {
        return Boolean.FALSE;
      }

      @Override
      public Boolean visitURLResource(final URLResourceReference urlResourceReference) {
        try {
          return Boolean.valueOf(ResourceReferenceHandler.this.connector.canWrite(getUri(urlResourceReference)));
        } catch (final URISyntaxException exception) {
          return Boolean.FALSE;
        }
      }

      @Override
      public Boolean visitURIResource(final URIResourceReference uriResourceReference) {
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

      @Override
      public Boolean visitPathResource(final PathResourceReference pathResourceReference) throws RuntimeException {
        return Files.isWritable(pathResourceReference.getPath());
      }
    }).booleanValue();
  }

  @Override
  public boolean isMemoryResource(final IResourceReference resourceReference) {
    return ResourceReferenceUtilities.isMemoryResource(resourceReference);
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
    if (!(resourceReference instanceof FileResourceReference || resourceReference instanceof PathResourceReference)
        && isFileSystemResource(resourceReference)) {
      try {
        return getContentLength(this.factory.create(getFile(resourceReference)));
      } catch (final URISyntaxException exception) {
        // nothing to do
      }
    }
    return resourceReference.accept(new IResourceReferenceVisitor<Long, RuntimeException>() {

      @Override
      public Long visitUrlResource(final UniformResourceLocatorReference urlResourceReference) {
        return Long.valueOf(-1l);
      }

      @Override
      public Long visitURLResource(final URLResourceReference urlResourceReference) {
        try {
          return Long.valueOf(ResourceReferenceHandler.this.connector.getContentLength(getUri(urlResourceReference)));
        } catch (final IOException | URISyntaxException exception) {
          return Long.valueOf(-1l);
        }
      }

      @Override
      public Long visitURIResource(final URIResourceReference uriResourceReference) throws RuntimeException {
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

      @Override
      public Long visitPathResource(final PathResourceReference pathResourceReference) throws RuntimeException {
        try {
          return Long.valueOf(Files.size(pathResourceReference.getPath()));
        } catch (final IOException exception) {
          return Long.valueOf(-1l);
        }
      }
    }).longValue();
  }

  @Override
  public boolean canDelete(final IResourceReference resourceReference) {
    try {
      return ifFile(resourceReference).convert(input -> canDelete(input)).getOr(() -> Boolean.FALSE).booleanValue();
    } catch (final IOException exception) {
      return false;
    }
  }

  @SuppressWarnings("nls")
  @Override
  public void delete(final IResourceReference resourceReference) throws IOException {
    if (resourceReference == null) {
      throw new IllegalArgumentException();
    }
    final File file = ifFile(resourceReference)
        .getOrThrow(() -> new IOException("not yet supported for '" + toString(resourceReference) + "' resources"));
    if (!canDelete(file)) {
      throw new IOException("insufficient privileges");
    }
    final Path directory = file.toPath();
    if (file.isDirectory()) {
      Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(final Path path, final BasicFileAttributes attributes) throws IOException {
          Files.delete(path);
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path folder, final IOException exception) throws IOException {
          if (exception != null) {
            throw exception;
          }
          Files.delete(folder);
          return FileVisitResult.CONTINUE;
        }
      });
      return;
    }
    Files.deleteIfExists(file.toPath());
  }

  private boolean canDelete(final File file) {
    if (!file.canWrite()) {
      return false;
    }
    if (file.isDirectory()) {
      final File[] files = java.util.Optional.ofNullable(file.listFiles()).orElseGet(() -> new File[0]);
      for (final File child : files) {
        if (!canDelete(child)) {
          return false;
        }
      }
    }
    return true;
  }

  protected IOptional<File, IOException> ifFile(final IResourceReference resourceReference) {
    return Optional
        .of(IOException.class, resourceReference)
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

  @Override
  public String getContentType(final IResourceReference resourceReference) {
    if (resourceReference == null) {
      return null;
    }
    final IResourceReferenceVisitor<String, RuntimeException> visitor = new IResourceReferenceVisitor<>() {

      @Override
      public String visitUrlResource(final UniformResourceLocatorReference urlResourceReference) throws RuntimeException  {
        throw new UnsupportedOperationException();
      }

      @Override
      public String visitFileResource(final FileResourceReference fileResourceReference) throws RuntimeException {
        return getContentType(fileResourceReference.getFile().toPath());
      }

      @Override
      public String visitURLResource(final URLResourceReference urlResourceReference) throws RuntimeException {
        try {
          if (isFileSystemResource(resourceReference)) {
            return getContentType(getPath(urlResourceReference));
          }
          return Optional
              .of(
                  IOException.class,
                  ResourceReferenceHandler.this.connector.getContentType(urlResourceReference.getUrl().toURI()))
              .getOr(() -> "application/octet-stream"); //$NON-NLS-1$
        } catch (final URISyntaxException | IOException exception) {
          return "application/octet-stream"; //$NON-NLS-1$
        }
      }

      @Override
      public String visitURIResource(final URIResourceReference uriResourceReference) throws RuntimeException {
        try {
          if (isFileSystemResource(resourceReference)) {
            return getContentType(getPath(uriResourceReference));
          }
          return Optional
              .of(
                  IOException.class,
                  ResourceReferenceHandler.this.connector.getContentType(uriResourceReference.getUri()))
              .getOr(() -> "application/octet-stream"); //$NON-NLS-1$
        } catch (final URISyntaxException | IOException exception) {
          return "application/octet-stream"; //$NON-NLS-1$
        }
      }

      @Override
      public String visitMemoryResource(final MemoryResourceReference memoryResourceReference) throws RuntimeException {
        return memoryResourceReference.getContentType();
      }

      @Override
      public String visitPathResource(final PathResourceReference pathResourceReference) throws RuntimeException {
        return getContentType(pathResourceReference.getPath());
      }

      private String getContentType(final Path path) {
        return ContentType.getByFileExtension(PathUtilities.getExtension(path))
            .convert(m -> m.toString())
            .getOr(() -> {
              try {
                String contentType = Files.probeContentType(path);
                return Optional.of(contentType).getOr(() -> "application/octet-stream");
              } catch (final IOException exception) {
                return "application/octet-stream"; //$NON-NLS-1$
              }
            });
      }
    };
    return resourceReference.accept(visitor);
  }

  @Override
  public String getFileName(final IResourceReference reference) {
    return ResourceReferenceUtilities.getFileName(reference);
  }

  @Override
  public FileTime lastModified(final IResourceReference resourceReference) throws IOException {
    final IResourceReferenceVisitor<FileTime, IOException> visitor = new IResourceReferenceVisitor<>() {

      @Override
      public FileTime visitUrlResource(final UniformResourceLocatorReference urlResourceReference) throws RuntimeException  {
        throw new UnsupportedOperationException();
      }

      @Override
      public FileTime visitFileResource(final FileResourceReference fileResourceReference) throws IOException {
        return lastModified(fileResourceReference.getFile().toPath());
      }

      @Override
      public FileTime visitURLResource(final URLResourceReference urlResourceReference) throws IOException {
        try {
          if (isFileSystemResource(urlResourceReference)) {
            return lastModified(getPath(urlResourceReference));
          }
          return FileTime.from(0, TimeUnit.SECONDS);
        } catch (URISyntaxException e) {
          throw new IOException(e.getMessage(), e);
        }
      }

      @Override
      public FileTime visitURIResource(final URIResourceReference uriResourceReference) throws IOException {
        try {
          if (isFileSystemResource(uriResourceReference)) {
            return lastModified(getPath(uriResourceReference));
          }
          return FileTime.from(0, TimeUnit.SECONDS);
        } catch (URISyntaxException e) {
          throw new IOException(e.getMessage(), e);
        }
      }

      @Override
      public FileTime visitMemoryResource(final MemoryResourceReference memoryResourceReference)
          throws IOException {
        return memoryResourceReference.creationTime();
      }

      @Override
      public FileTime visitPathResource(final PathResourceReference pathResourceReference) throws IOException {
        return lastModified(pathResourceReference.getPath());
      }

      private FileTime lastModified(final Path path) throws IOException {
        return Files.getLastModifiedTime(path);
      }
    };
    return resourceReference.accept(visitor);
  }

  @Override
  public FileTime lastAccessed(final IResourceReference resourceReference) throws IOException {
    final IResourceReferenceVisitor<FileTime, IOException> visitor = new IResourceReferenceVisitor<>() {

      @Override
      public FileTime visitUrlResource(final UniformResourceLocatorReference urlResourceReference) throws RuntimeException  {
        throw new UnsupportedOperationException();
      }

      @Override
      public FileTime visitFileResource(final FileResourceReference fileResourceReference) throws IOException {
        return visitPathResource(new PathResourceReference(fileResourceReference.getFile().toPath()));
      }

      @Override
      public FileTime visitURLResource(final URLResourceReference urlResourceReference) throws IOException {
        try {
          if (isFileSystemResource(urlResourceReference)) {
            return visitFileResource(new FileResourceReference(getFile(urlResourceReference)));
          }
          return FileTime.from(0, TimeUnit.SECONDS);
        } catch (URISyntaxException e) {
          throw new IOException(e.getMessage(), e);
        }
      }

      @Override
      public FileTime visitURIResource(final URIResourceReference uriResourceReference) throws IOException {
        try {
          if (isFileSystemResource(uriResourceReference)) {
            return visitFileResource(new FileResourceReference(getFile(uriResourceReference)));
          }
          return FileTime.from(0, TimeUnit.SECONDS);
        } catch (URISyntaxException e) {
          throw new IOException(e.getMessage(), e);
        }
      }

      @Override
      public FileTime visitMemoryResource(final MemoryResourceReference memoryResourceReference)
          throws IOException {
        return memoryResourceReference.creationTime();
      }

      @Override
      public FileTime visitPathResource(final PathResourceReference pathResourceReference) throws IOException {
        return lastAccessed(pathResourceReference.getPath());
      }

      private FileTime lastAccessed(final Path path) throws IOException {
        return Files.readAttributes(path, BasicFileAttributes.class).lastAccessTime();
      }
    };
    return resourceReference.accept(visitor);
  }

  @Override
  public FileTime created(final IResourceReference resourceReference) throws IOException {
    final IResourceReferenceVisitor<FileTime, IOException> visitor = new IResourceReferenceVisitor<>() {

      @Override
      public FileTime visitUrlResource(final UniformResourceLocatorReference urlResourceReference) throws RuntimeException  {
        throw new UnsupportedOperationException();
      }

      @Override
      public FileTime visitFileResource(final FileResourceReference fileResourceReference) throws IOException {
        return visitPathResource(new PathResourceReference(fileResourceReference.getFile().toPath()));
      }

      @Override
      public FileTime visitURLResource(final URLResourceReference urlResourceReference) throws IOException {
        try {
          if (isFileSystemResource(urlResourceReference)) {
            return visitFileResource(new FileResourceReference(getFile(urlResourceReference)));
          }
          return FileTime.from(0, TimeUnit.SECONDS);
        } catch (URISyntaxException e) {
          throw new IOException(e.getMessage(), e);
        }
      }

      @Override
      public FileTime visitURIResource(final URIResourceReference uriResourceReference) throws IOException {
        try {
          if (isFileSystemResource(uriResourceReference)) {
            return visitFileResource(new FileResourceReference(getFile(uriResourceReference)));
          }
          return FileTime.from(0, TimeUnit.SECONDS);
        } catch (URISyntaxException e) {
          throw new IOException(e.getMessage(), e);
        }
      }

      @Override
      public FileTime visitMemoryResource(final MemoryResourceReference memoryResourceReference)
          throws IOException {
        return memoryResourceReference.creationTime();
      }

      @Override
      public FileTime visitPathResource(final PathResourceReference pathResourceReference) throws IOException {
        return created(pathResourceReference.getPath());
      }

      private FileTime created(final Path path) throws IOException {
        return Files.readAttributes(path, BasicFileAttributes.class).creationTime();
      }

    };
    return resourceReference.accept(visitor);
  }

  @Override
  public IResourceReference toInMemoryReference(final IResourceReference resourceReference) throws IOException {
    return toInMemoryReference(resourceReference, getContentType(resourceReference), ENCODING);
  }

  @Override
  public IResourceReference
      toInMemoryReference(final IResourceReference resourceReference, final String contentType, final String encoding)
          throws IOException {
    final IResourceReferenceVisitor<IResourceReference, IOException> visitor = new IResourceReferenceVisitor<>() {

      @Override
      public IResourceReference visitUrlResource(final UniformResourceLocatorReference urlResourceReference) throws RuntimeException  {
        throw new UnsupportedOperationException();
      }

      @Override
      public IResourceReference visitFileResource(final FileResourceReference fileResourceReference)
          throws IOException {
        return copyTo(fileResourceReference);
      }

      @Override
      public IResourceReference visitURLResource(final URLResourceReference urlResourceReference) throws IOException {
        return copyTo(urlResourceReference);
      }

      @Override
      public IResourceReference visitURIResource(final URIResourceReference uriResourceReference) throws IOException {
        return copyTo(uriResourceReference);
      }

      @Override
      public IResourceReference visitMemoryResource(final MemoryResourceReference memoryResourceReference)
          throws IOException {
        return memoryResourceReference;
      }

      @Override
      public IResourceReference visitPathResource(final PathResourceReference pathResourceReference)
          throws IOException {
        return copyTo(pathResourceReference);
      }

      private IResourceReference copyTo(final IResourceReference reference) throws IOException {
        try (InputStream stream = openInputStream(resourceReference)) {
          return ResourceReferenceHandler.this.factory.create(IoUtilities.toByteArray(stream), contentType, encoding);
        }
      }

    };
    return resourceReference.accept(visitor);
  }

  @Override
  public boolean canAccessRandom(final IResourceReference resourceReference) {
    return isFileSystemResource(resourceReference);
  }

  @Override
  public IRandomInputAccess getRandomInputAccess(final IResourceReference resourceReference) throws IOException {
    try {
      return new RandomFileInputAccess(new RandomAccessFile(getFile(resourceReference), "r"));
    } catch (FileNotFoundException | URISyntaxException exception) {
      throw new IOException(exception.getMessage(), exception);
    }
  }

  @Override
  public IRandomOutputAccess getRandomOutputAccess(final IResourceReference resourceReference) throws IOException {
    try {
      return new RandomFileOutputAccess(new RandomAccessFile(getFile(resourceReference), "rw"));
    } catch (FileNotFoundException | URISyntaxException exception) {
      throw new IOException(exception.getMessage(), exception);
    }
  }
}