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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

import static org.junit.Assert.*;

public class HierarchicalClassKeyRegistryTest {

  interface I0 {
    // nothing to do
  }

  interface I1 extends I0 {
    // nothing to do
  }

  interface IN extends I0 {
    // nothing to do
  }

  interface I2 extends I1 {
    // nothing to do
  }

  class C0 implements I2, IN {
    // nothing to do
  }

  @Test
  public void testAdd() {
    final HierarchicalClassKeyRegistry<Object> registry = new HierarchicalClassKeyRegistry<>();
    final Object object = new Object();
    final Object other = new Object();
    final Object another = new Object();
    Object value;

    registry.add(I0.class, object);
    assertThat(registry.get(I0.class), equalTo(object));
    value = registry.get(C0.class);
    assertThat(value, equalTo(object));

    registry.add(I2.class, other);
    assertThat(registry.get(I2.class), equalTo(other));
    value = registry.get(C0.class);
    assertThat(value, equalTo(other));

    registry.add(I1.class, another);
    assertThat(registry.get(I1.class), equalTo(another));
    value = registry.get(C0.class);
    assertThat(value, equalTo(other));
  }

  @Test
  public void testHoldOrderOne() {
    final HierarchicalClassKeyRegistry<Object> registry = new HierarchicalClassKeyRegistry<>();
    final Object object = new Object();
    final Object other = new Object();
    registry.add(I1.class, object);
    registry.add(IN.class, other);
    assertThat(registry.get(I1.class), equalTo(object));
    assertThat(registry.get(IN.class), equalTo(other));
    assertThat(registry.get(C0.class), equalTo(object));
  }

  @Test
  public void testHoldOrderTow() {
    final HierarchicalClassKeyRegistry<Object> registry = new HierarchicalClassKeyRegistry<>();
    final Object object = new Object();
    final Object other = new Object();
    registry.add(IN.class, object);
    registry.add(I1.class, other);
    assertThat(registry.get(IN.class), equalTo(object));
    assertThat(registry.get(I1.class), equalTo(other));
    assertThat(registry.get(C0.class), equalTo(object));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testException() {
    final HierarchicalClassKeyRegistry<Object> registry = new HierarchicalClassKeyRegistry<>();
    final Object object = new Object();
    final Object other = new Object();
    registry.add(I0.class, object);
    registry.add(I0.class, other);
  }

  @Test
  public void testNull() {
    final HierarchicalClassKeyRegistry<Object> registry = new HierarchicalClassKeyRegistry<>();
    final Object object = new Object();
    registry.add(I0.class, object);
    assertThat(registry.get(Object.class), equalTo(null));
  }

}