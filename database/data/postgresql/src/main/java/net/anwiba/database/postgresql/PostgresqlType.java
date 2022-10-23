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
package net.anwiba.database.postgresql;

import java.sql.Types;

import net.anwiba.commons.jdbc.metadata.IDatabaseType;

public enum PostgresqlType implements IDatabaseType {

  OID(Types.INTEGER) {
    @Override
    public <T, E extends Exception> T accept(final IPostgresqlTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  INT2(Types.SMALLINT) {
    @Override
    public <T, E extends Exception> T accept(final IPostgresqlTypeVisitor<T, E> visitor) throws E {
      return visitor.visitShort();
    }
  },
  INT4(Types.INTEGER) {
    @Override
    public <T, E extends Exception> T accept(final IPostgresqlTypeVisitor<T, E> visitor) throws E {
      return visitor.visitInteger();
    }
  },
  INT8(Types.BIGINT) {
    @Override
    public <T, E extends Exception> T accept(final IPostgresqlTypeVisitor<T, E> visitor) throws E {
      return visitor.visitLong();
    }
  },
  FLOAT4(Types.NUMERIC) {
    @Override
    public <T, E extends Exception> T accept(final IPostgresqlTypeVisitor<T, E> visitor) throws E {
      return visitor.visitFloat();
    }
  },
  FLOAT8(Types.DOUBLE) {
    @Override
    public <T, E extends Exception> T accept(final IPostgresqlTypeVisitor<T, E> visitor) throws E {
      return visitor.visitDouble();
    }
  },
  MONEY(Types.DOUBLE) {
    @Override
    public <T, E extends Exception> T accept(final IPostgresqlTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  BPCHAR(Types.CHAR) {
    @Override
    public <T, E extends Exception> T accept(final IPostgresqlTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  TEXT(Types.VARCHAR) {
    @Override
    public <T, E extends Exception> T accept(final IPostgresqlTypeVisitor<T, E> visitor) throws E {
      return visitor.visitVarchar();
    }
  },
  NAME(Types.VARCHAR) {
    @Override
    public <T, E extends Exception> T accept(final IPostgresqlTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  BYTEA(Types.VARCHAR) {
    @Override
    public <T, E extends Exception> T accept(final IPostgresqlTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  BOOL(Types.BIT) {
    @Override
    public <T, E extends Exception> T accept(final IPostgresqlTypeVisitor<T, E> visitor) throws E {
      return visitor.visitBoolean();
    }
  },
  TIMETZ(Types.TIME) {
    @Override
    public <T, E extends Exception> T accept(final IPostgresqlTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  TIMESTAMPTZ(Types.TIMESTAMP) {
    @Override
    public <T, E extends Exception> T accept(final IPostgresqlTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnsupportedType();
    }
  },
  UNKNOWN(Integer.MAX_VALUE) {
    @Override
    public <T, E extends Exception> T accept(final IPostgresqlTypeVisitor<T, E> visitor) throws E {
      return visitor.visitUnknown();
    }
  };

  private final int code;

  private PostgresqlType(final int code) {
    this.code = code;
  }

  @Override
  public String getName() {
    return name();
  }

  public static PostgresqlType getByName(final String name) {
    for (final PostgresqlType type : PostgresqlType.values()) {
      if (type.name().equals(name.trim().toUpperCase())) {
        return type;
      }
    }
    return PostgresqlType.UNKNOWN;
  }

  public static PostgresqlType getByTypeCode(final int code) {
    for (final PostgresqlType type : PostgresqlType.values()) {
      if (type.code == code) {
        return type;
      }
    }
    return PostgresqlType.UNKNOWN;
  }

  @Override
  public int getCode() {
    return this.code;
  }

  public abstract <T, E extends Exception> T accept(final IPostgresqlTypeVisitor<T, E> visitor) throws E;
}