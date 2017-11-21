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
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;

class DefaultPreferences implements IPreferences {

  private static ILogger logger = Logging.getLogger(DefaultPreferences.class.getName());

  private final Preferences preferences;

  DefaultPreferences(final Preferences preferences) {
    this.preferences = preferences;
  }

  @Override
  public IPreferences node(final String... nodes) {
    return new DefaultPreferences(this.preferences.node(new PathFactory().create(nodes)));
  }

  @Override
  public boolean nodeExists(final String... nodes) {
    try {
      return this.preferences.nodeExists(new PathFactory().create(nodes));
    } catch (final BackingStoreException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage());
      logger.log(ILevel.ALL, exception.getMessage(), exception);
      return false;
    }
  }

  @Override
  public void put(final String key, final String value) {
    this.preferences.put(key, value);
  }

  @Override
  public String get(final String key, final String defaultValue) {
    return this.preferences.get(key, defaultValue);
  }

  @Override
  public double getDouble(final String key, final double defaultValue) {
    return this.preferences.getDouble(key, defaultValue);
  }

  @Override
  public int getInt(final String key, final int defaultValue) {
    return this.preferences.getInt(key, defaultValue);
  }

  @Override
  public void setInt(final String key, final int value) {
    this.preferences.putInt(key, value);
  }

  @Override
  public void setDouble(final String key, final double value) {
    this.preferences.putDouble(key, value);
  }

  @Override
  public void flush() {
    try {
      this.preferences.flush();
    } catch (final BackingStoreException exception) {
      logger.log(ILevel.WARNING, exception.getMessage());
      logger.log(ILevel.DEBUG, exception.getMessage(), exception);
    }
  }

  @Override
  public List<IPreferences> nodes() {
    final ArrayList<IPreferences> nodes = new ArrayList<>();
    try {
      final String[] names = this.preferences.childrenNames();
      Arrays.sort(names);
      for (final String name : names) {
        nodes.add(new DefaultPreferences(this.preferences.node(name)));
      }
      return nodes;
    } catch (final BackingStoreException | IllegalStateException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage());
      logger.log(ILevel.ALL, exception.getMessage(), exception);
      return new ArrayList<>();
    }
  }

  @Override
  public int hashCode() {
    return this.preferences.absolutePath().hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof DefaultPreferences) {
      final DefaultPreferences other = (DefaultPreferences) obj;
      return ObjectUtilities.equals(this.preferences.absolutePath(), other.preferences.absolutePath())
          && this.preferences.isUserNode() == other.preferences.isUserNode();
    }
    return super.equals(obj);
  }

  @Override
  public String getName() {
    return this.preferences.name();
  }

  @Override
  public Iterable<String> keys() {
    try {
      final ArrayList<String> keys = new ArrayList<>(Arrays.asList(this.preferences.keys()));
      Collections.sort(keys);
      return keys;
    } catch (final BackingStoreException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage());
      logger.log(ILevel.ALL, exception.getMessage(), exception);
      return new ArrayList<>();
    }
  }

  @Override
  public boolean getBoolean(final String key, final boolean defaultValue) {
    return this.preferences.getBoolean(key, defaultValue);
  }

  @Override
  public void setBoolean(final String key, final boolean value) {
    this.preferences.putBoolean(key, value);
  }

  @Override
  public String[] getPath() {
    return PreferenceUtilities.createPath(this.preferences);
  }

  @Override
  public void delete() {
    try {
      this.preferences.removeNode();
      this.preferences.flush();
    } catch (final BackingStoreException exception) {
      logger.log(ILevel.DEBUG, exception.getMessage());
      logger.log(ILevel.ALL, exception.getMessage(), exception);
    }
  }

  @Override
  public void remove(final String key) {
    this.preferences.remove(key);
  }
}