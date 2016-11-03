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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyValueRegistry<K, V> implements IKeyValueRegistry<K, V> {
  private final List<V> list = new ArrayList<>();
  private final Map<K, V> map = new HashMap<>();

  @Override
  public synchronized void register(final K key, final V value) {
    this.map.put(key, value);
    this.list.add(value);
  }

  @Override
  public synchronized V get(final K key) {
    return this.map.get(key);
  }

  @Override
  public synchronized boolean contains(final K key) {
    return this.map.containsKey(key);
  }

  @Override
  public synchronized V[] getItems(final Comparator<K> comparator, final Class<K> keyClazz, final Class<V> valueClazz) {
    final K[] keys = this.map.keySet().toArray((K[]) Array.newInstance(keyClazz, this.list.size()));
    final V[] values = (V[]) Array.newInstance(valueClazz, this.list.size());
    Arrays.sort(keys, comparator);
    for (int i = 0; i < keys.length; i++) {
      values[i] = this.map.get(keys[i]);
    }
    return values;
  }

  @Override
  public synchronized void remove(final K key) {
    final V value = this.map.remove(key);
    if (value == null) {
      return;
    }
    this.list.remove(value);
  }

  @Override
  public synchronized boolean isEmpty() {
    return this.map.isEmpty();
  }
}