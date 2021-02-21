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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class MapEntryTest {

  @Test
  public void MapEntry() {
    final HashableIdentifier identifier = new HashableIdentifier();
    final Object object = new Object();
    final MapEntry<HashableIdentifier, Object> entry = new MapEntry<>(identifier, object);
    final MapEntry<HashableIdentifier, Object> other = new MapEntry<>(new HashableIdentifier(), new Object());
    assertThat(entry, equalTo(entry));
    assertThat(entry.getKey(), equalTo(identifier));
    assertThat(entry.getValue(), equalTo(object));
    assertThat(entry, not(equalTo(other)));
    assertThat(entry, not(equalTo(new MapEntry<>(identifier, new Object()))));
    entry.setValue(other);
    assertThat(entry, not(equalTo(object)));
    assertThat(entry.getValue(), equalTo((Object) other));
  }
}
