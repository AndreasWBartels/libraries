/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas W. Bartels 
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
package net.anwiba.commons.ensure.test;

import net.anwiba.commons.ensure.Conditions;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings({ "nls", "boxing" })
public class ConditionsTest {

  @Test
  public void equalToTest() {
    assertTrue(Conditions.equalTo(Integer.valueOf(10)).accept(Integer.valueOf(10)));
    assertFalse(Conditions.equalTo(Integer.valueOf(10)).accept(Integer.valueOf(11)));
  }

  @Test
  public void isNullTest() {
    assertTrue(Conditions.isNull().accept(null));
    assertFalse(Conditions.isNull().accept(Integer.valueOf(11)));
  }

  @Test
  public void isTrueTest() {
    assertTrue(Conditions.isTrue().accept(true));
    assertFalse(Conditions.isTrue().accept(false));
    assertFalse(Conditions.isTrue().accept(null));
  }

  @Test
  public void notTest() {
    assertTrue(Conditions.not(Conditions.isNull()).accept(Integer.valueOf(11)));
    assertFalse(Conditions.not(Conditions.isNull()).accept(null));
  }

  @Test
  public void anyOfTest() {
    assertTrue(Conditions.anyOf(Conditions.isNull(), Conditions.not(Conditions.isNull())).accept(Integer.valueOf(11)));
    assertTrue(Conditions.anyOf(Conditions.isNull(), Conditions.equalTo(Integer.valueOf(11))).accept(
        Integer.valueOf(11)));
    assertFalse(Conditions.anyOf(Conditions.isNull(), Conditions.equalTo(Integer.valueOf(10))).accept(
        Integer.valueOf(11)));
  }

  @Test
  public void allOfTest() {
    assertTrue(Conditions.allOf(Conditions.not(Conditions.isNull()), Conditions.equalTo(Integer.valueOf(11))).accept(
        Integer.valueOf(11)));
    assertFalse(Conditions.allOf(Conditions.isNull(), Conditions.equalTo(Integer.valueOf(11))).accept(
        Integer.valueOf(11)));
    assertFalse(Conditions.allOf(
        Conditions.not(Conditions.isNull()),
        Conditions.not(Conditions.equalTo(Integer.valueOf(11)))).accept(Integer.valueOf(11)));
    assertTrue(Conditions.contains("a").accept("a"));
  }

  @Test
  public void containsTest() {
    assertTrue(Conditions.contains("a").accept("a"));
    assertTrue(Conditions.contains("a").accept("ab"));
    assertTrue(Conditions.contains("a").accept("aa"));
    assertFalse(Conditions.contains("a").accept(null));
    assertFalse(Conditions.contains("a").accept("b"));
  }

  @Test
  public void startsWithTest() {
    assertTrue(Conditions.startsWith("a").accept("a"));
    assertTrue(Conditions.startsWith("a").accept("ab"));
    assertFalse(Conditions.startsWith("a").accept(null));
    assertFalse(Conditions.startsWith("a").accept("b"));
  }

  @Test
  public void endsWithTest() {
    assertTrue(Conditions.endsWith("a").accept("a"));
    assertTrue(Conditions.endsWith("a").accept("ba"));
    assertFalse(Conditions.endsWith("a").accept(null));
    assertFalse(Conditions.endsWith("a").accept("b"));
  }

  @Test
  public void inTest() {
    assertTrue(Conditions.in("a", "b").accept("a"));
    assertFalse(Conditions.in("a", "b").accept("c"));
  }

  @Test
  public void greaterThan() {
    assertTrue(Conditions.greaterThan("b").accept("c"));
    assertFalse(Conditions.greaterThan("b").accept("a"));
    assertFalse(Conditions.greaterThan("b").accept("b"));
    assertTrue(Conditions.greaterThan(2).accept(3));
    assertFalse(Conditions.greaterThan(5).accept(4));
  }

  @Test
  public void lowerThan() {
    assertTrue(Conditions.lowerThan("b").accept("a"));
    assertFalse(Conditions.lowerThan("b").accept("c"));
    assertFalse(Conditions.lowerThan("b").accept("b"));
    assertTrue(Conditions.lowerThan(5).accept(4));
    assertFalse(Conditions.lowerThan(2).accept(3));
  }

  @Test
  public void betweenTest() {
    assertFalse(Conditions.between("b", "d").accept("a"));
    assertTrue(Conditions.between("b", "d").accept("b"));
    assertTrue(Conditions.between("b", "d").accept("c"));
    assertTrue(Conditions.between("b", "d").accept("d"));
    assertFalse(Conditions.between("b", "d").accept("e"));
    assertFalse(Conditions.between(1, 3).accept(0));
    assertTrue(Conditions.between(1, 3).accept(1));
    assertTrue(Conditions.between(1, 3).accept(2));
    assertTrue(Conditions.between(1, 3).accept(3));
    assertFalse(Conditions.between(1, 3).accept(4));
  }

  @Test
  public void IsEmptyTest() {
    assertTrue(Conditions.isEmpty().accept(Arrays.asList()));
    assertFalse(Conditions.isEmpty().accept(Arrays.asList("")));
    assertTrue(Conditions.allOf(
        Conditions.notNull(),
        Conditions.not(Conditions.isEmpty()),
        Conditions.containsAll("a", "b")).accept(Arrays.asList("a", "b", "c")));
  }
}