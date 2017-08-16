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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.object.ObjectPair;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public final class RequestHandler extends AbstractHandler {

  private final List<ObjectPair<IApplicable<String>, AbstractRequestProcessor>> processors = new ArrayList<>();

  public void addProcessor(final IApplicable<String> applicable, final AbstractRequestProcessor processor) {
    this.processors.add(new ObjectPair<>(applicable, processor));
  }

  public void addProcessor(final String path, final AbstractRequestProcessor processor) {
    this.processors.add(new ObjectPair<IApplicable<String>, AbstractRequestProcessor>(
        i -> Objects.equals(i, path),
        processor));
  }

  @Override
  public void handle(
      final String target,
      final Request baseRequest,
      final HttpServletRequest request,
      final HttpServletResponse response) throws IOException, ServletException {
    for (final ObjectPair<IApplicable<String>, AbstractRequestProcessor> processor : this.processors) {
      if (processor.getFirstObject().isApplicable(target)) {
        processor.getSecondObject().process(request, response);
        return;
      }
    }
  }
}
