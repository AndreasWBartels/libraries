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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.anwiba.commons.http.testing.AbstractRequestProcessor.Continue;
import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.object.ObjectPair;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

public final class RequestHandler extends AbstractHandler {

  private final List<
      ObjectPair<IApplicable<ObjectPair<String, HttpServletRequest>>, AbstractRequestProcessor>> processors =
          new ArrayList<>();
  private final IOptional<AbstractRequestProcessor, RuntimeException> fallbackProcessor;

  public RequestHandler(
      final List<ObjectPair<IApplicable<ObjectPair<String, HttpServletRequest>>, AbstractRequestProcessor>> processors,
      final AbstractRequestProcessor fallbackProcessor) {
    this.processors.addAll(processors);
    this.fallbackProcessor = Optional.of(fallbackProcessor);
  }

  @Override
  public void handle(final String target,
      final Request baseRequest,
      final HttpServletRequest request,
      final HttpServletResponse response) throws IOException,
      ServletException {
    if (Objects.equals(request.getMethod(), "CONNECT")) {
      response.sendError(405);
      return;
    }
    for (final ObjectPair<IApplicable<ObjectPair<String, HttpServletRequest>>,
        AbstractRequestProcessor> processor : this.processors) {
      if (processor.getFirstObject().isApplicable(ObjectPair.of(target, baseRequest))) {
        Continue continuing = processor.getSecondObject().process(request, response);
        if (Objects.equals(continuing, Continue.FALSE)) {
          return;
        }
      }
    }
    if (this.fallbackProcessor.isAccepted()) {
      Continue continuing = this.fallbackProcessor.get().process(request, response);
      if (Objects.equals(continuing, Continue.FALSE)) {
        return;
      }
    }
  }
}
