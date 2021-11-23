/*
 * #%L
 * anwiba commons
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
package net.anwiba.commons.jdbc;

import java.sql.SQLException;
import java.util.Iterator;

import net.anwiba.commons.lang.exception.IAdditionalThrowableIterable;

public class SqlExceptionToAdditionalThrowableIterable implements IAdditionalThrowableIterable {

  @Override
  public boolean isApplicable(Throwable throwable) {
    return throwable instanceof SQLException;
  }

  @Override
  public Iterable<Throwable> iterable(Throwable throwable) {
    return new Iterable<Throwable>() {
      
      SQLException exception = ((SQLException) throwable).getNextException();
      
      @Override
      public Iterator<Throwable> iterator() {
        return new Iterator<Throwable>() {
          
          @Override
          public Throwable next() {
            try {
              return exception;
            } finally {
              exception = null;
            }
          }
          
          @Override
          public boolean hasNext() {
            return exception != null;
          }
        };
      }
    };
  }

}
