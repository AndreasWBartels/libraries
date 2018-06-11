/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.commons.swing.database.console.result;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.anwiba.commons.model.IObjectModel;

public class ResultReseter {

  private final IObjectModel<Statement> statementModel;
  private final IObjectModel<ResultSet> resultSetModel;

  public ResultReseter(final IObjectModel<Statement> statementModel, final IObjectModel<ResultSet> resultSetModel) {
    super();
    this.statementModel = statementModel;
    this.resultSetModel = resultSetModel;
  }

  public void reset() {
    if (this.resultSetModel.get() != null) {
      try {
        @SuppressWarnings("resource")
        final ResultSet set = this.resultSetModel.get();
        this.resultSetModel.set(null);
        set.close();
      } catch (final SQLException exception) {
        // nothing to do
      }
    }
    if (this.statementModel.get() != null) {
      try {
        @SuppressWarnings("resource")
        final Statement statement = this.statementModel.get();
        this.statementModel.set(null);
        statement.close();
      } catch (final SQLException exception) {
        // nothing to do
      }
    }
  }
}
