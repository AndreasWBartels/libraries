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
package net.anwiba.commons.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.utilities.ArrayUtilities;

public final class DummyPreferences implements IPreferences {

  private final Map<String, IPreferences> children = new HashMap<>();
  private final Map<String, Object> preferencesByName = new HashMap<>();
  private final String[] path;
  private final String name;
  private final DummyPreferences parent;

  public DummyPreferences(final String... path) {
    this(null, path);
  }

  public DummyPreferences(final DummyPreferences parent, final String... path) {
    this.parent = parent;
    this.path = path;
    this.name = path.length == 0 ? null : path[path.length - 1];
  }

  @Override
  public void setInt(final String key, final int value) {
    this.preferencesByName.put(key, Integer.valueOf(value));
  }

  @Override
  public void setDouble(final String key, final double value) {
    this.preferencesByName.put(key, Double.valueOf(value));
  }

  @Override
  public boolean getBoolean(final String key, final boolean defaultValue) {
    if (this.preferencesByName.containsKey(key)) {
      final Object object = this.preferencesByName.get(key);
      return object == null ? defaultValue : Boolean.valueOf(ObjectUtilities.toString(object)).booleanValue();
    }
    return defaultValue;
  }

  @Override
  public void setBoolean(final String key, final boolean value) {
    this.preferencesByName.put(key, Boolean.valueOf(value));
  }

  @Override
  public void put(final String key, final String value) {
    this.preferencesByName.put(key, value);
  }

  @Override
  public boolean nodeExists(final String... nodes) {
    if (nodes.length == 0 || !this.children.containsKey(nodes[0])) {
      return false;
    }
    final IPreferences preferences = this.children.get(nodes[0]);
    if (nodes.length > 1) {
      return preferences.nodeExists(Arrays.copyOfRange(nodes, 1, nodes.length));
    }
    return true;
  }

  @Override
  public IPreferences node(final String... nodes) {
    if (nodes.length == 0) {
      return null;
    }
    if (!this.children.containsKey(nodes[0])) {
      this.children.put(nodes[0], new DummyPreferences(this, ArrayUtilities.concat(String.class, this.path, nodes[0])));
    }
    final IPreferences preferences = this.children.get(nodes[0]);
    if (nodes.length > 1) {
      return preferences.node(Arrays.copyOfRange(nodes, 1, nodes.length));
    }
    return preferences;
  }

  @Override
  public int getInt(final String key, final int defaultValue) {
    if (this.preferencesByName.containsKey(key)) {
      final Object object = this.preferencesByName.get(key);
      if (object instanceof Number) {
        return ((Number) object).intValue();
      }
      return object == null ? defaultValue : Integer.valueOf(ObjectUtilities.toString(object)).intValue();
    }
    return defaultValue;
  }

  @Override
  public double getDouble(final String key, final double defaultValue) {
    if (this.preferencesByName.containsKey(key)) {
      final Object object = this.preferencesByName.get(key);
      if (object instanceof Number) {
        return ((Number) object).doubleValue();
      }
      return object == null ? defaultValue : Double.valueOf(ObjectUtilities.toString(object)).doubleValue();
    }
    return defaultValue;
  }

  @Override
  public String get(final String key, final String defaultValue) {
    if (this.preferencesByName.containsKey(key)) {
      final Object object = this.preferencesByName.get(key);
      return object == null ? defaultValue : ObjectUtilities.toString(object);
    }
    return defaultValue;
  }

  @Override
  public void flush() {
    // nothing to do
  }

  @Override
  public List<IPreferences> nodes() {
    final ArrayList<IPreferences> preferences = new ArrayList<>();
    final ArrayList<String> keys = new ArrayList<>(this.children.keySet());
    Collections.sort(keys);
    for (final String key : keys) {
      preferences.add(this.children.get(key));
    }
    return preferences;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(this.path);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof DummyPreferences) {
      final DummyPreferences other = (DummyPreferences) obj;
      return Arrays.equals(this.path, other.path);
    }
    return super.equals(obj);
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Iterable<String> keys() {
    final ArrayList<String> keys = new ArrayList<>(this.preferencesByName.keySet());
    Collections.sort(keys);
    return keys;
  }

  @Override
  public String[] getPath() {
    return this.path;
  }

  @Override
  public void remove(final String key) {
    if (this.preferencesByName.containsKey(key)) {
      this.preferencesByName.remove(key);
    }
  }

  @Override
  public void delete() {
    if (this.parent == null) {
      return;
    }
    this.parent.children.remove(this.name);
  }
}