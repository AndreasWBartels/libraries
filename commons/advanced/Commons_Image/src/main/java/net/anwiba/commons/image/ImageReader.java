/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2016 Andreas W. Bartels
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
package net.anwiba.commons.image;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import com.sun.media.jai.codec.MemoryCacheSeekableStream;

import net.anwiba.commons.http.IHttpRequestExecutor;
import net.anwiba.commons.http.IHttpRequestExecutorFactory;
import net.anwiba.commons.http.IRequest;
import net.anwiba.commons.http.IResponse;
import net.anwiba.commons.http.RequestBuilder;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.resource.reference.IResourceReference;
import net.anwiba.commons.resource.reference.IResourceReferenceHandler;
import net.anwiba.commons.resource.utilities.IoUtilities;
import net.anwiba.commons.thread.cancel.ICanceler;
import net.anwiba.commons.utilities.io.url.IUrl;
import net.anwiba.commons.utilities.io.url.UrlBuilder;
import net.anwiba.commons.utilities.io.url.parser.UrlParser;
import net.anwiba.commons.utilities.string.StringUtilities;

public final class ImageReader implements IImageReader {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ImageReader.class);
  private final IResourceReferenceHandler handler;
  private final IHttpRequestExecutorFactory httpRequestExcecutorFactory;

  public ImageReader(
      final IResourceReferenceHandler handler,
      final IHttpRequestExecutorFactory httpRequestExcecutorFactory) {
    super();
    this.handler = handler;
    this.httpRequestExcecutorFactory = httpRequestExcecutorFactory;
  }

  @Override
  public IImageContainer read(final ICanceler canceler, final IResourceReference resourceReference)
      throws InterruptedException,
      IOException {
    canceler.check();

    if (this.handler.isFileSystemResource(resourceReference)) {
      return read(canceler, this.handler.openInputStream(resourceReference));
    }

    if (!this.handler.hasLocation(resourceReference)) {
      return read(canceler, this.handler.openInputStream(resourceReference));
    }

    final URL url = this.handler.getUrl(resourceReference);
    if (!(StringUtilities.equalsIgnoreCase(url.getProtocol(), "http")
        || StringUtilities.equalsIgnoreCase(url.getProtocol(), "https"))) {
      try (final InputStream stream = url.openStream()) {
        return read(canceler, IoUtilities.copy(stream));
      }
    }

    final IRequest request = RequestBuilder.get(this.handler.toString(resourceReference)).build();

    try (final IHttpRequestExecutor executor = this.httpRequestExcecutorFactory.create()) {
      try (final IResponse response = executor.execute(canceler, request)) {
        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {

          if (!response.getContentType().toLowerCase().startsWith("image")) {
            logger.log(ILevel.ERROR, "unexpected content type '" + response.getContentType() + "'");
            if (logger.isLoggable(ILevel.ERROR)) {
              logger.log(ILevel.ERROR, response.getBody());
            }
            throw new IOException("unexpected content type '" + response.getContentType() + "'");
          }

          try (final InputStream stream = response.getInputStream()) {
            return read(canceler, IoUtilities.copy(stream));
          }
        }
        logger.log(
            ILevel.DEBUG,
            "connect to '"
                + toPrintableString(resourceReference)
                + "' faild "
                + response.getStatusCode()
                + " "
                + response.getStatusText());
        if (logger.isLoggable(ILevel.DEBUG)) {
          logger.log(ILevel.DEBUG, response.getBody());
        }
        throw new IOException(response.getStatusCode() + " " + response.getStatusText());
      } catch (final IOException exception) {
        throw new IOException(
            "Couldn't read '" + toPrintableString(resourceReference) + "', " + exception.getMessage(),
            exception);
      }
    }
  }

  private String toPrintableString(final IResourceReference resourceReference) {
    final String string = this.handler.toString(resourceReference);
    try {
      final IUrl url = new UrlParser().parse(string);
      if (url.getPassword() != null) {
        return new UrlBuilder(url).setPassword("**********").build().toString(); //$NON-NLS-1$
      }
      return new UrlBuilder(url).build().toString();
    } catch (final CreationException exception) {
      return string;
    }
  }

  @Override
  public IImageContainer read(final ICanceler canceler, final InputStream inputStream)
      throws InterruptedException,
      IOException {
    canceler.check();
    return new PlanarImageContainer(createRenderOp(inputStream));
  }

  @SuppressWarnings("resource")
  private RenderedOp createRenderOp(final InputStream inputStream) {
    final MemoryCacheSeekableStream memoryCacheSeekableStream = new MemoryCacheSeekableStream(inputStream);
    return JAI.create("Stream", memoryCacheSeekableStream); //$NON-NLS-1$
  }
}
