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

import java.util.List;

public interface IPreferences {

  IPreferences node(String... nodes);

  boolean nodeExists(String... nodes);

  void put(String key, String value);

  void remove(String key);

  String get(String key, String defaultValue);

  double getDouble(String key, double defaultValue);

  void setDouble(String key, double value);

  int getInt(String key, int defaultValue);

  void setInt(String key, int value);

  long getLong(String key, long defaultValue);

  void setLong(String key, long value);

  void flush();

  List<IPreferences> nodes();

  String getName();

  Iterable<String> keys();

  boolean getBoolean(String key, boolean defaultValue);

  void setBoolean(String key, boolean value);

  String[] getPath();

  void delete();

}
