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
package net.anwiba.database.oracle.utilities;

import net.anwiba.commons.jdbc.resource.AbstractStatementString;

public class OracleUtilitiesStatementString extends AbstractStatementString {

  static {
    initializeMessages(OracleUtilitiesStatementString.class);
  }

  public static String CallableCreateSequenceTrigger;
  public static String CallableResetSequenceStatement;
  public static String CallableAdjustSequenceStatement;

  public static String IndexNameStatement;
  public static String GatherTableStatisticCall;

  public static String ExistsTablePreparedStatement;
  public static String ExistsViewPreparedStatement;
  public static String ExistsUserPreparedStatement;
  public static String ExistsTablespacePreparedStatement;

  public static String[] CreateUserStatementStrings;
  public static String[] GrantAnyAccessToUserStatementStrings;
  public static String DropUserPreparedStatement;
  public static String DropTableSpacePreparedStatement;
  public static String NumberOfEntriesPreparedStatement;
  public static String GetSessionCountPreparedStatement;

}
