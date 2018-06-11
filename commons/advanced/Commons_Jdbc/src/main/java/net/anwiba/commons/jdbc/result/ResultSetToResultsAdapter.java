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
package net.anwiba.commons.jdbc.result;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.NoSuchElementException;

@SuppressWarnings("nls")
public class ResultSetToResultsAdapter implements IResults {

  private final ResultSet resultSet;
  private IResult result = null;

  public ResultSetToResultsAdapter(final ResultSet resultSet) {
    this.resultSet = resultSet;
  }

  @Override
  public boolean hasNext() throws SQLException {
    if (this.result != null) {
      return true;
    }
    try {
      this.result = this.resultSet.next() ? new ResultSetToResultAdapter(this.resultSet) : null;
      return this.result != null;
    } catch (final SQLRecoverableException exception) {
      if (exception.getMessage().contains("interrupted") || exception.getMessage().contains("Closed Connection")) {
        return false;
      }
      throw exception;
    }
  }

  @Override
  public IResult next() throws SQLException {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    try {
      return this.result;
    } finally {
      this.result = null;
    }
  }

  @Override
  public void close() throws SQLException {
    try {
      this.resultSet.close();
    } catch (final SQLRecoverableException exception) {
      if (exception.getMessage().contains("interrupted") || exception.getMessage().contains("Closed Connection")) {
        return;
      }
      throw exception;
    }
  }

  @Override
  public boolean isClosed() throws SQLException {
    return this.resultSet.isClosed();
  }
}
