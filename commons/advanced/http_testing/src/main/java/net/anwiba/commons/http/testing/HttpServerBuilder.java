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

package net.anwiba.commons.http.testing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jetty.server.Server;

import jakarta.servlet.http.HttpServletRequest;
import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.object.ObjectPair;

public class HttpServerBuilder {

  private int port = 8000;
  private final List<
      ObjectPair<IApplicable<ObjectPair<String, HttpServletRequest>>, AbstractRequestProcessor>> processors =
          new ArrayList<>();
  private AbstractRequestProcessor fallbackProcessor;

  public Server build() {
    final Server server = new Server(this.port);
    server.setHandler(new RequestHandler(this.processors, this.fallbackProcessor));
    return server;
  }

  public HttpServerBuilder setPort(final int port) {
    this.port = port;
    return this;
  }

  public HttpServerBuilder addProcessorForTarget(final String target, final AbstractRequestProcessor processor) {
    return addProcessorForTarget(value -> Objects.equals(value, target), processor);
  }

  public HttpServerBuilder addProcessorForTarget(final IApplicable<String> applicable,
      final AbstractRequestProcessor processor) {
    this.processors.add(ObjectPair.of(context -> applicable.isApplicable(context.getFirstObject()), processor));
    return this;
  }

  public HttpServerBuilder addProcessorForRequest(final IApplicable<HttpServletRequest> applicable,
      final AbstractRequestProcessor processor) {
    this.processors.add(ObjectPair.of(context -> applicable.isApplicable(context.getSecondObject()), processor));
    return this;
  }

  public HttpServerBuilder setFallbackProcessor(final AbstractRequestProcessor fallbackProcessor) {
    this.fallbackProcessor = fallbackProcessor;
    return this;
  }
}
