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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.resource.utilities.FileUtilities;
import net.anwiba.commons.resource.utilities.UriToUrlConverter;
import net.anwiba.commons.resource.utilities.UriUtilities;
import net.anwiba.commons.resource.utilities.UrlToUriConverter;

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
        return new File(uriResourceReference.getUri());
      }

      @Override
      public File visitFileResource(final FileResourceReference fileResourceReference) {
        return fileResourceReference.getFile();
      }

      @Override
      public File visitMemoryResource(final MemoryResourceReference memoryResourceReference) {
        throw new UnsupportedOperationException();
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
      public URL visitMemoryResource(final MemoryResourceReference memoryResourceReference) {
        throw new UnsupportedOperationException();
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
      public URI visitMemoryResource(final MemoryResourceReference memoryResourceReference) {
        throw new UnsupportedOperationException();
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
        return memoryResourceReference.getMimeType();
      }

    });
  }

  public static boolean hasLocation(final IResourceReference resourceReference) {
    if (resourceReference == null) {
      return false;
    }
    return resourceReference.accept(new IResourceReferenceVisitor<Boolean, RuntimeException>() {

      @Override
      public Boolean visitUrlResource(final UrlResourceReference urlResourceReference) {
        return Boolean.TRUE;
      }

      @Override
      public Boolean visitUriResource(final UriResourceReference uriResourceReference) {
        return Boolean.TRUE;
      }

      @Override
      public Boolean visitFileResource(final FileResourceReference fileResourceReference) {
        return Boolean.TRUE;
      }

      @Override
      public Boolean visitMemoryResource(final MemoryResourceReference memoryResourceReference) {
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
        return MessageFormat.format("memory:{0}", memoryResourceReference.toString()); //$NON-NLS-1$
      }

    });
  }

  //  public static IResourceReference changeExtention(final IResourceReference reference, final String extension) {
  //
  //    if (reference == null) {
  //      return null;
  //    }
  //    return reference.accept(new IResourceReferenceVisitor<IResourceReference, RuntimeException>() {
  //
  //      @Override
  //      public IResourceReference visitFileResource(final FileResourceReference resourceReference)
  //          throws RuntimeException {
  //        try {
  //          final File file = ResourceReferenceUtilities.getFile(resourceReference);
  //          final File base = FileUtilities.getFileWithoutExtension(file);
  //          final File[] listFiles;
  //          if (base.getParentFile() != null && base.getParentFile().exists()) {
  //            listFiles = base.getParentFile().listFiles(new FilenameFilter() {
  //
  //              @Override
  //              public boolean accept(final File dir, final String name) {
  //                return name.equalsIgnoreCase((base.getName() + "." + extension)); //$NON-NLS-1$
  //              }
  //            });
  //          } else {
  //            listFiles = new File[0];
  //          }
  //          if (listFiles == null || listFiles.length == 0) {
  //            return new FileResourceReference(FileUtilities.addExtension(base, extension));
  //          }
  //          if (listFiles.length > 1) {
  //            throw new IllegalArgumentException(
  //                "Base of Shapefilename is not unique. " + ResourceReferenceUtilities.toString(resourceReference)); //$NON-NLS-1$
  //          }
  //          return new FileResourceReference(listFiles[0]);
  //        } catch (final URISyntaxException exception) {
  //          throw new IllegalStateException(extension);
  //        }
  //      }
  //
  //      @Override
  //      public IResourceReference visitUrlResource(final UrlResourceReference resourceReference) throws RuntimeException {
  //        final URL url = resourceReference.getUrl();
  //        try {
  //          return visitUriResource(new UriResourceReference(new UrlToUriConverter().convert(url)));
  //        } catch (URISyntaxException | IllegalArgumentException exception) {
  //          final String extensionLessUrl = UrlUtilities.removeExtension(url.toExternalForm());
  //          final String resultUrl = UrlUtilities.addExtension(extensionLessUrl, extension);
  //          return referenceFactory.create(resultUrl);
  //        }
  //      }
  //
  //      @Override
  //      public IResourceReference visitUriResource(final UriResourceReference resourceReference) throws RuntimeException {
  //        try {
  //          final URI uri = resourceReference.getUri();
  //          final String currentExtension = UriUtilities.getExtension(uri);
  //          final URI changedUri = UriUtilities.changeUriExtension(uri, currentExtension, extension);
  //          return referenceFactory.create(changedUri);
  //        } catch (final URISyntaxException e) {
  //          final String string = resourceReference.getUri().toString();
  //          final String changed = FileUtilities.addExtension(FileUtilities.getFileWithoutExtension(string), extension);
  //          return referenceFactory.create(changed);
  //        }
  //      }
  //
  //      @Override
  //      public IResourceReference visitMemoryResource(final MemoryResourceReference memoryResourceReference)
  //          throws RuntimeException {
  //        throw new UnsupportedOperationException();
  //      }
  //    });
  //  }
}