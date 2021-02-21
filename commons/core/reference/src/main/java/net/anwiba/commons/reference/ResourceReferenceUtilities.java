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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.reference.utilities.FileUtilities;
import net.anwiba.commons.reference.utilities.PathUtilities;
import net.anwiba.commons.reference.utilities.UriToUrlConverter;
import net.anwiba.commons.reference.utilities.UriUtilities;
import net.anwiba.commons.reference.utilities.UrlToUriConverter;

public class ResourceReferenceUtilities {

  static ILogger logger = Logging.getLogger(ResourceReferenceUtilities.class.getName());

  public static File getFile(final IResourceReference resourceReference) throws URISyntaxException {
    if (resourceReference == null) {
      throw new IllegalArgumentException();
    }
    final UrlToUriConverter urlToUriConverter = new UrlToUriConverter();
    return resourceReference.accept(new IResourceReferenceVisitor<File, URISyntaxException>() {

      @Override
      public File visitUrlResource(final UrlResourceReference urlResourceReference) throws URISyntaxException {
        return new File(urlToUriConverter.convert(urlResourceReference.getUrl()));
      }

      @Override
      public File visitUriResource(final UriResourceReference uriResourceReference) {
        try {
          return new File(uriResourceReference.getUri());
        } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException(uriResourceReference.getUri().toString(), e);
        }
      }

      @Override
      public File visitFileResource(final FileResourceReference fileResourceReference) {
        return fileResourceReference.getFile();
      }

      @Override
      public File visitMemoryResource(final MemoryResourceReference memoryResourceReference) {
        throw new UnsupportedOperationException();
      }

      @Override
      public File visitPathResource(final PathResourceReference pathResourceReference) throws URISyntaxException {
        return pathResourceReference.getPath().toFile();
      }
    });
  }

  public static Path getPath(final IResourceReference resourceReference) throws URISyntaxException {
    if (resourceReference == null) {
      throw new IllegalArgumentException();
    }
    final UrlToUriConverter urlToUriConverter = new UrlToUriConverter();
    return resourceReference.accept(new IResourceReferenceVisitor<Path, URISyntaxException>() {

      @Override
      public Path visitUrlResource(final UrlResourceReference urlResourceReference) throws URISyntaxException {
        return Paths.get(urlToUriConverter.convert(urlResourceReference.getUrl()));
      }

      @Override
      public Path visitUriResource(final UriResourceReference uriResourceReference) {
        return Paths.get(uriResourceReference.getUri());
      }

      @Override
      public Path visitFileResource(final FileResourceReference fileResourceReference) {
        return fileResourceReference.getFile().toPath();
      }

      @Override
      public Path visitMemoryResource(final MemoryResourceReference memoryResourceReference) {
        throw new UnsupportedOperationException();
      }

      @Override
      public Path visitPathResource(final PathResourceReference pathResourceReference) throws URISyntaxException {
        return pathResourceReference.getPath();
      }
    });
  }

  public static URL getUrl(final IResourceReference resourceReference) throws MalformedURLException {
    if (resourceReference == null) {
      throw new IllegalArgumentException();
    }
    final UriToUrlConverter uriToUrlConverter = new UriToUrlConverter();
    return resourceReference.accept(new IResourceReferenceVisitor<URL, MalformedURLException>() {

      @Override
      public URL visitUrlResource(final UrlResourceReference urlResourceReference) {
        return urlResourceReference.getUrl();
      }

      @Override
      public URL visitUriResource(final UriResourceReference uriResourceReference) throws MalformedURLException {
        return uriToUrlConverter.convert(uriResourceReference.getUri());
      }

      @Override
      public URL visitFileResource(final FileResourceReference fileResourceReference) throws MalformedURLException {
        return uriToUrlConverter.convert(fileResourceReference.getFile().toURI());
      }

      @Override
      public URL visitMemoryResource(final MemoryResourceReference memoryResourceReference)
          throws MalformedURLException {
        return new URL(memoryResourceReference.toString());
      }

      @Override
      public URL visitPathResource(final PathResourceReference pathResourceReference) throws MalformedURLException {
        Path path = pathResourceReference.getPath();
        if (Objects.equals(path.getFileSystem().provider().getScheme(), "file")) {
          return path.toFile().toURL();
        }
        return uriToUrlConverter.convert(path.toUri());
      }
    });
  }

  public static URI getUri(final IResourceReference resourceReference) throws URISyntaxException {
    if (resourceReference == null) {
      throw new IllegalArgumentException();
    }
    final UrlToUriConverter urlToUriConverter = new UrlToUriConverter();
    return resourceReference.accept(new IResourceReferenceVisitor<URI, URISyntaxException>() {

      @Override
      public URI visitUrlResource(final UrlResourceReference urlResourceReference) throws URISyntaxException {
        return urlToUriConverter.convert(urlResourceReference.getUrl());
      }

      @Override
      public URI visitUriResource(final UriResourceReference uriResourceReference) {
        return uriResourceReference.getUri();
      }

      @Override
      public URI visitFileResource(final FileResourceReference fileResourceReference) {
        return fileResourceReference.getFile().toURI();
      }

      @Override
      public URI visitMemoryResource(final MemoryResourceReference memoryResourceReference) throws URISyntaxException {
        return new URI(memoryResourceReference.toString());
      }

      @Override
      public URI visitPathResource(final PathResourceReference pathResourceReference) throws URISyntaxException {
        return pathResourceReference.getPath().toUri();
      }
    });
  }

  public static String getExtension(final IResourceReference resourceReference) {
    if (resourceReference == null) {
      throw new IllegalArgumentException();
    }
    return resourceReference.accept(new IResourceReferenceVisitor<String, RuntimeException>() {

      @Override
      public String visitFileResource(final FileResourceReference fileResourceReference) throws RuntimeException {
        return FileUtilities.getExtension(fileResourceReference.getFile());
      }

      @Override
      public String visitUrlResource(final UrlResourceReference urlResourceReference) throws RuntimeException {
        final String path = urlResourceReference.getUrl().getPath();
        if (path == null) {
          return null;
        }
        return FileUtilities.getExtension(new File(path));
      }

      @Override
      public String visitUriResource(final UriResourceReference uriResourceReference) throws RuntimeException {
        final String path = uriResourceReference.getUri().getPath();
        if (path == null) {
          return null;
        }
        return FileUtilities.getExtension(new File(path));
      }

      @Override
      public String visitMemoryResource(final MemoryResourceReference memoryResourceReference) throws RuntimeException {
        try {
          final String mimeType = memoryResourceReference.getContentType();
          if (mimeType == null) {
            return null;
          }
          return new MimeType(mimeType).getSubType();
        } catch (final MimeTypeParseException exception) {
          return null;
        }
      }

      @Override
      public String visitPathResource(final PathResourceReference pathResourceReference) throws RuntimeException {
        return PathUtilities.getExtension(pathResourceReference.getPath());
      }

    });
  }

  public static boolean isMemoryResource(final IResourceReference resourceReference) {
    if (resourceReference == null) {
      return false;
    }
    return resourceReference.accept(new IResourceReferenceVisitor<Boolean, RuntimeException>() {

      @Override
      public Boolean visitUrlResource(final UrlResourceReference urlResourceReference) {
        return Boolean.FALSE;
      }

      @Override
      public Boolean visitUriResource(final UriResourceReference uriResourceReference) {
        return Boolean.FALSE;
      }

      @Override
      public Boolean visitFileResource(final FileResourceReference fileResourceReference) {
        return Boolean.FALSE;
      }

      @Override
      public Boolean visitMemoryResource(final MemoryResourceReference memoryResourceReference) {
        return Boolean.TRUE;
      }

      @Override
      public Boolean visitPathResource(final PathResourceReference pathResourceReference) throws RuntimeException {
        return Boolean.FALSE;
      }
    }).booleanValue();
  }

  public static boolean isFileSystemResource(final IResourceReference resourceReference) {
    if (resourceReference == null) {
      return false;
    }
    final UrlToUriConverter urlToUriConverter = new UrlToUriConverter();
    return resourceReference.accept(new IResourceReferenceVisitor<Boolean, RuntimeException>() {

      @Override
      public Boolean visitUriResource(final UriResourceReference uriResourceReference) throws RuntimeException {
        return Boolean.valueOf(UriUtilities.isFileUri(uriResourceReference.getUri()));
      }

      @Override
      public Boolean visitUrlResource(final UrlResourceReference urlResourceReference) {
        try {
          final URI uri = urlToUriConverter.convert(urlResourceReference.getUrl());
          return Boolean.valueOf(UriUtilities.isFileUri(uri));
        } catch (final URISyntaxException exception) {
          logger.log(ILevel.WARNING, exception.getLocalizedMessage());
          return Boolean.FALSE;
        }
      }

      @Override
      public Boolean visitFileResource(final FileResourceReference fileResourceReference) {
        return Boolean.TRUE;
      }

      @Override
      public Boolean visitMemoryResource(final MemoryResourceReference memoryResourceReference) {
        return Boolean.FALSE;
      }

      @Override
      public Boolean visitPathResource(final PathResourceReference pathResourceReference) throws RuntimeException {
        return Boolean.TRUE;
      }
    }).booleanValue();
  }

  public static String toString(final IResourceReference resourceReference) {
    if (resourceReference == null) {
      throw new IllegalArgumentException();
    }
    return resourceReference.accept(new IResourceReferenceVisitor<String, RuntimeException>() {

      @Override
      public String visitFileResource(final FileResourceReference fileResourceReference) throws RuntimeException {
        return fileResourceReference.getFile().toString();
      }

      @Override
      public String visitUrlResource(final UrlResourceReference urlResourceReference) throws RuntimeException {
        return urlResourceReference.getUrl().toString();
      }

      @Override
      public String visitUriResource(final UriResourceReference uriResourceReference) throws RuntimeException {
        return uriResourceReference.getUri().toString();
      }

      @Override
      public String visitMemoryResource(final MemoryResourceReference memoryResourceReference) throws RuntimeException {
        return memoryResourceReference.toString();
      }

      @Override
      public String visitPathResource(final PathResourceReference pathResourceReference) throws RuntimeException {
        return pathResourceReference.getPath().toString();
      }
    });
  }

  public static String getFileName(final IResourceReference resourceReference) {
    if (resourceReference == null) {
      throw new IllegalArgumentException();
    }
    return resourceReference.accept(new IResourceReferenceVisitor<String, RuntimeException>() {

      @Override
      public String visitFileResource(final FileResourceReference fileResourceReference) throws RuntimeException {
        return fileResourceReference.getFile().getName();
      }

      @Override
      public String visitUrlResource(final UrlResourceReference urlResourceReference) throws RuntimeException {
        URL url = urlResourceReference.getUrl();
        return new File(url.getPath()).getName();
      }

      @Override
      public String visitUriResource(final UriResourceReference uriResourceReference) throws RuntimeException {
        try {
          URL url = uriResourceReference.getUri().toURL();
          return new File(url.getPath()).getName();
        } catch (MalformedURLException e) {
          URI uri = uriResourceReference.getUri();
          String path = uri.getPath();
          return new File(path).getName();
        }
      }

      @Override
      public String visitMemoryResource(final MemoryResourceReference memoryResourceReference) throws RuntimeException {
        return Base64.getEncoder().encodeToString(String.valueOf(memoryResourceReference.hashCode()).getBytes());
      }

      @Override
      public String visitPathResource(final PathResourceReference pathResourceReference) throws RuntimeException {
        return pathResourceReference.getPath().getFileName().toString();
      }

    });
  }
}