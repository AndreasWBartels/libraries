/*
 * #%L
 * anwiba commons tools
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
package net.anwiba.tools.generator.java.bean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("nls")
public class JavaConstants {

  public static final String PROPERTIES = "properties"; //$NON-NLS-1$
  public static final String NAMED_VALUE_PROVIDER = net.anwiba.commons.utilities.provider.INamedValueProvider.class
      .getName();

  public static final String JAVA_UTIL_LIST = java.util.List.class.getName();
  public static final String JAVA_UTIL_ARRAYLIST = java.util.ArrayList.class.getName();
  public static final String JAVA_UTIL_MAP = java.util.Map.class.getName();
  public static final String JAVA_UTIL_HASHMAP = java.util.HashMap.class.getName();
  public static final String JAVA_UTIL_LINKHASHMAP = java.util.LinkedHashMap.class.getName();
  public static final String JAVA_LANG_OVERRIDE = java.lang.Override.class.getName();
  public static final String JAVA_LANG_ITERABLE = java.lang.Iterable.class.getName();
  public static final String JAVA_LANG_RUNTIME_EXCEPTION = java.lang.RuntimeException.class.getName();
  public static final String JAVA_LANG_CLASS = java.lang.Class.class.getName();
  public static final String JAVA_LANG_STRING = java.lang.String.class.getName();
  public static final String JAVA_LANG_OBJECT = java.lang.Object.class.getName();
  public static final String JAVA_LANG_FLOAT = java.lang.Float.class.getName();
  public static final String FLOAT = "float";
  public static final String JAVA_LANG_DOUBLE = java.lang.Double.class.getName();
  public static final String DOUBLE = "double";
  public static final String JAVA_LANG_INTEGER = java.lang.Integer.class.getName();
  public static final String INT = "int";
  public static final String JAVA_LANG_SHORT = java.lang.Short.class.getName();
  public static final String SHORT = "short";
  public static final String JAVA_LANG_LONG = java.lang.Long.class.getName();
  public static final String LONG = "long";
  public static final String JAVA_LANG_CHARACTER = java.lang.Character.class.getName();
  public static final String CHAR = "char";
  public static final String JAVA_LANG_BYTE = java.lang.Byte.class.getName();
  public static final String BYTE = "byte";
  public static final String JAVA_LANG_BOOLEAN = java.lang.Boolean.class.getName();
  public static final String BOOLEAN = "boolean";
  public static final Set<String> shortTypes = new HashSet<>();

  public final static Set<String> primitives = new HashSet<>();
  static {
    primitives.add(SHORT);
    primitives.add(INT);
    primitives.add(LONG);
    primitives.add(FLOAT);
    primitives.add(DOUBLE);
    primitives.add(BYTE);
    primitives.add(BOOLEAN);
    primitives.add(CHAR);
  }
  public final static Map<String, String> primitiveClasses = new HashMap<>();
  static {
    primitiveClasses.put(SHORT, JAVA_LANG_SHORT);
    primitiveClasses.put(INT, JAVA_LANG_INTEGER);
    primitiveClasses.put(LONG, JAVA_LANG_LONG);
    primitiveClasses.put(FLOAT, JAVA_LANG_FLOAT);
    primitiveClasses.put(DOUBLE, JAVA_LANG_DOUBLE);
    primitiveClasses.put(BOOLEAN, JAVA_LANG_BOOLEAN);
    primitiveClasses.put(BYTE, JAVA_LANG_BYTE);
    primitiveClasses.put(CHAR, JAVA_LANG_CHARACTER);
  }

  static {
    shortTypes.add(SHORT);
    shortTypes.add(JAVA_LANG_SHORT);
    shortTypes.add(java.lang.Short.class.getSimpleName());
  }
  public static final Set<String> integerTypes = new HashSet<>();
  static {
    integerTypes.add(INT);
    integerTypes.add(JAVA_LANG_INTEGER);
    integerTypes.add(java.lang.Integer.class.getSimpleName());
  }
  public static final Set<String> longTypes = new HashSet<>();
  static {
    longTypes.add(LONG);
    longTypes.add(JAVA_LANG_LONG);
    longTypes.add(java.lang.Long.class.getSimpleName());
  }
  public static final Set<String> floatTypes = new HashSet<>();
  static {
    floatTypes.add(FLOAT);
    floatTypes.add(JAVA_LANG_FLOAT);
    floatTypes.add(java.lang.Float.class.getSimpleName());
  }
  public static final Set<String> doubleTypes = new HashSet<>();
  static {
    doubleTypes.add(DOUBLE);
    doubleTypes.add(JAVA_LANG_DOUBLE);
    doubleTypes.add(java.lang.Double.class.getSimpleName());
  }
  public static final Set<String> booleanTypes = new HashSet<>();
  static {
    booleanTypes.add(BOOLEAN);
    booleanTypes.add(JAVA_LANG_BOOLEAN);
    booleanTypes.add(java.lang.Boolean.class.getSimpleName());
  }
  public static final Set<String> byteTypes = new HashSet<>();
  static {
    byteTypes.add(BYTE);
    byteTypes.add(JAVA_LANG_BYTE);
    byteTypes.add(java.lang.Byte.class.getSimpleName());
  }
  public static final Set<String> characterTypes = new HashSet<>();
  static {
    characterTypes.add(CHAR);
    characterTypes.add(JAVA_LANG_CHARACTER);
    characterTypes.add(java.lang.Character.class.getSimpleName());
  }
  public static final Set<String> stringTypes = new HashSet<>();
  static {
    stringTypes.add(JAVA_LANG_STRING);
    stringTypes.add(java.lang.String.class.getSimpleName());
  }
  static public Set<String> resevedNames = new HashSet<>();
  static {
    resevedNames.addAll(Arrays.asList(
        "default",
        "package",
        "class",
        "interface",
        "enum",
        "extends",
        "implements",
        "abstract",
        "import",
        "native",
        "final",
        "transient",
        "volatile",
        "static",
        "private",
        "public",
        "protected",
        "synchronized",
        "none",
        "null",
        "this",
        "super",
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        CHAR,
        BYTE,
        BOOLEAN,
        "instanceof",
        "if",
        "else",
        "while",
        "do",
        "try",
        "catch",
        "finally",
        "return"));
  }

}
