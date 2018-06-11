/*
 * #%L
 * anwiba commons advanced
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

package net.anwiba.http.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.anwiba.commons.reference.utilities.IoUtilities;

public class RequestProcessorBuilder {

  private IResponseContentProvider responseContentProvider = null;
  private IRequestRecorder recorder = new IRequestRecorder() {

    @Override
    public void add(final HttpServletRequest request) {
      // nothing to do
    }
  };
  private int statusCode;
  private String contentType;

  public AbstractRequestProcessor build() {
    return new AbstractRequestProcessor() {

      @Override
      protected void process(final HttpServletRequest request, final HttpServletResponse response)
          throws IOException,
          ServletException {
        RequestProcessorBuilder.this.recorder.add(request);
        try {
          response.setStatus(RequestProcessorBuilder.this.statusCode);
          response.setContentLength(-1);
          response.setContentType(RequestProcessorBuilder.this.contentType);
          final byte[] responseBytes = RequestProcessorBuilder.this.responseContentProvider.getResponseBytes();
          response.setContentLength(responseBytes.length);
          response.getOutputStream().write(responseBytes);
          response.flushBuffer();
        } catch (final IOException exception) {
          exception.printStackTrace();
          throw exception;
        } catch (final RuntimeException exception) {
          exception.printStackTrace();
          throw exception;
        }
      }
    };
  }

  public RequestProcessorBuilder setRequestRecorder(final IRequestRecorder recorder) {
    this.recorder = recorder;
    return this;
  }

  public RequestProcessorBuilder setStatusCode(final int statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  public RequestProcessorBuilder setContentType(final String contentType) {
    this.contentType = contentType;
    return this;
  }

  public RequestProcessorBuilder setResponseContent(final File file) {
    this.responseContentProvider = new IResponseContentProvider() {

      @Override
      public byte[] getResponseBytes() throws IOException {
        return getResponseBytes(file);
      }

      private byte[] getResponseBytes(@SuppressWarnings("hiding") final File file)
          throws FileNotFoundException,
          IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
          final byte[] responseBytes = getBytes(fileInputStream);
          return responseBytes;
        }
      }

      private byte[] getBytes(final InputStream fileInputStream) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IoUtilities.pipe(fileInputStream, byteArrayOutputStream);
        final byte[] response = byteArrayOutputStream.toByteArray();
        return response;
      }
    };
    return this;
  }

  public RequestProcessorBuilder setResponseContent(final String string) {
    this.responseContentProvider = new IResponseContentProvider() {

      @Override
      public byte[] getResponseBytes() throws IOException {
        return string.getBytes();
      }

    };
    return this;
  }

  public RequestProcessorBuilder setResponseContent(final byte[] array) {
    this.responseContentProvider = new IResponseContentProvider() {

      @Override
      public byte[] getResponseBytes() throws IOException {
        return array;
      }

    };
    return this;
  }

}
