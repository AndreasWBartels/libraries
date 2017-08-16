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

import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class ServerSampleTest {

  protected static Server httpServer;
  protected static final Queue<AbstractRequestValidator> serverRequests = new LinkedList<>();

  @BeforeClass
  public static void startServer() throws Exception {
    final String[] expectedQueries = new String[]{ "f=json" }; //$NON-NLS-1$
    httpServer = new HttpServerBuilder() //
        .setPort(8000)
        .add("/ArcGIS/rest/services/point/FeatureServer", //$NON-NLS-1$
            new RequestProcessorBuilder() //
                .setContentType("text/plain;charset=utf-8") //$NON-NLS-1$
                .setStatusCode(HttpURLConnection.HTTP_OK)
                .setRequestRecorder(new RequestValidatorRecorder(serverRequests, expectedQueries))
                .setResponseContent("foo") //$NON-NLS-1$
                .build())
        .build();
    httpServer.start();
  }

  @AfterClass
  public static void stopServer() throws Exception {
    httpServer.stop();
  }

}
