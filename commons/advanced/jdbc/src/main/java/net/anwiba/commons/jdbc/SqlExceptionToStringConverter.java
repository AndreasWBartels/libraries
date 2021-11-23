/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
package net.anwiba.commons.jdbc;

import java.sql.SQLException;

import net.anwiba.commons.lang.exception.IThrowableToStringConverter;
import net.anwiba.commons.utilities.string.StringUtilities;

public class SqlExceptionToStringConverter implements IThrowableToStringConverter {

  @Override
  public boolean isApplicable(Throwable throwable) {
    return throwable instanceof SQLException;
  }

  @Override
  public String toString(Throwable throwable) {
    SQLException sqlException = (SQLException) throwable;

    String reason = sqlException.getMessage();
    String state = sqlException.getSQLState();
    int errorCode = sqlException.getErrorCode();

    StringBuilder builder = new StringBuilder();
    builder.append(reason);
    
    if (errorCode != 0) {
      builder.append("; code=");
      builder.append(errorCode);
    }

    if (!StringUtilities.isNullOrEmpty(state)) {
      builder.append("; state=");
      builder.append(state);
    }

    return builder.toString();
  }

}
