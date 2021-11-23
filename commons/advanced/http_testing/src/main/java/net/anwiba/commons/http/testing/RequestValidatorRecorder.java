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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import jakarta.servlet.http.HttpServletRequest;
import net.anwiba.commons.lang.optional.Optional;

public final class RequestValidatorRecorder implements IRequestRecorder {
  private final String[] expectedQueries;
  private final Queue<AbstractRequestValidator> requestValidators;
  private final Queue<String> expectedQueryStack;

  public RequestValidatorRecorder(
      final Queue<AbstractRequestValidator> requestValidators,
      final String... expectedQueries) {
    this.requestValidators = requestValidators;
    this.expectedQueries = expectedQueries;
    this.expectedQueryStack = new LinkedList<>(Arrays.asList(this.expectedQueries));
  }

  @Override
  public void add(final HttpServletRequest request) {
    Optional.of(this.expectedQueryStack.poll())
        .consume(expectedQuery -> this.requestValidators
            .add(new RequestedQueryStringValidator(expectedQuery, request.getQueryString())));
  }
}
