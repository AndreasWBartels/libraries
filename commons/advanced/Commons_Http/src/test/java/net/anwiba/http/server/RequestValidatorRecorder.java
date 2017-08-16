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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import javax.servlet.http.HttpServletRequest;

public final class RequestValidatorRecorder implements IRequestRecorder {
  private final String[] expectedQueries;
  private final Queue<AbstractRequestValidator> serverRequests;

  public RequestValidatorRecorder(
      final Queue<AbstractRequestValidator> serverRequests,
      final String... expectedQueries) {
    this.serverRequests = serverRequests;
    this.expectedQueries = expectedQueries;
  }

  @Override
  public void add(final HttpServletRequest request) {
    final String query = request.getQueryString();
    final Queue<String> expectedQueryStack = new LinkedList<>(Arrays.asList(this.expectedQueries));
    final String expectedQuery = expectedQueryStack.poll();
    this.serverRequests.add(new RequestedQueryStringValidator(expectedQuery, query));
  }
} 
