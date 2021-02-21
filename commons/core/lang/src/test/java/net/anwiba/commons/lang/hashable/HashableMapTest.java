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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

public class HashableMapTest {

  @Test
  public void add() {
    final MapEntry<HashableIdentifier, Object> entry = new MapEntry<>(new HashableIdentifier(), new Object());
    final MapEntry<HashableIdentifier, Object> otherEntry = new MapEntry<>(new HashableIdentifier(), new Object());
    final HashableMap<HashableIdentifier, Object> map = new HashableMap<>();
    final HashableMap<HashableIdentifier, Object> otherMap = new HashableMap<>();
    CollectionTestUtilities.assertMap(
        map,
        otherMap,
        entry,
        otherEntry,
        new IMapClonner<HashableIdentifier, Object, HashableMap<HashableIdentifier, Object>>() {
          @Override
          public HashableMap<HashableIdentifier, Object> clone(
              @SuppressWarnings("hiding") final HashableMap<HashableIdentifier, Object> map) {
            return map.clone();
          }
        });
  }

  @SuppressWarnings("unlikely-arg-type")
  @Test
  public void equals() {
    final MapEntry<HashableIdentifier, Object> entry = new MapEntry<>(new HashableIdentifier(), new Object());
    final MapEntry<HashableIdentifier, Object> otherEntry = new MapEntry<>(new HashableIdentifier(), new Object());
    final HashableMap<HashableIdentifier, Object> map = new HashableMap<>();
    final HashMap<HashableIdentifier, Object> otherMap = new HashMap<>();
    assertThat(map.equals(otherMap), equalTo(true));
    assertThat(otherMap.equals(map), equalTo(true));
    map.put(entry.getKey(), entry.getValue());
    map.put(otherEntry.getKey(), otherEntry.getValue());
    otherMap.put(entry.getKey(), entry.getValue());
    assertThat(map.equals(otherMap), equalTo(false));
    assertThat(otherMap.equals(map), equalTo(false));
    otherMap.put(otherEntry.getKey(), otherEntry.getValue());
    assertThat(map.equals(otherMap), equalTo(true));
    assertThat(otherMap.equals(map), equalTo(true));
  }
}
