/*
 * #%L
 * jgisshell
 * %%
 * Copyright (C) 2007 - 2021 Andreas Bartels
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
package net.anwiba.database.sqlite.utilities;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.anwiba.commons.jdbc.DatabaseUtilities;
import net.anwiba.commons.jdbc.connection.IJdbcConnectionDescription;
import net.anwiba.commons.jdbc.database.IDatabaseVersionListFactory;
import net.anwiba.commons.jdbc.result.IResult;
import net.anwiba.commons.jdbc.software.FileDatabaseSoftware;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.object.ObjectPair;

public class SqliteDatabaseVersionsListFactory implements IDatabaseVersionListFactory {

  @Override
  public boolean isApplicable(final IJdbcConnectionDescription context) {
    return context != null
        && Objects.equals(context.getDatabaseSoftware(), FileDatabaseSoftware.SQLITE);
  }

  @Override
  public List<ObjectPair<String, String>> create(final Connection connection) throws SQLException {
    final List<ObjectPair<String, String>> result = new ArrayList<>();
    String databaseVersion = DatabaseUtilities.result(connection,
        "select sqlite_version()",
        optional -> optional.convert(r -> r.getString(1)).get());
    if (databaseVersion != null) {
      result.add(ObjectPair.of("Database", databaseVersion));
    }
    try {
      String spatiaLiteVersion = DatabaseUtilities.result(connection,
          "select spatialite_version()()",
          optional -> optional.convert(r -> r.getString(1)).get());
      if (spatiaLiteVersion != null) {
        result.add(ObjectPair.of("SpatiaLite", spatiaLiteVersion));
      }
    } catch (SQLException exception) {
      // nothing todo
    }
    final List<ObjectPair<String, String>> extensions = DatabaseUtilities.results(connection,
        "PRAGMA module_list",
        (IConverter<IResult, ObjectPair<String, String>,
            SQLException>) r -> ObjectPair.of(r.getString(1), null));
    result.addAll(extensions);
    return result;
  }

}
