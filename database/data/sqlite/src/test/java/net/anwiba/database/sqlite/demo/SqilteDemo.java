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
package net.anwiba.database.sqlite.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

public class SqilteDemo {

  @Test
//  @Disabled

  public void demo() throws Throwable {
    Class.forName(org.sqlite.JDBC.class.getName());
    try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/test/resources/sample.db")) { //$NON-NLS-1$
      try (final Statement statement = connection.createStatement()) {
        statement.setQueryTimeout(30); // set timeout to 30 sec.
        statement.executeUpdate("drop table if exists person"); //$NON-NLS-1$
        statement.executeUpdate("create table person (id integer, name string)"); //$NON-NLS-1$
        statement.executeUpdate("insert into person values(1, 'leo')"); //$NON-NLS-1$
        statement.executeUpdate("insert into person values(2, 'yui')"); //$NON-NLS-1$
        try (final ResultSet rs = statement.executeQuery("select * from person")) { //$NON-NLS-1$
          while (rs.next()) {
            // read the result set
            System.out.println("name = " + rs.getString("name")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("id = " + rs.getInt("id")); //$NON-NLS-1$//$NON-NLS-2$
          }
        }
      }
    }
  }
}
