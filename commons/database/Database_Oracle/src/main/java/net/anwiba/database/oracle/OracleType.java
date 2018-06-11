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
package net.anwiba.database.oracle;

import net.anwiba.commons.jdbc.metadata.IDataBaseType;
import oracle.jdbc.OracleTypes;

public enum OracleType implements IDataBaseType {

  NUMBER(OracleTypes.NUMBER) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitNumber();
    }
  },
  FLOAT(OracleTypes.FLOAT) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitFloat();
    }
  },
  VARCHAR2(OracleTypes.VARCHAR) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitVarchar();
    }
  },
  NVARCHAR2(OracleTypes.NVARCHAR) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitVarchar();
    }
  },
  INTERVALDS(OracleTypes.INTERVALDS) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitUnsupportedType();
    }
  },
  INTERVALYM(OracleTypes.INTERVALYM) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitUnsupportedType();
    }
  },
  TIMESTAMPLTZ(OracleTypes.TIMESTAMPLTZ) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitUnsupportedType();
    }
  },
  TIMESTAMPTZ(OracleTypes.TIMESTAMPTZ) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitUnsupportedType();
    }
  },
  PLSQL_INDEX_TABLE(OracleTypes.PLSQL_INDEX_TABLE) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitUnsupportedType();
    }
  },
  BFILE(OracleTypes.BFILE) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitUnsupportedType();
    }
  },
  CURSOR(OracleTypes.CURSOR) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitUnsupportedType();
    }
  },
  RAW(OracleTypes.RAW) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitUnsupportedType();
    }
  },
  LONGVARCHAR(OracleTypes.LONGVARCHAR) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitUnsupportedType();
    }
  },
  BINARY_FLOAT(OracleTypes.BINARY_FLOAT) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitUnsupportedType();
    }
  },
  BINARY_DOUBLE(OracleTypes.BINARY_DOUBLE) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitUnsupportedType();
    }
  },
  FIXED_CHAR(OracleTypes.FIXED_CHAR) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitUnsupportedType();
    }
  },
  OPAQUE(OracleTypes.OPAQUE) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitUnsupportedType();
    }
  },
  JAVA_STRUCT(OracleTypes.JAVA_STRUCT) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitUnsupportedType();
    }
  },
  UNKNOWN(Integer.MAX_VALUE) {
    @Override
    public <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E {
      visitor.visitUnknown();
    }
  };

  private final int code;

  private OracleType(final int code) {
    this.code = code;
  }

  public static OracleType getType(final int code) {
    for (final OracleType oracleType : OracleType.values()) {
      if (oracleType.code == code) {
        return oracleType;
      }
    }
    return OracleType.UNKNOWN;
  }

  @Override
  public int getCode() {
    return this.code;
  }

  public abstract <T, E extends Exception> void accept(final IOracleTypeVisitor<T, E> visitor) throws E;

  public static OracleType getByName(final String name) {
    for (final OracleType type : OracleType.values()) {
      if (type.name().equals(name.trim().toUpperCase())) {
        return type;
      }
    }
    return OracleType.UNKNOWN;
  }
}
