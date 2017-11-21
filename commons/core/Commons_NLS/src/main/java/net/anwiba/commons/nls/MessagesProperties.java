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

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.Properties;

class MessagesProperties extends Properties {

  private static ILogger logger = Logging.getLogger(MessagesProperties.class.getName());

  private static final int MOD_EXPECTED = Modifier.PUBLIC | Modifier.STATIC;
  private static final int MOD_MASK = MOD_EXPECTED | Modifier.FINAL;
  private static final long serialVersionUID = 1L;

  private final String bundleName;
  private final Map<Object, Object> fields;
  private final boolean isAccessible;
  private final Object assignedFlag;

  public static final class PrivilegedFieldAccessibleSetterAction implements PrivilegedAction<Void> {
    private final Field field;

    public PrivilegedFieldAccessibleSetterAction(final Field field) {
      this.field = field;
    }

    @Override
    public Void run() {
      this.field.setAccessible(true);
      return null;
    }
  }

  MessagesProperties(
    final Map<Object, Object> fieldMap,
    final String bundleName,
    final boolean isAccessible,
    final Object assignedFlag) {
    super();
    this.fields = fieldMap;
    this.bundleName = bundleName;
    this.isAccessible = isAccessible;
    this.assignedFlag = assignedFlag;
  }

  @Override
  public synchronized Object put(final Object key, final Object value) {
    final Object fieldObject = this.fields.put(key, this.assignedFlag);
    if (fieldObject == this.assignedFlag) {
      return null;
    }
    if (fieldObject == null) {
      final String msg = "NLS unused message: " + key + " in: " + this.bundleName;//$NON-NLS-1$ //$NON-NLS-2$
      logger.log(ILevel.WARNING, msg);
      return null;
    }
    final Field field = (Field) fieldObject;
    if ((field.getModifiers() & MOD_MASK) != MOD_EXPECTED) {
      return null;
    }
    try {
      if (!this.isAccessible) {
        AccessController.doPrivileged(new PrivilegedFieldAccessibleSetterAction(field));
      }
      field.set(null, value);
    } catch (final Exception e) {
      logger.log(ILevel.FATAL, "Exception setting field value.", e); //$NON-NLS-1$
    }
    return null;
  }

  @Override
  public synchronized boolean equals(final Object o) {
    if (o instanceof MessagesProperties) {
      final MessagesProperties other = (MessagesProperties) o;
      return this.bundleName == null ? other.bundleName == null : this.bundleName == other.bundleName
          || this.bundleName.equals(other.bundleName) && super.equals(o);
    }
    return false;
  }

  @Override
  public synchronized int hashCode() {
    return super.hashCode();
  }
}