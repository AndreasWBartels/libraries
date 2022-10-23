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

package net.anwiba.commons.http;

import net.anwiba.commons.http.testing.ConnectionValuesValidator;
import net.anwiba.commons.http.testing.HttpServerBuilder;
import net.anwiba.commons.http.testing.IRequestRecorder;
import net.anwiba.commons.http.testing.RequestCountValidator;
import net.anwiba.commons.http.testing.RequestProcessorBuilder;
import net.anwiba.commons.lang.exception.CanceledException;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.logging.LoggingUtilities;
import net.anwiba.commons.thread.cancel.Canceler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Set;

import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HttpRequestExecutorTest {

  static final ConnectionValuesValidator connectionValueValidator = new ConnectionValuesValidator();
  static final RequestCountValidator requestCountValidator = new RequestCountValidator();

  private static Server httpServer;

  @BeforeAll
  public static void startServer() throws Exception {
    LoggingUtilities.initialize("debug", "org.apache.hc.client5.http.protocol"
    //         , "org.apache.hc.client5.http"
        , "org.apache.hc.client5.http.headers"
    //        , "org.apache.hc.client5.http.impl.classic"
    //        , "org.apache.hc.client5.http.impl.io"
    //        , "org.apache.hc.client5.http.ssl"
    );
    final IRequestRecorder recorder = request -> {
      requestCountValidator.add(request);
      connectionValueValidator.add(request);
    };
    httpServer = new HttpServerBuilder() //
        .setPort(8081)
        .addProcessorForTarget("/same", //$NON-NLS-1$
            new RequestProcessorBuilder() //
                .setContentType("text/plain; charset=utf-8") //$NON-NLS-1$
                .setStatusCode(HttpURLConnection.HTTP_OK)
                .setRequestRecorder(recorder)
                .setResponseContent("succesful") //$NON-NLS-1$
                .build())
        .addProcessorForTarget("/new", //$NON-NLS-1$
            new RequestProcessorBuilder() //
                .setContentType("text/plain; charset=utf-8") //$NON-NLS-1$
                .setStatusCode(HttpURLConnection.HTTP_OK)
                .setRequestRecorder(recorder)
                .setResponseContent("succesful") //$NON-NLS-1$
                .build())
        .addProcessorForTarget("/pool", //$NON-NLS-1$
            new RequestProcessorBuilder() //
                .setContentType("text/plain; charset=utf-8") //$NON-NLS-1$
                .setStatusCode(HttpURLConnection.HTTP_OK)
                .setRequestRecorder(recorder)
                .setResponseContent("succesful") //$NON-NLS-1$
                .build())
        .addProcessorForTarget("/tv", //$NON-NLS-1$
            new RequestProcessorBuilder() //
                .setContentType("text/plain; charset=utf-8") //$NON-NLS-1$
                .setStatusCode(HttpURLConnection.HTTP_OK)
                .setRequestRecorder(recorder)
                .setResponseContent("succesful") //$NON-NLS-1$
                .build())
        .setFallbackProcessor(new RequestProcessorBuilder() //
            .setContentType("text/plain; charset=utf-8") //$NON-NLS-1$
            .setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST)
            .setResponseContent("faild") //$NON-NLS-1$
            .build())
        //        .setFallbackProcessor(new AbstractRequestProcessor() {
        //
        //          @Override
        //          protected Continue process(final HttpServletRequest request, final HttpServletResponse response)
        //              throws IOException,
        //              ServletException {
        //            return Continue.TRUE;
        //          }
        //        })
        .build();
    httpServer.start();
  }

  @BeforeEach
  public void reset() throws Exception {
    requestCountValidator.reset();
    connectionValueValidator.reset();
  }

  @Test
  public void proxy() throws Exception {
    try (final IHttpRequestExecutor executor = new HttpRequestExecutorFactoryBuilder()
        .setProxy("http", "localhost", 8081)
        .useAlwaysANewConnection()
        .build()
        .create()) {
      execute(executor, "http://anwiba.net/new", 1);
    }
    requestCountValidator.assertEquals(1);
    connectionValueValidator.assertEquals(Set.of("close"));
  }

  @Test
  public void useAlwaysANewConnection() throws Exception {
    try (final IHttpRequestExecutor executor = new HttpRequestExecutorFactoryBuilder()
        .useAlwaysANewConnection()
        .build()
        .create()) {
      execute(executor, "/new");
    }
    try (final IHttpRequestExecutor executor = new HttpRequestExecutorFactoryBuilder()
        .useAlwaysANewConnection()
        .build()
        .create()) {
      execute(executor, "/new");
    }
    requestCountValidator.assertEquals(20);
    connectionValueValidator.assertEquals(Set.of("close"));
  }

  @Test
  public void useAlwaysTheSameConnection() throws Exception {
    try (final IHttpRequestExecutor executor = new HttpRequestExecutorFactoryBuilder()
        .useAlwaysTheSameConnection()
        .build()
        .create()) {
      execute(executor, "/same");
    }
    try (final IHttpRequestExecutor executor = new HttpRequestExecutorFactoryBuilder()
        .useAlwaysTheSameConnection()
        .build()
        .create()) {
      execute(executor, "/same");
    }
    requestCountValidator.assertEquals(20);
    connectionValueValidator.assertEquals(Set.of("keep-alive"));
  }

  @Test
  public void usePoolingConnection() throws Exception {
    try (final IHttpRequestExecutor executor = new HttpRequestExecutorFactoryBuilder()
        .usePoolingConnection()
        .build()
        .create()) {
      execute(executor, "/pool");
    }
    try (final IHttpRequestExecutor executor = new HttpRequestExecutorFactoryBuilder()
        .usePoolingConnection()
        .build()
        .create()) {
      execute(executor, "/pool");
    }
    requestCountValidator.assertEquals(20);
    connectionValueValidator.assertEquals(Set.of("keep-alive"));
  }

  private void execute(final IHttpRequestExecutor executor, final String path) throws CanceledException,
      IOException,
      CreationException {
    execute(executor, "http://localhost:8081" + path, 10);
  }

  protected void execute(final IHttpRequestExecutor executor, final String url, final int numberOfRequests)
      throws IOException,
      CanceledException,
      CreationException {
    for (int i = 0; i < numberOfRequests; i++) {
      try (IResponse response = executor.execute(Canceler.DummyCanceler, RequestBuilder.get(url).build())) {
        assertResponse(response);
      }
    }
  }

  protected void assertResponse(final IResponse response) throws IOException {
    Assertions.assertNotNull(response);
    Assertions.assertEquals(200, response.getStatusCode());
    Assertions.assertEquals("text/plain;charset=utf-8", response.getContentType());
    Assertions.assertEquals("utf-8", response.getContentEncoding());
    Assertions.assertEquals(9, response.getContentLength());
    Assertions.assertEquals("succesful", response.getBody());
  }

  @AfterAll
  public static void stopServer() throws Exception {
    httpServer.stop();
  }

}
