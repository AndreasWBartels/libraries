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
package net.anwiba.database.oracle.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.anwiba.database.oracle.OracleType;
import oracle.jdbc.OracleTypes;

public class OracleTypeTest {

  @Test
  public void testTypes() throws Exception {
    assertEquals(OracleType.BFILE, OracleType.getType(OracleTypes.BFILE));
    assertEquals(OracleType.BINARY_DOUBLE, OracleType.getType(OracleTypes.BINARY_DOUBLE));
    assertEquals(OracleType.BINARY_FLOAT, OracleType.getType(OracleTypes.BINARY_FLOAT));
    assertEquals(OracleType.CURSOR, OracleType.getType(OracleTypes.CURSOR));
    assertEquals(OracleType.FIXED_CHAR, OracleType.getType(OracleTypes.FIXED_CHAR));
    assertEquals(OracleType.INTERVALDS, OracleType.getType(OracleTypes.INTERVALDS));
    assertEquals(OracleType.INTERVALYM, OracleType.getType(OracleTypes.INTERVALYM));
    assertEquals(OracleType.JAVA_STRUCT, OracleType.getType(OracleTypes.JAVA_STRUCT));
    assertEquals(OracleType.LONGVARCHAR, OracleType.getType(OracleTypes.LONGVARCHAR));
    assertEquals(OracleType.OPAQUE, OracleType.getType(OracleTypes.OPAQUE));
    assertEquals(OracleType.PLSQL_INDEX_TABLE, OracleType.getType(OracleTypes.PLSQL_INDEX_TABLE));
    assertEquals(OracleType.TIMESTAMPLTZ, OracleType.getType(OracleTypes.TIMESTAMPLTZ));
    assertEquals(OracleType.TIMESTAMPTZ, OracleType.getType(OracleTypes.TIMESTAMPTZ));
  }
}
