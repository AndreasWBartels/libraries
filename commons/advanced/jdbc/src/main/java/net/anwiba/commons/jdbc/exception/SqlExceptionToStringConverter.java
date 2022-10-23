/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2022 Andreas W. Bartels
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
package net.anwiba.commons.jdbc.exception;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import net.anwiba.commons.lang.exception.IThrowableToStringConverter;
import net.anwiba.commons.lang.exception.ThrowableConverterResult;
import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.optional.Optional;

public class SqlExceptionToStringConverter implements IThrowableToStringConverter {

  @Override
  public IOptional<Class<? extends Throwable>, RuntimeException> getThrowableClass() {
    return Optional.of(SQLException.class);
  }

  @Override
  public boolean isApplicable(final Throwable throwable) {
    return throwable instanceof SQLException;
  }

  @Override
  public ThrowableConverterResult convert(final Throwable throwable) {
    return ThrowableConverterResult.of(toString(throwable));
  }

  public String toString(final Throwable throwable) {
    List<String> messages = new LinkedList<>();
    addTo(messages, throwable);
    return String.join("; ", messages);
  }

  protected void addTo(final List<String> messages, final Throwable throwable) {
    SQLException sqlException = (SQLException) throwable;
    String reason = sqlException.getMessage();
    String state = sqlException.getSQLState();
    int errorCode = sqlException.getErrorCode();
    if (errorCode != 0) {
      addTo(messages, "code", errorCode);
    }
    addTo(messages, "state", state);
    messages.add(reason);
  }
}
