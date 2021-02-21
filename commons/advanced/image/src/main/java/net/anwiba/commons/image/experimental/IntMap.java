/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
/*
 * Copyright (C) 2010 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package net.anwiba.commons.image.experimental;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class IntMap implements Cloneable {
  public static final int NULL = Integer.MIN_VALUE;
  private final Map<Integer, Integer> map;
  private final int[] table;
  private final int tableOffset;
  private int size;

  public IntMap() {
    this(0, 1024);
  }

  public IntMap(final int tableOffset, final int bufferSize) {
    this.map = new TreeMap<Integer, Integer>();
    this.table = new int[bufferSize];
    this.tableOffset = tableOffset;
    this.size = 0;
    Arrays.fill(this.table, NULL);
  }

  public int getSize() {
    return this.size;
  }

  public void putValue(final int key, final int value) {
    if (value == NULL) {
      throw new IllegalArgumentException("value");
    }
    final int index = key - this.tableOffset;
    if (index >= 0 && index < this.table.length) {
      final int oldValue = this.table[index];
      this.table[index] = value;
      if (oldValue == NULL) {
        this.size++;
      }
    } else {
      final Integer oldValue = this.map.put(key, value);
      if (oldValue == null) {
        this.size++;
      }
    }
  }

  public void removeValue(final int key) {
    final int index = key - this.tableOffset;
    if (index >= 0 && index < this.table.length) {
      final int oldValue = this.table[index];
      this.table[index] = NULL;
      if (oldValue != NULL) {
        this.size--;
      }
    } else {
      final Integer oldValue = this.map.remove(key);
      if (oldValue != null) {
        this.size--;
      }
    }
  }

  public int getValue(final int key) {
    final int index = key - this.tableOffset;
    if (index >= 0 && index < this.table.length) {
      return this.table[index];
    } else {
      final Integer oldValue = this.map.get(key);
      if (oldValue != null) {
        return oldValue;
      } else {
        return NULL;
      }
    }
  }

  /**
   * Gets the array of keys in this map, sorted in ascending order.
   * 
   * @return the array of keys in the map
   */
  public int[] getKeys() {
    final int[] keys = new int[getSize()];
    int j = 0;
    for (int index = 0; index < this.table.length; index++) {
      int value = this.table[index];
      if (value != NULL) {
        keys[j++] = index + this.tableOffset;
      }
    }
    final Set<Integer> set = this.map.keySet();
    for (Integer key : set) {
      keys[j++] = key;
    }
    Arrays.sort(keys);
    return keys;
  }

  /**
   * Gets the key/value pairs. The array is sorted in ascending key order.
   * 
   * @return the key/value pairs with {@code pairs[i] = {key, value}}
   */
  public int[][] getPairs() {
    final int[] keys = getKeys();
    final int[][] pairs = new int[keys.length][2];
    for (int i = 0; i < keys.length; i++) {
      pairs[i][0] = keys[i];
      pairs[i][1] = getValue(keys[i]);
    }
    return pairs;
  }

  /**
   * Gets the key/value ranges.
   * 
   * @return {@code ranges = {{keyMin, keyMax}, {valueMin, valueMax}}}
   */
  public int[][] getRanges() {
    int[] keys = getKeys();
    int keyMin = Integer.MAX_VALUE;
    int keyMax = Integer.MIN_VALUE;
    int valueMin = Integer.MAX_VALUE;
    int valueMax = Integer.MIN_VALUE;
    for (int key : keys) {
      keyMin = Math.min(keyMin, key);
      keyMax = Math.max(keyMax, key);
      final int value = getValue(key);
      valueMin = Math.min(valueMin, value);
      valueMax = Math.max(valueMax, value);
    }
    return new int[][] { { keyMin, keyMax }, { valueMin, valueMax } };
  }

  @Override
  public Object clone() {
    final IntMap clone = new IntMap(this.tableOffset, this.table.length);
    final int[][] pairs = getPairs();
    for (int[] pair : pairs) {
      clone.putValue(pair[0], pair[1]);
    }
    return clone;
  }
}
