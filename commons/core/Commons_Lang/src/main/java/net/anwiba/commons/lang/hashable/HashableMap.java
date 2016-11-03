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
package net.anwiba.commons.lang.hashable;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class HashableMap<K extends IHashable, V> implements Map<K, V>, Serializable, Cloneable {

  private static final long serialVersionUID = -3641759535670670188L;

  final HashMap<HashableWrapper<K>, V> map;

  public HashableMap() {
    this.map = new HashMap<>();
  }

  public HashableMap(final int initialCapacity) {
    this.map = new HashMap<>(initialCapacity);
  }

  public HashableMap(final int initialCapacity, final float loadFactor) {
    this.map = new HashMap<>(initialCapacity, loadFactor);
  }

  HashableMap(final HashMap<HashableWrapper<K>, V> map) {
    this.map = map;
  }

  @Override
  public int size() {
    return this.map.size();
  }

  @Override
  public boolean isEmpty() {
    return this.map.isEmpty();
  }

  @Override
  public V put(final K key, final V value) {
    return this.map.put(new HashableWrapper<>(key), value);
  }

  @Override
  public boolean containsValue(final Object value) {
    return this.map.containsValue(value);
  }

  @Override
  public boolean containsKey(final Object key) {
    if (key != null && !(key instanceof IHashable)) {
      return false;
    }
    final IHashable hashable = (IHashable) key;
    return this.map.containsKey(new HashableWrapper<>(hashable));
  }

  @Override
  public V get(final Object key) {
    if (key != null && !(key instanceof IHashable)) {
      return null;
    }
    final IHashable hashable = (IHashable) key;
    return this.map.get(new HashableWrapper<>(hashable));
  }

  @Override
  public V remove(final Object key) {
    if (key != null && !(key instanceof IHashable)) {
      return null;
    }
    final IHashable hashable = (IHashable) key;
    return this.map.remove(new HashableWrapper<>(hashable));
  }

  @Override
  public void putAll(@SuppressWarnings("hiding") final Map<? extends K, ? extends V> map) {
    final int numKeysToBeAdded = map.size();
    if (numKeysToBeAdded == 0) {
      return;
    }
    final HashMap<HashableWrapper<K>, V> tmp = new HashMap<>(map.size());
    for (final Iterator<? extends Map.Entry<? extends K, ? extends V>> i = map.entrySet().iterator(); i.hasNext();) {
      final Map.Entry<? extends K, ? extends V> e = i.next();
      tmp.put(new HashableWrapper<K>(e.getKey()), e.getValue());
    }
    this.map.putAll(tmp);
  }

  @Override
  public void clear() {
    this.map.clear();
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Set<K> keySet() {
    final Set<HashableWrapper<K>> keySet = this.map.keySet();
    if (keySet instanceof HashSet) {
      final HashableSet<K> hashableSet = new HashableSet<>();
      hashableSet.addAll((HashSet) this.map.keySet());
      return hashableSet;
    }
    return new HashableSet<>(new HashSet<>(this.map.keySet()));
  }

  @Override
  public Collection<V> values() {
    return this.map.values();
  }

  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet() {
    final Set<java.util.Map.Entry<HashableWrapper<K>, V>> wrappedEntrySet = this.map.entrySet();
    if (wrappedEntrySet instanceof HashSet) {
      return new HashableEntrySet<>((HashSet<Entry<HashableWrapper<K>, V>>) wrappedEntrySet);
    }
    return new HashableEntrySet<>(new HashSet<>(wrappedEntrySet));
  }

  @Override
  public int hashCode() {
    return this.map.hashCode();
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(final Object object) {
    if (object == this) {
      return true;
    }
    if (!(object instanceof Map)) {
      return false;
    }
    final Map<K, V> other = (Map<K, V>) object;
    if (other.size() != size()) {
      return false;
    }
    try {
      for (final Entry<K, V> entry : entrySet()) {
        final K key = entry.getKey();
        final V value = entry.getValue();
        if (value == null) {
          if (!(other.get(key) == null && other.containsKey(key))) {
            return false;
          }
          continue;
        }
        if (!value.equals(other.get(key))) {
          return false;
        }
      }
    } catch (final ClassCastException unused) {
      return false;
    } catch (final NullPointerException unused) {
      return false;
    }
    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  public HashableMap<K, V> clone() {
    return new HashableMap<>((HashMap<HashableWrapper<K>, V>) this.map.clone());
  }
}