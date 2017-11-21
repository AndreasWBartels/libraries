/*
 * #%L
 * anwiba commons core
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
package net.anwiba.commons.nls;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.logging.Level;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;

public abstract class NLS {

  private static ILogger logger = Logging.getLogger(NLS.class.getName());

  private static final String EXTENSION = ".properties"; //$NON-NLS-1$
  private static final Object ASSIGNED = new Object();
  private static final int MOD_EXPECTED = Modifier.PUBLIC | Modifier.STATIC;
  private static final int MOD_MASK = MOD_EXPECTED | Modifier.FINAL;
  private volatile static AtomicReferenceArray<String> nlSuffixes = null;

  public static void initializeMessages(final Class<?> clazz) {
    initializeMessages(clazz.getName(), clazz);
  }

  public static void initializeMessages(final String bundleName, final Class<?> clazz) {
    if (System.getSecurityManager() == null) {
      load(bundleName, clazz);
      return;
    }
    AccessController.doPrivileged(new PrivilegedAction<Object>() {
      @Override
      @SuppressWarnings("synthetic-access")
      public Object run() {
        load(bundleName, clazz);
        return null;
      }
    });
  }

  public static String bind(final String message, final Object... bindings) {
    return internalBind(message, bindings);
  }

  private static String internalBind(final String message, final Object... args) {
    if (message == null) {
      return "No message available."; //$NON-NLS-1$
    }
    if (args.length == 0) {
      return message;
    }
    return MessageFormat.format(message, args);
  }

  private static String[] buildVariants(final String classRoot) {
    if (nlSuffixes == null) {
      String nl = Locale.getDefault().toString();
      final ArrayList<String> result = new ArrayList<>(4);
      int lastSeparator;
      while (true) {
        result.add('_' + nl + EXTENSION);
        lastSeparator = nl.lastIndexOf('_');
        if (lastSeparator == -1) {
          break;
        }
        nl = nl.substring(0, lastSeparator);
      }
      result.add(EXTENSION);
      nlSuffixes = new AtomicReferenceArray<>(result.toArray(new String[result.size()]));
    }
    final String pathRoot = classRoot.replace('.', '/');
    final String[] variants = new String[nlSuffixes.length()];
    for (int i = 0; i < variants.length; i++) {
      variants[i] = pathRoot + nlSuffixes.get(i);
    }
    return variants;
  }

  private static void computeMissingMessages(
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
        final String value = "NLS missing message: " + field.getName() + " in: " + bundleName; //$NON-NLS-1$ //$NON-NLS-2$
        logger.log(ILevel.WARNING, value);
        if (!isAccessible) {
          field.setAccessible(true);
        }
        field.set(null, value);
      } catch (final SecurityException exception) {
        logger.log(ILevel.FATAL, "Error setting the missing message value for: " + field.getName(), exception); //$NON-NLS-1$
      } catch (final IllegalArgumentException exception) {
        logger.log(ILevel.FATAL, "Error setting the missing message value for: " + field.getName(), exception); //$NON-NLS-1$
      } catch (final IllegalAccessException exception) {
        logger.log(ILevel.FATAL, "Error setting the missing message value for: " + field.getName(), exception); //$NON-NLS-1$
      }
    }
  }

  private static void load(final String bundleName, final Class<?> clazz) {
    final Field[] fieldArray = clazz.getDeclaredFields();
    final ClassLoader loader = clazz.getClassLoader();
    final boolean isAccessible = (clazz.getModifiers() & Modifier.PUBLIC) != 0;
    final int len = fieldArray.length;
    final Map<Object, Object> fields = new HashMap<>(len * 2);
    for (int i = 0; i < len; i++) {
      fields.put(fieldArray[i].getName(), fieldArray[i]);
    }
    final String[] variants = buildVariants(bundleName);
    for (final String element : variants) {
      @SuppressWarnings("resource")
      final InputStream input = loader == null ? ClassLoader.getSystemResourceAsStream(element) : loader
          .getResourceAsStream(element);
      if (input == null) {
        continue;
      }
      try {
        final MessagesProperties properties = new MessagesProperties(fields, bundleName, isAccessible, ASSIGNED);
        properties.load(input);
      } catch (final IOException e) {
        logger.log(Level.SEVERE, "Error loading " + element, e); //$NON-NLS-1$
      } finally {
        try {
          input.close();
        } catch (final IOException e) {
          // ignore
        }
      }
    }
    computeMissingMessages(bundleName, fields, fieldArray, isAccessible);
  }
}