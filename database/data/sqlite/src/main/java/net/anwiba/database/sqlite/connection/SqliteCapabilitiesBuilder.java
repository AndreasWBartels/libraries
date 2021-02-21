/*
 * #%L
 * anwiba commons database
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
package net.anwiba.database.sqlite.connection;

import net.anwiba.commons.version.IVersion;
import net.anwiba.commons.version.VersionUtilities;

public class SqliteCapabilitiesBuilder {

  private String sqliteVersionString;
  private String spatiaLiteDatabaseVersionString;
  private String spatiaLiteVersionString;

  public void setSqliteVersion(final String sqliteVersion) {
    this.sqliteVersionString = sqliteVersion;
  }

  public void setSpatiaLiteVersion(final String spatiaLiteVersion) {
    this.spatiaLiteVersionString = spatiaLiteVersion;
  }

  public void setSpatiaLiteDatabaseVersion(final String spatiaLiteDatabaseVersion) {
    this.spatiaLiteDatabaseVersionString = spatiaLiteDatabaseVersion;
  }

  public ISqliteCapabilities build() {
    final IVersion sqliteVersion = VersionUtilities.valueOf(this.sqliteVersionString);
    final IVersion spatiaLiteDatabaseVersion = VersionUtilities.valueOf(this.spatiaLiteDatabaseVersionString);
    final IVersion spatiaLiteVersion = VersionUtilities.valueOf(this.spatiaLiteVersionString);

    return new SqliteCapabilities(sqliteVersion, spatiaLiteVersion, spatiaLiteDatabaseVersion);
  }
}
