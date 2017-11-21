/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.commons.jdbc.database;

import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.jdbc.connection.IJdbcConnectionDescription;

public class DatabaseFacadeProvider implements IDatabaseFacadeProvider, IDatabaseFacadeRegistry {

  final DatabaseFacade databaseFacade = new DatabaseFacade();
  private final List<IRegistrableDatabaseFacade> facades = new ArrayList<>();

  public DatabaseFacadeProvider(final List<IRegistrableDatabaseFacade> facades) {
    this.facades.addAll(facades);
  }

  @Override
  public void add(final IRegistrableDatabaseFacade facade) {
    this.facades.add(facade);
  }

  @Override
  public IDatabaseFacade getFacade(final IJdbcConnectionDescription connectionDescription) {
    return this.facades
        .stream()
        .filter(f -> f.isApplicable(connectionDescription))
        .map(f -> (IDatabaseFacade) f)
        .findFirst()
        .orElseGet(() -> this.databaseFacade);
  }

}
