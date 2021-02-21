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

public final class SqliteCapabilities implements ISqliteCapabilities {

  private final IVersion sqliteVersion;
  private final IVersion spatiaLiteVersion;
  private final IVersion spatiaLiteDatabaseVersion;

  public SqliteCapabilities(
    final IVersion sqliteVersion,
    final IVersion spatiaLiteVersion,
    final IVersion spatiaLiteDatabaseVersion) {
    this.sqliteVersion = sqliteVersion;
    this.spatiaLiteVersion = spatiaLiteVersion;
    this.spatiaLiteDatabaseVersion = spatiaLiteDatabaseVersion;
  }

  public IVersion getSqliteVersion() {
    return this.sqliteVersion;
  }

  public IVersion getSpatiaLiteVersion() {
    return this.spatiaLiteVersion;
  }

  public IVersion getSpatiaLiteDatabaseVersion() {
    return this.spatiaLiteDatabaseVersion;
  }

  @Override
  public boolean canChange() {
    if (this.spatiaLiteDatabaseVersion == null || this.spatiaLiteVersion == null) {
      return false;
    }
    if (this.spatiaLiteDatabaseVersion.getMajor() > 3 && this.spatiaLiteVersion.getMajor() > 3) {
      return true;
    }
    if (this.spatiaLiteDatabaseVersion.getMajor() < 4 && this.spatiaLiteVersion.getMajor() < 4) {
      return true;
    }
    return false;
  }

  @Override
  public boolean isExtended() {
    return this.spatiaLiteVersion != null;
  }
}