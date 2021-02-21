/*
 * #%L
 * anwiba database
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
package net.anwiba.database.swing.console.converter;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

import net.anwiba.commons.lang.object.IObjectToStringConverter;
import net.anwiba.commons.reference.utilities.IoUtilities;

public class ClobToStringConverter implements IObjectToStringConverter<Object> {

  @Override
  public String toString(final Object object) {
    if (object instanceof Clob) {
      Clob clob = (Clob) object;
      try (Reader reader = clob.getCharacterStream()) {
        return IoUtilities.toString(reader);
      } catch (IOException | SQLException exception) {
        return exception.getMessage();
      }
    }
    return null;
  }

}
