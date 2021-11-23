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
package net.anwiba.commons.datasource.resource;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.time.Clock;
import java.time.ZonedDateTime;

import net.anwiba.commons.datasource.connection.FileConnectionDescription;
import net.anwiba.commons.datasource.connection.HttpConnectionDescription;
import net.anwiba.commons.datasource.connection.IConnectionDescription;
import net.anwiba.commons.datasource.connection.MemoryConnectionDescription;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.functional.ConversionException;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.reference.FileResourceReference;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.reference.IResourceReferenceVisitor;
import net.anwiba.commons.reference.MemoryResourceReference;
import net.anwiba.commons.reference.PathResourceReference;
import net.anwiba.commons.reference.ResourceReferenceFactory;
import net.anwiba.commons.reference.UriResourceReference;
import net.anwiba.commons.reference.UrlResourceReference;
import net.anwiba.commons.utilities.io.url.IAuthentication;
import net.anwiba.commons.utilities.io.url.IUrl;

@SuppressWarnings("nls")
public class ResourceDescription implements IResourceDescription {

  private static final long serialVersionUID = 1L;
  private final IConnectionDescription connectionDescription;

  public static ResourceDescription of(final String string) throws CreationException {
    return of(new ResourceReferenceFactory().create(string));
  }

  public static ResourceDescription of(final IResourceReference resourceReference) throws CreationException {
    final IConnectionDescription connectionDescription =
        resourceReference.accept(new IResourceReferenceVisitor<IConnectionDescription, CreationException>() {

          @Override
          public IConnectionDescription visitFileResource(final FileResourceReference fileResourceReference)
              throws RuntimeException {
            return new FileConnectionDescription(fileResourceReference);
          }

          @Override
          public IConnectionDescription visitUrlResource(final UrlResourceReference urlResourceReference)
              throws CreationException {
            final URL inUrl = urlResourceReference.getUrl();
            if ("file".equals(inUrl.getProtocol())) {
              return new FileConnectionDescription(new ResourceReferenceFactory().create(new File(inUrl.getPath())));
            }
            try {
              final IUrl url = new StringToUrlConverter().convert(inUrl.toString());
              if (url == null) {
                throw new CreationException("Couldn't create resource description for url '" + inUrl.toString() + "'");
              }
              return new HttpConnectionDescription(
                  url.getHostname(),
                  url.getPort(),
                  url.getPathString(),
                  url.getUserName(),
                  url.getPassword(),
                  url.getQuery(),
                  Optional
                      .of(url.getScheme())
                      .accept(s -> !s.isEmpty())
                      .convert(s -> s.get(s.size() - 1))
                      .accept(p -> "https".equalsIgnoreCase(p)) //$NON-NLS-1$
                      .isAccepted());
            } catch (final ConversionException exception) {
              throw new CreationException(
                  "Couldn't create resource description for url '" + inUrl.toString() + "'",
                  exception);
            }
          }

          @Override
          public IConnectionDescription visitUriResource(final UriResourceReference uriResourceReference)
              throws RuntimeException,
              CreationException {
            final URI uri = uriResourceReference.getUri();
            if ("file".equals(uri.getScheme())) {
              return new FileConnectionDescription(new ResourceReferenceFactory().create(new File(uri.getPath())));
            }
            try {
              final IUrl url = new StringToUrlConverter().convert(uri.toString());
              if (url == null) {
                throw new CreationException("Couldn't create resource description for url '" + uri.toString() + "'");
              }
              return new HttpConnectionDescription(
                  url.getHostname(),
                  url.getPort(),
                  url.getPathString(),
                  url.getUserName(),
                  url.getPassword(),
                  url.getQuery(),
                  Optional
                      .of(url.getScheme())
                      .accept(s -> !s.isEmpty())
                      .convert(s -> s.get(s.size() - 1))
                      .accept(p -> "https".equalsIgnoreCase(p)) //$NON-NLS-1$
                      .isAccepted());
            } catch (final ConversionException exception) {
              throw new CreationException(
                  "Couldn't create resource description for url '" + uri.toString() + "'",
                  exception);
            }
          }

          @Override
          public IConnectionDescription visitMemoryResource(final MemoryResourceReference memoryResourceReference)
              throws RuntimeException {
            return new MemoryConnectionDescription(
                memoryResourceReference,
                memoryResourceReference.getContentType(),
                ZonedDateTime.ofInstant(memoryResourceReference.creationTime().toInstant(),
                    Clock.systemDefaultZone().getZone()));
          }

          @Override
          public IConnectionDescription visitPathResource(final PathResourceReference pathResourceReference)
              throws RuntimeException {
            return new FileConnectionDescription(pathResourceReference);
          }
        });
    return new ResourceDescription(connectionDescription);
  }

  public static ResourceDescription of(final IConnectionDescription connectionDescription) {
    return new ResourceDescription(connectionDescription);
  }

  ResourceDescription(final IConnectionDescription connectionDescription) {
    this.connectionDescription = connectionDescription;
  }

  @Override
  public IConnectionDescription getConnectionDescription() {
    return this.connectionDescription;
  }

  @Override
  public String getUrl() {
    return this.connectionDescription.getUrl();
  }

  @Override
  public IResourceDescription adapt(final IAuthentication authentication) {
    return new ResourceDescription(this.connectionDescription.adapt(authentication));
  }

}
