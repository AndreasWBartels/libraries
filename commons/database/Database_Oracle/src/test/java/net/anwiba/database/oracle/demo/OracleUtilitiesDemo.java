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
package net.anwiba.database.oracle.demo;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Ignore;
import org.junit.runner.RunWith;

import de.jdemo.annotation.Demo;
import de.jdemo.framework.PlainDemoCase;
import de.jdemo.junit.DemoAsTestRunner;
import net.anwiba.commons.jdbc.DatabaseUtilities;
import net.anwiba.commons.jdbc.connection.DefaultDatabaseConnector;
import net.anwiba.database.oracle.utilities.OracleUtilities;

@RunWith(DemoAsTestRunner.class)
public class OracleUtilitiesDemo extends PlainDemoCase {

  @Ignore
  @Demo
  public void demoGatherTableStatistic() throws SQLException {
    Connection connection = null;
    try {
      connection =

          new DefaultDatabaseConnector().connectReadOnly(
              "jdbc:oracle:thin:@" + DemoOracleResource.HOST + ":1521:" + DemoOracleResource.INSTANCE, //$NON-NLS-1$ //$NON-NLS-2$
              DemoOracleResource.USER,
              DemoOracleResource.PASSWORD,
              1000);
      OracleUtilities.gatherTableStatistic(connection, "ANWIBA", "BUNDESLAENDER"); //$NON-NLS-1$ //$NON-NLS-2$
    } finally {
      DatabaseUtilities.close(connection);
    }
  }
}
