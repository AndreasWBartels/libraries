/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.commons.jdbc.type;

import java.sql.Types;

public enum DatabaseType {

  BIT(Types.BIT) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  TINYINT(Types.TINYINT) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitShort();
    }
  },
  SMALLINT(Types.SMALLINT) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitShort();
    }
  },
  INTEGER(Types.INTEGER) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitInteger();
    }
  },
  BIGINT(Types.BIGINT) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitLong();
    }
  },
  FLOAT(Types.FLOAT) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitFloat();
    }
  },
  REAL(Types.REAL) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitDouble();
    }
  },
  DOUBLE(Types.DOUBLE) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitDouble();
    }
  },
  DOUBLE_PRECISION(Types.DOUBLE) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitDouble();
    }
  },
  NUMERIC(Types.NUMERIC) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitNumeric();
    }
  },
  DECIMAL(Types.DECIMAL) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitDouble();
    }
  },
  CHAR(Types.CHAR) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitChar();
    }
  },
  VARCHAR(Types.VARCHAR) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitVarchar();
    }
  },
  LONGVARCHAR(Types.LONGNVARCHAR) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  DATE(Types.DATE) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitDate();
    }
  },
  TIME(Types.TIME) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitTime();
    }
  },
  TIME_WITH_TIMEZONE(Types.TIME_WITH_TIMEZONE) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitTimeWithTimeZone();
    }
  },
  TIMESTAMP(Types.TIMESTAMP) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitTimeStampWithTimeZone();
    }
  },
  TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitTimeStamp();
    }
  },
  BINARY(Types.BINARY) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  VARBINARY(Types.VARBINARY) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  LONGVARBINARY(Types.LONGVARBINARY) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  NULL(Types.NULL) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  OTHER(Types.OTHER) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitOther();
    }
  },
  JAVA_OBJECT(Types.OTHER) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  DISTINCT(Types.DISTINCT) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  STRUCT(Types.STRUCT) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitStrukt();
    }
  },
  ARRAY(Types.ARRAY) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  BLOB(Types.BLOB) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  CLOB(Types.CLOB) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  REF(Types.REF) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  DATALINK(Types.DATALINK) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  BOOLEAN(Types.BOOLEAN) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitBoolean();
    }
  },
  ROWID(Types.ROWID) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  NCHAR(Types.NCHAR) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  NVARCHAR(Types.NVARCHAR) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  LONGNVARCHAR(Types.LONGNVARCHAR) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  NCLOB(Types.NCLOB) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  SQLXML(Types.SQLXML) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  UNKNOWN(Integer.MAX_VALUE) {
    @Override
    public <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnknown();
    }
  };

  private final int code;

  private DatabaseType(final int code) {
    this.code = code;
  }

  public static DatabaseType getByTypeId(final int code) {
    for (final DatabaseType databaseType : DatabaseType.values()) {
      if (databaseType.code == code) {
        return databaseType;
      }
    }
    return DatabaseType.UNKNOWN;
  }

  public int getCode() {
    return this.code;
  }

  public static DatabaseType getByName(final String name) {
    if (name == null) {
      return null;
    }
    String normedName = name.trim().replaceAll(" ", "_").toUpperCase();
    for (final DatabaseType databaseType : DatabaseType.values()) {
      if (databaseType.name().equals(normedName)) {
        return databaseType;
      }
    }
    return DatabaseType.UNKNOWN;
  }

  public abstract <T, E extends Exception> T accept(final IDatabaseTypeVisitor<T, E> visitor) throws E;
}
