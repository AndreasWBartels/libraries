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
package net.anwiba.commons.utilities.registry;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HierarchicalClassKeyRegistry<O> {

  List<Class<?>> classes = new ArrayList<>();
  Map<Class<?>, O> objects = new HashMap<>();

  public void add(final Class<?> key, final O object) {
    if (this.classes.contains(key)) {
      throw new IllegalArgumentException(MessageFormat.format("ambiguity conflict, class {0} is always registered", //$NON-NLS-1$
          key.getName()));
    }
    this.classes.add(key);
    Collections.sort(this.classes, new Comparator<Class<?>>() {

      @Override
      public int compare(final Class<?> clazz, final Class<?> other) {
        if (other.isAssignableFrom(clazz)) {
          return -1;
        }
        if (clazz.isAssignableFrom(other)) {
          return 1;
        }
        return 0;
      }
    });
    this.objects.put(key, object);
  }

  public O get(final Class<?> clazz) {
    final Entry<Class<?>, O> entry = getEntry(clazz);
    if (entry == null) {
      return null;
    }
    return entry.getValue();
  }

  public Entry<Class<?>, O> getEntry(final Class<?> clazz) {
    for (final Class<?> key : this.classes) {
      if (key.equals(clazz) || key.isAssignableFrom(clazz)) {
        final O object = this.objects.get(key);
        return new Entry<Class<?>, O>() {

          @Override
          public Class<?> getKey() {
            return key;
          }

          @Override
          public O getValue() {
            return object;
          }

          @Override
          public O setValue(final O value) {
            throw new UnsupportedOperationException();
          }
        };
      }
    }
    return null;
  }
}
