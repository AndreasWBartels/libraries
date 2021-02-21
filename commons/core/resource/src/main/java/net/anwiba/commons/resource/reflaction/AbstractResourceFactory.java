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
package net.anwiba.commons.resource.reflaction;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import net.anwiba.commons.lang.functional.IBiFunction;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.reference.IResourceReference;
import net.anwiba.commons.resource.annotation.Encoding;
import net.anwiba.commons.resource.annotation.Location;
import net.anwiba.commons.resource.annotation.Static;

public abstract class AbstractResourceFactory {

  private static ILogger logger = Logging.getLogger(AbstractResourceFactory.class.getName());

  private static final int MOD_EXPECTED = Modifier.PUBLIC | Modifier.STATIC;
  private static final int MOD_MASK = MOD_EXPECTED | Modifier.FINAL;
  private static final String EXTENSION = "txt"; //$NON-NLS-1$
  private static final Object ASSIGNED = new Object();
  private static final FieldValueFactory fieldValueFactory = new FieldValueFactory();

  public static void initialize(final Class<?> clazz) {
    initialize(clazz, (c, u) -> ResourceUtilities.getUrl(c, u));
  }

  public static void initialize(final Class<?> clazz, final IBiFunction<Class, String, URL, IOException> helper) {
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

  static void load(final Class<?> clazz, final IBiFunction<Class, String, URL, IOException> helper) {
    final Field[] fieldArray = clazz.getDeclaredFields();
    final Map<String, Object> fields = buildFieldsMap(fieldArray);
    final boolean isAccessible = (clazz.getModifiers() & Modifier.PUBLIC) != 0;
    final String rootPath = createRootPath(clazz);
    for (final Field field : fieldArray) {
      final String resourceUrl = createResourceUrl(rootPath, field);
      URL resource = null;
      try {
        resource = ResourceUtilities.getUrl(helper, clazz, resourceUrl);
      } catch (IOException exception) {
        logger.log(ILevel.ERROR,
            "Couldn't creat url for binding class: '" + clazz.getName() + "' value: '" + field.getName() //$NON-NLS-1$
                + "' resource url is: " + resourceUrl,
            exception);
        throw new RuntimeException(
            "Couldn't creat url for binding class: '" + clazz.getName() + "' value: '" + field.getName()
                + "' resource url is: " + resourceUrl,
            exception);
      }
      if (resource == null) {
        logger.log(ILevel.ERROR,
            "Couldn't creat url for binding class: '" + clazz.getName() + "' value: '" + field.getName() //$NON-NLS-1$
                + "' resource url is: " + resourceUrl);
        throw new RuntimeException(
            "Couldn't creat url for binding class: '" + clazz.getName() + "' value: '" + field.getName()
                + "' resource url is: " + resourceUrl);
      }
      final Object fieldObject = fields.put(field.getName(), ASSIGNED);
      if (fieldObject == null) {
        final String msg = "unused statement: " + field.getName();//$NON-NLS-1$
        logger.log(ILevel.DEBUG, msg);
        continue;
      }
      if (fieldObject == ASSIGNED) {
        continue;
      }
      setValue(field, isAccessible, resource);
    }
    computeMissingBinding(clazz.getName(), fields, fieldArray, isAccessible);
  }

  private static String createRootPath(final Class<?> clazz) {
    return clazz.getPackage().getName().replace('.', '/');
  }

  private static String createResourceUrl(final String pathRoot, final Field field) {
    final Location annotation = field.getAnnotation(Location.class);
    if (annotation != null) {
      return annotation.value();
    }
    final String name = field.getName();
    final String extention = getExtention(field);
    return MessageFormat.format("{0}.{1}", name, extention); //$NON-NLS-1$
  }

  private static String getExtention(final Field field) {
    try {
      final Class<?> clazz = Class.forName(field.getType().getName());
      if (IResourceProvider.class.isAssignableFrom(clazz)) {
        final Method method = clazz.getDeclaredMethod("getExtention"); //$NON-NLS-1$
        return (String) method.invoke(null);
      }
    } catch (final Throwable exception) {
      return EXTENSION;
    }
    return EXTENSION;
  }

  private static void computeMissingBinding(
      final String bundleName,
      final Map<String, Object> fieldsMap,
      final Field[] fieldArray,
      final boolean isAccessible) {
    final int numFields = fieldArray.length;
    for (int i = 0; i < numFields; i++) {
      final Field field = fieldArray[i];
      if ((field.getModifiers() & MOD_MASK) != MOD_EXPECTED) {
        continue;
      }
      if (fieldsMap.get(field.getName()) == ASSIGNED) {
        continue;
      }
      try {
        final String value = "resource reader missing binding for: " + field.getName() + " in: " + bundleName; //$NON-NLS-1$ //$NON-NLS-2$
        logger.log(ILevel.DEBUG, value);
        if (!isAccessible) {
          field.setAccessible(true);
        }
        setMessage(field, value);
      } catch (final RuntimeException e) {
        logger.log(ILevel.ERROR, "Error setting the binding value for: " + field.getName(), e); // $NON-NLS-1$
        throw e;
      } catch (final Exception e) {
        logger.log(ILevel.ERROR, "Error setting the binding value for: " + field.getName(), e); //$NON-NLS-1$
        throw new RuntimeException(e.getMessage(), e);
      }
    }
  }

  public static void setMessage(final Field field, final String message) {
    try {
      final Class<?> type = field.getType();
      final Class<?> clazz = Class.forName(type.getName());
      if (IResourceProvider.class.isAssignableFrom(clazz)) {
        throw new RuntimeException(message);
      }
      if (IResourceReference.class.equals(clazz)) {
        throw new RuntimeException(message);
      }
      if (type.isArray()) {
        throw new RuntimeException(message);
      }
      field.set(null, message);
    } catch (final ClassNotFoundException exception) {
      throw new RuntimeException(message, exception);
    } catch (final IllegalArgumentException exception) {
      throw new RuntimeException(message, exception);
    } catch (final IllegalAccessException exception) {
      throw new RuntimeException(message, exception);
    }
  }

  private static Map<String, Object> buildFieldsMap(final Field[] fieldArray) {
    final Map<String, Object> fields = new HashMap<>(fieldArray.length * 2);
    for (final Field field : fieldArray) {
      fields.put(field.getName(), field);
    }
    return fields;
  }

  private static synchronized void setValue(
      final Field field,
      final boolean isAccessible,
      final URL resourceUrl) {
    if ((field.getModifiers() & MOD_MASK) != MOD_EXPECTED) {
      return;
    }
    try {
      final Object result =
          fieldValueFactory.create(field.getType(), isStatic(field), resourceUrl, getCharset(field));
      if (!isAccessible) {
        field.setAccessible(true);
      }
      field.set(null, result);
    } catch (final InvocationTargetException e) {
      logger.log(ILevel.ERROR, "Exception setting field value.", e.getCause()); //$NON-NLS-1$
    } catch (final IllegalAccessException e) {
      logger.log(ILevel.ERROR, "Exception setting field value.", e); //$NON-NLS-1$
    } catch (final SecurityException e) {
      logger.log(ILevel.ERROR, "Exception setting field value.", e); //$NON-NLS-1$
    }
  }

  private static boolean isStatic(final Field field) {
    final Static staticAnnotation = field.getAnnotation(Static.class);
    return staticAnnotation != null
        ? staticAnnotation.value()
        : true;
  }

  private static Charset getCharset(final Field field) {
    final Encoding annotation = field.getAnnotation(Encoding.class);
    try {
      return Charset
          .forName(
              annotation != null
                  ? annotation.value()
                  : "UTF-8"); //$NON-NLS-1$
    } catch (final UnsupportedCharsetException exception) {
      logger.log(ILevel.ERROR, exception.getLocalizedMessage(), exception);
      return Charset.forName("UTF-8"); //$NON-NLS-1$
    } catch (final IllegalCharsetNameException exception) {
      logger.log(ILevel.ERROR, exception.getLocalizedMessage(), exception);
      return Charset.forName("UTF-8"); //$NON-NLS-1$
    } catch (final IllegalArgumentException exception) {
      logger.log(ILevel.ERROR, exception.getLocalizedMessage(), exception);
      return Charset.forName("UTF-8"); //$NON-NLS-1$
    }
  }
}