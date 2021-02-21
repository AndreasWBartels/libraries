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
package net.anwiba.commons.jdbc.resource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.BiFunction;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.utilities.string.StringUtilities;

public abstract class AbstractStatementString {

  static final ILogger logger = Logging.getLogger(AbstractStatementString.class.getName());

  private static final String EXTENSION = "sql"; //$NON-NLS-1$
  private static final int MOD_EXPECTED = Modifier.PUBLIC | Modifier.STATIC;
  private static final int MOD_MASK = MOD_EXPECTED | Modifier.FINAL;
  private static final Object ASSIGNED = new Object();

  public static void initialize(final Class<?> clazz,
      final BiFunction<Class, String, InputStream> helper) {
    if (System.getSecurityManager() == null) {
      load(clazz, helper);
      return;
    }
    AccessController.doPrivileged(new PrivilegedAction<>() {
      @Override
      public Object run() {
        load(clazz, helper);
        return null;
      }
    });
  }

  public static String bind(final String statement, final Object... bindings) {
    return bind(Locale.getDefault(), statement, bindings);
  }

  public static String bind(final Locale local, final String statement, final Object... bindings) {
    if (statement == null) {
      return "No statement available."; //$NON-NLS-1$
    }
    if (bindings.length == 0) {
      return statement;
    }

    return new MessageFormat(doubleQuots(statement.toCharArray()), local).format(bindings);
  }

  private static String doubleQuots(final char[] characters) {
    final StringBuilder builder = new StringBuilder();
    for (final char character : characters) {
      if (character == '\'') {
        builder.append(character);
      }
      builder.append(character);
    }
    return builder.toString();
  }

  private static Map<String, String> buildVariants(final String classRoot, final Field[] fieldArray) {
    final String pathRoot = classRoot.replace('.', '/');
    final Map<String, String> resources = new HashMap<>();
    for (final Field field : fieldArray) {
      resources.put(field.getName(), pathRoot + "/" + field.getName() + "." + EXTENSION); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return resources;
  }

  private static void computeMissingStatement(
      final String bundleName,
      final Map<Object, Object> fieldMap,
      final Field[] fieldArray,
      final boolean isAccessible) {
    final int numFields = fieldArray.length;
    for (int i = 0; i < numFields; i++) {
      final Field field = fieldArray[i];
      if ((field.getModifiers() & MOD_MASK) != MOD_EXPECTED) {
        continue;
      }
      if (fieldMap.get(field.getName()) == ASSIGNED) {
        continue;
      }
      try {
        final String value = "missing statement: " + field.getName() + " in: " + bundleName; //$NON-NLS-1$ //$NON-NLS-2$
        logger.log(ILevel.WARNING, value);
        if (!isAccessible) {
          field.setAccessible(true);
        }
        field.set(null, value);
      } catch (final Exception e) {
        logger.log(ILevel.FATAL, "Error setting the missing statement value for: " + field.getName(), e); //$NON-NLS-1$
      }
    }
  }

  static void load(final Class<?> clazz, final BiFunction<Class, String, InputStream> helper) {
    final Field[] fieldArray = clazz.getDeclaredFields();
    final ClassLoader loader = clazz.getClassLoader();
    final boolean isAccessible = (clazz.getModifiers() & Modifier.PUBLIC) != 0;
    final int len = fieldArray.length;
    final Map<Object, Object> fields = new HashMap<>(len * 2);
    for (int i = 0; i < len; i++) {
      fields.put(fieldArray[i].getName(), fieldArray[i]);
    }
    final String bundleName = clazz.getPackage().getName();
    final Map<String, String> resources = buildVariants(bundleName, fieldArray);
    for (final Field field : fieldArray) {
      final String element = resources.get(field.getName());
      @SuppressWarnings("resource")
      final InputStream input = ResourceUtilities.getInputStream(helper, clazz, element);
      if (input == null) {
        logger.log(ILevel.SEVERE, "Error loading " + element); //$NON-NLS-1$
        continue;
      }
      try {
        final String value = readInputStream(input);
        setValue(fields, field, value, isAccessible);
      } catch (final IOException e) {
        logger.log(ILevel.SEVERE, "Error loading " + element, e); //$NON-NLS-1$
      } finally {
        try {
          input.close();
        } catch (final IOException e) {
          // ignore
        }
      }
    }
    computeMissingStatement(clazz.getName(), fields, fieldArray, isAccessible);
  }

  private static synchronized void setValue(
      final Map<Object, Object> fields,
      final Field field,
      final String value,
      final boolean isAccessible) {
    final Object fieldObject = fields.put(field.getName(), ASSIGNED);
    if (fieldObject == ASSIGNED) {
      return;
    }
    if (fieldObject == null) {
      final String msg = "unused statement: " + field.getName();//$NON-NLS-1$
      logger.log(ILevel.WARNING, msg);
      return;
    }
    if ((field.getModifiers() & MOD_MASK) != MOD_EXPECTED) {
      return;
    }
    try {
      if (!isAccessible) {
        field.setAccessible(true);
      }
      final String fieldValue = value.replace("\r", ""); //$NON-NLS-1$//$NON-NLS-2$
      if (field.getType().isArray()) {
        final String[] array = createArray(fieldValue);
        field.set(null, array);
        return;
      }
      field.set(null, fieldValue);
    } catch (final Exception e) {
      logger.log(ILevel.SEVERE, "Exception setting field value.", e); //$NON-NLS-1$
    }
    return;
  }

  private static String[] createArray(final String fieldValue) {
    final List<String> values = new ArrayList<>();
    final StringTokenizer tokenizer = new StringTokenizer(fieldValue, ";"); //$NON-NLS-1$
    while (tokenizer.hasMoreElements()) {
      final String nextToken = tokenizer.nextToken().trim();
      if (StringUtilities.isNullOrEmpty(nextToken)) {
        continue;
      }
      values.add(nextToken);
    }
    return values.toArray(new String[values.size()]);
  }

  private static String readInputStream(final InputStream inputStream) throws IOException {
    final StringBuffer string = new StringBuffer();
    final byte[] buffer = new byte[1024];
    int length = 0;
    while ((length = inputStream.read(buffer)) > -1) {
      string.append(new String(buffer, 0, length));
    }
    return string.toString().trim();
  }
}