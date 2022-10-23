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
package net.anwiba.database.oracle.utilities;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.anwiba.commons.jdbc.DatabaseUtilities;
import net.anwiba.commons.jdbc.connection.IJdbcConnectionDescription;
import net.anwiba.commons.jdbc.database.IDatabaseVersionListFactory;
import net.anwiba.commons.jdbc.result.IResult;
import net.anwiba.commons.jdbc.software.ServiceDatabaseSoftware;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.object.ObjectPair;

public class OracleDatabaseVersionsListFactory implements IDatabaseVersionListFactory {

  @Override
  public boolean isApplicable(final IJdbcConnectionDescription context) {
    return context != null
        && Objects.equals(context.getDatabaseSoftware(), ServiceDatabaseSoftware.ORACLE);
  }

  @Override
  public List<ObjectPair<String, String>> create(final Connection connection) throws SQLException {
    final List<ObjectPair<String, String>> result = new ArrayList<>();
    final List<ObjectPair<String, String>> extensions = DatabaseUtilities.results(connection,
        "SELECT PRODUCT, VERSION FROM PRODUCT_COMPONENT_VERSION",
        (IConverter<IResult, ObjectPair<String, String>,
            SQLException>) r -> ObjectPair.of(r.getString(1), r.getString(2)));
    result.addAll(extensions);
    return result;
  }

}
