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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;

import net.anwiba.commons.resource.utilities.FileUtilities;
import net.anwiba.commons.resource.utilities.UriUtilities;

@SuppressWarnings("nls")
public class ResourceReferenceFactory implements IResourceReferenceFactory {

  @Override
  public IResourceReference create(final File file) {
    if (file == null) {
      throw new IllegalArgumentException();
    }
    return new FileResourceReference(file);
  }

  @Override
  public IResourceReference create(final byte[] buffer, final String mimeType) {
    if (buffer == null) {
      throw new IllegalArgumentException();
    }
    return new MemoryResourceReference(buffer, mimeType);
  }

  @Override
  public IResourceReference create(final URL url) {
    if (url == null) {
      throw new IllegalArgumentException();
    }
    return new UrlResourceReference(url);
  }

  @Override
  public IResourceReference create(final URI uri) {
    if (uri == null) {
      throw new IllegalArgumentException();
    }
    return new UriResourceReference(uri);
  }

  @Override
  public IResourceReference create(final String pathString) {
    if (pathString == null) {
      throw new IllegalArgumentException();
    }
    try {
      if (UriUtilities.isFileUrl(pathString)) {
        final URL url = new URL(pathString);
        final String filePath = url.getFile();
        if (url.getHost() != null && url.getHost().trim().length() > 0) {
          return new UrlResourceReference(url);
        }
        if (FileUtilities.isAbsoluteWindowsFilePath(filePath)) {
          final String absoluteWindowsFilePath = filePath.startsWith("/") ? filePath : "/" + filePath;
          final URL fileUrl = new URL(MessageFormat.format("file:{0}", absoluteWindowsFilePath));
          return new UrlResourceReference(fileUrl);
        }
        return new UriResourceReference(new File(filePath).toURI());
      }
      if (UriUtilities.isHttpUrl(pathString)) {
        return new UrlResourceReference(new URL(pathString));
      }
      return new FileResourceReference(new File(pathString));
    } catch (final MalformedURLException exception) {
      throw new IllegalArgumentException(MessageFormat.format(UriUtilities.ERROR_MESSAGE, pathString), exception);
    }
  }

  @Override
  public IResourceReference createTemporaryResourceReference(final String prefix, final String suffix)
      throws IOException {
    final File file = File.createTempFile(prefix, suffix);
    file.deleteOnExit();
    return new ResourceReferenceFactory().create(file);
  }

}
