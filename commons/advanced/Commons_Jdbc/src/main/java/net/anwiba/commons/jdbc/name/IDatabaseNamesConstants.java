/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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
package net.anwiba.commons.jdbc.name;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("nls")
public interface IDatabaseNamesConstants {

  Set<String> RESERVED_NAMES = new HashSet<>(
      Arrays.asList(
          "abort",
          "action",
          "add",
          "after",
          "all",
          "alter",
          "analyze",
          "and",
          "as",
          "asc",
          "attach",
          "autoincrement",
          "before",
          "begin",
          "between",
          "by",
          "cascade",
          "case",
          "cast",
          "check",
          "collate",
          "column",
          "commit",
          "conflict",
          "constraint",
          "create",
          "cross",
          "current_date",
          "current_time",
          "current_timestamp",
          "database",
          "default",
          "deferrable",
          "deferred",
          "delete",
          "desc",
          "detach",
          "distinct",
          "double",
          "drop",
          "each",
          "else",
          "end",
          "escape",
          "except",
          "exclusive",
          "exists",
          "explain",
          "fail",
          "float",
          "for",
          "foreign",
          "from",
          "full",
          "glob",
          "group",
          "having",
          "if",
          "ignore",
          "immediate",
          "in",
          "index",
          "indexed",
          "initially",
          "inner",
          "insert",
          "instead",
          "intersect",
          "int",
          "into",
          "is",
          "isnull",
          "join",
          "key",
          "left",
          "level",
          "like",
          "limit",
          "match",
          "natural",
          "no",
          "not",
          "notnull",
          "null",
          "of",
          "offset",
          "on",
          "or",
          "order",
          "outer",
          "plan",
          "pragma",
          "primary",
          "query",
          "raise",
          "recursive",
          "references",
          "regexp",
          "reindex",
          "release",
          "rename",
          "replace",
          "restrict",
          "right",
          "rollback",
          "row",
          "savepoint",
          "select",
          "set",
          "table",
          "temp",
          "temporary",
          "then",
          "to",
          "transaction",
          "trigger",
          "union",
          "unique",
          "update",
          "using",
          "vacuum",
          "values",
          "view",
          "virtual",
          "when",
          "where",
          "with",
          "without",
          "int",
          "integer",
          "tinyint",
          "smallint",
          "mediumint",
          "bigint",
          "unsigned_big",
          "int2",
          "int8",
          "integer",
          "character",
          "varchar",
          "varying_character",
          "nchar",
          "native_character",
          "nvarchar",
          "text",
          "clob",
          "blob",
          "real",
          "double",
          "double_precision",
          "float",
          "numeric",
          "decimal",
          "boolean",
          "date",
          "datetime"));

}
