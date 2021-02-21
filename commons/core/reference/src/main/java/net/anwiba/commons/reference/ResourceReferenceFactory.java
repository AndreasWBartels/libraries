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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.reference.utilities.UriUtilities;

@SuppressWarnings("nls")
public class ResourceReferenceFactory implements IResourceReferenceFactory {

  @Override
  public IResourceReference create(final File file) {
    if (file == null) {
      throw new IllegalArgumentException();
    }
    try {
      return create(file.getPath());
    } catch (CreationException e) {
      return new FileResourceReference(file);
    }
  }

  @Override
  public IResourceReference create(final byte[] buffer, final String mimeType, final String encoding) {
    if (buffer == null) {
      throw new IllegalArgumentException();
    }
    return new MemoryResourceReference(buffer, mimeType, encoding);
  }

  @Override
  public IResourceReference create(final URL url) {
    if (url == null) {
      throw new IllegalArgumentException();
    }
    final UrlResourceReference urlResourceReference = new UrlResourceReference(url);
    if (ResourceReferenceUtilities.isFileSystemResource(urlResourceReference)) {
      try {
        Path path = ResourceReferenceUtilities.getPath(urlResourceReference);
        return create(path);
      } catch (URISyntaxException e) {
        return urlResourceReference;
      }
    }
    return urlResourceReference;
  }

  @Override
  public IResourceReference create(final URI uri) {
    if (uri == null) {
      throw new IllegalArgumentException();
    }
    final UriResourceReference uriResourceReference = new UriResourceReference(uri);
    if (ResourceReferenceUtilities.isFileSystemResource(uriResourceReference)) {
      try {
        Path path = ResourceReferenceUtilities.getPath(uriResourceReference);
        return create(path);
      } catch (URISyntaxException e) {
        return uriResourceReference;
      }
    }
    return uriResourceReference;
  }

  @Override
  public IResourceReference create(final Path path) {
    if (path == null) {
      throw new IllegalArgumentException();
    }
    return new PathResourceReference(path);
  }

  @Override
  public IResourceReference create(final String pathString) throws CreationException {
    if (pathString == null) {
      throw new IllegalArgumentException();
    }
    try {
      if (UriUtilities.isHttpUrl(pathString)) {
        return new UrlResourceReference(new URL(pathString));
      }
      if (pathString.length() > 5 && pathString.substring(0, 5).equalsIgnoreCase("data:")) {
        final Pattern pattern = Pattern
            .compile(
                "([dD][aA][tT][aA]):([a-zA-Z]*/[a-zA-Z0-9]*)(;[cC][hH][aA][rR][sS][eE][tT]=([0-9a-zA-Z\\-]*))?(;([bB][aA][sS][eE]64))?,([0-9a-zA-Z/+=]*)"); //$NON-NLS-1$
        final Matcher matcher = pattern.matcher(pathString);
        if (matcher.matches()) {
          final String mimeType = matcher.group(2);
          final String encoding = matcher.group(4);
          final String code = matcher.group(6);
          final String data = matcher.group(7);
          if ("base64".equals(code)) {
            return new MemoryResourceReference(Base64.getDecoder().decode(data), mimeType, encoding);
          }
          return new MemoryResourceReference(URLDecoder.decode(data, encoding).getBytes(encoding), mimeType, encoding);
        }
        throw new CreationException(MessageFormat.format(UriUtilities.ERROR_MESSAGE, pathString));
      }
      if (UriUtilities.isFileUrl(pathString)) {
        final URL url = new URL(pathString);
        if (url.getHost() != null && url.getHost().trim().length() > 0) {
          return new UrlResourceReference(url);
        }
        final String filePath = url.getFile();
        // if (FileUtilities.isAbsoluteWindowsFilePath(filePath)) {
        // final String absoluteWindowsFilePath = filePath.startsWith("/") ? filePath : "/" + filePath;
        // final URL fileUrl = new URL(MessageFormat.format("file:{0}", absoluteWindowsFilePath));
        // return new UrlResourceReference(fileUrl);
        // }
        return create(new File(filePath).toPath());
      }
      return create(new File(pathString).toPath());
    } catch (final MalformedURLException exception) {
      throw new CreationException(MessageFormat.format(UriUtilities.ERROR_MESSAGE, pathString), exception);
    } catch (final UnsupportedEncodingException exception) {
      throw new CreationException(MessageFormat.format(UriUtilities.ERROR_MESSAGE, pathString), exception);
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
