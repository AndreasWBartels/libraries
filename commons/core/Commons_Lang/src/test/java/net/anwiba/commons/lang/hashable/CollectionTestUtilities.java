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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CollectionTestUtilities {

  public static <K, V, E extends Entry<K, V>, M extends Map<K, V>> void assertMap(
      final M map,
      final M otherMap,
      final E entry,
      final E otherEntry,
      final IMapClonner<K, V, M> clonner) {
    otherMap.put(otherEntry.getKey(), otherEntry.getValue());

    assertThat(map.isEmpty(), equalTo(true));
    assertTrue(map.equals(clonner.clone(map)));
    assertFalse(map.equals(otherMap));

    assertThat(map.put(entry.getKey(), entry.getValue()), nullValue());
    assertThat(map.isEmpty(), equalTo(false));
    assertThat(map.size(), equalTo(1));
    assertTrue(map.containsKey(entry.getKey()));
    assertFalse(map.containsKey(otherEntry.getKey()));
    assertTrue(map.containsValue(entry.getValue()));
    assertFalse(map.containsValue(otherEntry.getValue()));
    assertTrue(map.equals(clonner.clone(map)));
    assertFalse(map.equals(otherMap));

    assertThat(map.put(entry.getKey(), entry.getValue()), equalTo(entry.getValue()));
    assertThat(map.isEmpty(), equalTo(false));
    assertThat(map.size(), equalTo(1));
    assertTrue(map.containsKey(entry.getKey()));
    assertFalse(map.containsKey(otherEntry.getKey()));
    assertTrue(map.containsValue(entry.getValue()));
    assertFalse(map.containsValue(otherEntry.getValue()));
    assertTrue(map.equals(clonner.clone(map)));
    assertFalse(map.equals(otherMap));

    Iterator<Entry<K, V>> iterator = map.entrySet().iterator();
    assertTrue(iterator.hasNext());
    assertThat(iterator.next(), equalTo((Entry<K, V>) entry));
    assertFalse(iterator.hasNext());

    assertThat(map.put(otherEntry.getKey(), otherEntry.getValue()), nullValue());
    assertThat(map.isEmpty(), equalTo(false));
    assertThat(map.size(), equalTo(2));
    assertTrue(map.containsKey(entry.getKey()));
    assertTrue(map.containsKey(otherEntry.getKey()));
    assertTrue(map.containsValue(entry.getValue()));
    assertTrue(map.containsValue(otherEntry.getValue()));
    assertTrue(map.equals(clonner.clone(map)));
    assertFalse(map.equals(otherMap));

    iterator = map.entrySet().iterator();
    assertTrue(iterator.hasNext());
    assertThat(iterator.next(), anyOf(equalTo((Entry<K, V>) entry), equalTo((Entry<K, V>) otherEntry)));
    assertTrue(iterator.hasNext());
    assertThat(iterator.next(), anyOf(equalTo((Entry<K, V>) entry), equalTo((Entry<K, V>) otherEntry)));
    assertFalse(iterator.hasNext());

    assertThat(map.remove(otherEntry.getKey()), equalTo(otherEntry.getValue()));
    assertThat(map.isEmpty(), equalTo(false));
    assertThat(map.size(), equalTo(1));
    assertTrue(map.containsKey(entry.getKey()));
    assertFalse(map.containsKey(otherEntry.getKey()));
    assertTrue(map.containsValue(entry.getValue()));
    assertFalse(map.containsValue(otherEntry.getValue()));
    assertTrue(map.equals(clonner.clone(map)));
    assertFalse(map.equals(otherMap));

    map.clear();
    assertThat(map.isEmpty(), equalTo(true));
    assertThat(map.size(), equalTo(0));
    assertFalse(map.containsKey(entry.getKey()));
    assertFalse(map.containsKey(otherEntry.getKey()));

    assertThat(map.put(null, entry.getValue()), nullValue());
    assertThat(map.isEmpty(), equalTo(false));
    assertThat(map.size(), equalTo(1));
    assertTrue(map.containsKey(null));
    assertThat(map.remove(null), equalTo(entry.getValue()));
  }

  public static <T, S extends Set<T>> void assertSet(
      final S set,
      final S otherSet,
      final T object,
      final T otherObject,
      final ISetClonner<T, S> clonner) {
    otherSet.add(otherObject);

    assertThat(set.isEmpty(), equalTo(true));
    assertTrue(set.equals(clonner.clone(set)));
    assertFalse(set.equals(otherSet));

    assertTrue(set.add(object));
    assertThat(set.isEmpty(), equalTo(false));
    assertThat(set.size(), equalTo(1));
    assertTrue(set.contains(object));
    assertFalse(set.contains(otherObject));
    assertTrue(set.equals(clonner.clone(set)));
    assertFalse(set.equals(otherSet));

    assertFalse(set.add(object));
    assertThat(set.isEmpty(), equalTo(false));
    assertThat(set.size(), equalTo(1));
    assertTrue(set.contains(object));
    assertFalse(set.contains(otherObject));

    Iterator<T> iterator = set.iterator();
    assertTrue(iterator.hasNext());
    assertThat(iterator.next(), equalTo(object));
    assertFalse(iterator.hasNext());

    assertTrue(set.add(otherObject));
    assertThat(set.isEmpty(), equalTo(false));
    assertThat(set.size(), equalTo(2));
    assertTrue(set.contains(object));
    assertTrue(set.contains(otherObject));
    assertTrue(set.containsAll(Arrays.asList(object, otherObject)));
    assertTrue(set.equals(clonner.clone(set)));
    assertFalse(set.equals(otherSet));

    iterator = set.iterator();
    assertTrue(iterator.hasNext());
    assertThat(iterator.next(), anyOf(equalTo(object), equalTo(otherObject)));
    assertTrue(iterator.hasNext());
    assertThat(iterator.next(), anyOf(equalTo(object), equalTo(otherObject)));
    assertFalse(iterator.hasNext());

    assertTrue(set.remove(otherObject));
    assertThat(set.isEmpty(), equalTo(false));
    assertThat(set.size(), equalTo(1));
    assertTrue(set.contains(object));
    assertFalse(set.contains(otherObject));

    set.clear();
    assertThat(set.isEmpty(), equalTo(true));
    assertThat(set.size(), equalTo(0));
    assertFalse(set.contains(object));
    assertFalse(set.contains(otherObject));

    assertTrue(set.add(null));
    assertThat(set.isEmpty(), equalTo(false));
    assertThat(set.size(), equalTo(1));
    assertTrue(set.contains(null));
    assertTrue(set.remove(null));
  }

}
