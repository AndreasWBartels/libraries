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
package net.anwiba.commons.lang.tree;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import net.anwiba.commons.lang.comparable.AlpaNumericStringComparator;
import net.anwiba.commons.lang.comparable.ComparableComparator;
import net.anwiba.commons.lang.counter.IntCounter;
import net.anwiba.commons.lang.random.IIsNullDecider;
import net.anwiba.commons.lang.random.RandomObjectGenerator;
import net.anwiba.commons.lang.tree.converter.ItemToStringConverter;
import net.anwiba.commons.lang.tree.converter.TreeToStringConverter;
import net.anwiba.commons.lang.tree.distance.IntegerDistanceCalculator;
import net.anwiba.commons.lang.tree.distance.LevenshteinStringDistanceCalculator;

@SuppressWarnings({ "nls" })
public class TreeTest {

  final Comparator<Integer> intergerComparator = new ComparableComparator<>();
  final IntegerDistanceCalculator integerdistanceCalculator = new IntegerDistanceCalculator();
  final Comparator<String> stringComparator = new AlpaNumericStringComparator();
  // final Comparator<String> stringComparator = new ComparableComparator<>();
  final LevenshteinStringDistanceCalculator stringDistanceCalculator = new LevenshteinStringDistanceCalculator();

  @Test
  public void verteilung() throws Exception {
    final Tree<Integer, Integer> tree = _initializeWithKeyEqualValue(
        new Tree<>(this.intergerComparator, 11, new TreeItemChooser<Integer, Integer>(this.integerdistanceCalculator)),
        1,
        101,
        102,
        103,
        104,
        105,
        106,
        107,
        108,
        109,
        1000);
    assertThat(tree.getFirst().getKey(), equalTo(1));
    assertThat(tree.getLast().getKey(), equalTo(1000));
    assertEqualTo(tree.getValues(), 1, 101, 102, 103, 104, 105, 106, 107, 108, 109, 1000);
    insert(tree, 500);
    assertEqualTo(tree.getValues(), 1, 101, 102, 103, 104, 105, 106, 107, 108, 500, 1000);
    insert(tree, 250);
    assertEqualTo(tree.getValues(), 1, 101, 102, 103, 104, 105, 106, 107, 250, 500, 1000);
    insert(tree, 50);
    assertEqualTo(tree.getValues(), 1, 50, 102, 103, 104, 105, 106, 107, 250, 500, 1000);
    insert(tree, 75);
    assertEqualTo(tree.getValues(), 1, 50, 75, 103, 104, 105, 106, 107, 250, 500, 1000);
    insert(tree, 85);
    assertEqualTo(tree.getValues(), 1, 50, 75, 85, 104, 105, 106, 107, 250, 500, 1000);
    insert(tree, 170);
    assertEqualTo(tree.getValues(), 1, 50, 75, 85, 104, 105, 106, 170, 250, 500, 1000);
  }

  @Test
  public void address() throws Exception {
    final List<String> values = read("addressen.txt");
    final Tree<String, String> stringTree = new Tree<>(
        this.stringComparator,
        2000,
        new TreeItemChooser<String, String>(this.stringDistanceCalculator));
    insert(stringTree, values, 0, 16);
    insert(stringTree, values, 16, values.size() - 16);
    final Map<String, Integer> map = new HashMap<>();
    final IntCounter counter = new IntCounter(-1);
    for (final String string : stringTree.getValues()) {
      map.put(string, counter.next());
    }
    final Tree<Integer, String> integerTree = new Tree<>(
        this.intergerComparator,
        16,
        new TreeItemChooser<Integer, String>(this.integerdistanceCalculator));
    for (final String string : values) {
      integerTree.insert(map.get(string), string);
    }
    // print(integerTree.getValues());
    // print(ItemToStringConverter.COMPLEX, integerTree);
  }

  private List<String> read(final String name) throws IOException {
    final ArrayList<String> list = new ArrayList<>();
    final URL resource = getClass().getClassLoader().getResource(name);
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream()));) {
      String line;
      while ((line = reader.readLine()) != null) {
        list.add(line);
      }
      return list;
    }
  }

  @Test
  public void initialize() throws Exception {
    final ITree<Integer, Integer> tree = new Tree<>(this.intergerComparator);
    assertThat(tree.isEmpty(), equalTo(true));
    assertThat(tree.size(), equalTo(0));
  }

  @Test
  public void insert() throws Exception {
    final Tree<Integer, Integer> tree = new Tree<>(this.intergerComparator);
    insert(tree, 1);
    assertThat(tree.isEmpty(), equalTo(false));
    assertThat(tree.size(), equalTo(1));
    insert(tree, 1);
    assertThat(tree.isEmpty(), equalTo(false));
    assertThat(tree.size(), equalTo(1));
    insert(tree, 3);
    insert(tree, 2);
    assertThat(tree.size(), equalTo(3));
    assertSorted(tree.getValues(), tree.size());
    insert(tree, 9);
    insert(tree, 8);
    insert(tree, 5);
    insert(tree, 7);
    insert(tree, 6);
    insert(tree, 4);
    insert(tree, 10);
    insert(tree, 11);
    assertThat(tree.size(), equalTo(11));
    assertSorted(tree.getValues(), tree.size());
    assertSorted(tree.getKeys(), tree.size());
  }

  @Test
  public void remove() throws Exception {
    final Tree<Integer, Integer> tree = new Tree<>(this.intergerComparator);
    insert(tree, 1, 1);
    insert(tree, 2, 2);
    insert(tree, 3, 3);
    insert(tree, 4, 4);
    insert(tree, 5, 10);
    insert(tree, 6, 5);
    insert(tree, 7, 6);
    insert(tree, 8, 7);
    assertThat(tree.size(), equalTo(8));
    remove(tree, 5);
    assertThat(tree.size(), equalTo(7));
    assertSorted(tree.getValues(), tree.size());
  }

  @Test
  public void get() throws Exception {
    final Tree<Integer, Integer> tree = _initializeWithKeySequencer(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    assertThat(tree.size(), equalTo(10));
    assertThat(tree.get(7), equalTo(7));
  }

  @Test
  public void maximumSize() throws Exception {
    final Tree<Integer, Integer> tree = _initializeWithKeySequencer(
        new Tree<>(this.intergerComparator, 10, new TreeItemChooser<Integer, Integer>(this.integerdistanceCalculator)),
        0,
        1,
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9);
    assertThat(tree.size(), equalTo(10));
    insert(tree, 18);
    assertThat(tree.size(), equalTo(10));
    remove(tree, 0);
    assertThat(tree.size(), equalTo(9));
    insert(tree, 16);
    insert(tree, 23);
    insert(tree, 0);
    assertThat(tree.size(), equalTo(10));
  }

  @Test
  public void maximumSizeStringTree() throws Exception {
    final Tree<String, String> tree = new Tree<>(
        this.stringComparator,
        10,
        new TreeItemChooser<String, String>(this.stringDistanceCalculator));
    final RandomObjectGenerator generator = new RandomObjectGenerator(4711, new IIsNullDecider() {

      @Override
      public boolean isNull() {
        return false;
      }
    }, 16);
    String min = "ZZZZZZZZZZZZZZZZ";
    String max = "";
    for (int i = 0; i < 100000; i++) {
      final String value = generator.generateString();
      min = this.stringComparator.compare(min, value) < 0 ? min : value;
      max = this.stringComparator.compare(max, value) > 0 ? max : value;
      insert(tree, value);
    }
    // print(tree);
    assertThat(tree.size(), equalTo(10));
    assertThat(tree.getFirst().getKey(), equalTo(min));
    assertThat(tree.getLast().getKey(), equalTo(max));
    assertEqualTo(tree.getKeys(), "0", "8", "9", "a", "f", "g", "h", "l", "rhi", "zzzu82e");
    // assertEqualTo(tree.getKeys(), "0", "9kyz", "9z", "bs", "ff", "ls97", "lvs", "peq1i", "ynrli", "zzzu82e");
  }

  @Test
  public void distanceMin() throws Exception {
    final Tree<Integer, Integer> tree = _initializeWithKeyEqualValue(
        new Tree<>(this.intergerComparator, 8, new TreeItemChooser<Integer, Integer>(this.integerdistanceCalculator)),
        1,
        2,
        3,
        4,
        5,
        6,
        7,
        8);
    // print(tree);
    insert(tree, 0);
    // print(tree);
    assertThat(tree.size(), equalTo(8));
    assertThat(tree.getFirst().getKey(), equalTo(0));
    assertThat(tree.getLast().getKey(), equalTo(8));
  }

  @Test
  public void distanceMax() throws Exception {
    final Tree<Integer, Integer> tree = _initializeWithKeyEqualValue(
        new Tree<>(this.intergerComparator, 8, new TreeItemChooser<Integer, Integer>(this.integerdistanceCalculator)),
        0,
        1,
        2,
        3,
        4,
        5,
        6,
        7);
    // print(tree);
    insert(tree, 8);
    // print(tree);
    assertThat(tree.size(), equalTo(8));
    assertThat(tree.getFirst().getKey(), equalTo(0));
    assertThat(tree.getLast().getKey(), equalTo(8));
  }

  @Test
  public void distance() throws Exception {
    final Random random = new Random(4711);
    final Tree<Integer, Integer> tree = _initializeWithKeySequencer(
        new Tree<>(this.intergerComparator, 10, new TreeItemChooser<Integer, Integer>(this.integerdistanceCalculator)));
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    for (int i = 0; i < 10000; i++) {
      final int next = random.nextInt(1000000);
      min = Math.min(min, next);
      max = Math.max(max, next);
      insert(tree, next);
    }
    // print(tree);
    assertThat(tree.size(), equalTo(10));
    assertThat(tree.getFirst().getKey(), equalTo(min));
    assertThat(tree.getLast().getKey(), equalTo(max));
    assertEqualTo(tree.getValues(), 174, 109110, 218870, 328207, 437839, 549213, 660811, 772719, 885472, 999930);
  }

  @SuppressWarnings("unchecked")
  private <T> void assertEqualTo(final Iterable<T> values, final T... expectedValues) {
    int counter = 0;
    for (final T value : values) {
      assertThat(value, equalTo(expectedValues[counter++]));
    }
  }

  private Tree<Integer, Integer> _initializeWithKeySequencer(final int... values) {
    final Tree<Integer, Integer> tree = new Tree<>(this.intergerComparator);
    int counter = 0;
    for (final int value : values) {
      insert(tree, counter++, value);
    }
    return tree;
  }

  private <T> Tree<Integer, T> _initializeWithKeySequencer(
      final Tree<Integer, T> tree,
      @SuppressWarnings("unchecked") final T... values) {
    int counter = 0;
    for (final T value : values) {
      insert(tree, counter++, value);
    }
    return tree;
  }

  private <T> Tree<T, T> _initializeWithKeyEqualValue(
      final Tree<T, T> tree,
      @SuppressWarnings("unchecked") final T... values) {
    for (final T value : values) {
      insert(tree, value, value);
    }
    return tree;
  }

  private <T> void remove(final Tree<T, T> tree, final T key) {
    tree.remove(key);
  }

  private <V> void insert(final Tree<V, V> tree, final List<V> values, final int offset, final int length) {
    for (int i = 0; i < length; i++) {
      insert(tree, values.get(offset + i));
    }
  }

  private <V> void insert(final Tree<V, V> tree, final V value) {
    insert(tree, value, value);
  }

  private <K, T> void insert(final Tree<K, T> tree, final K key, final T value) {
    tree.insert(key, value);
  }

  @SuppressWarnings("unused")
  private <T> void print(final Tree<T, T> tree) {
    print(ItemToStringConverter.COMPLEX, tree);
  }

  private <K, V> void print(final int format, final Tree<K, V> tree) {
    final TreeToStringConverter<K, V> toStringConverter = new TreeToStringConverter<>(format);
    System.out.println(toStringConverter.toString(tree));
    System.out.println("----");
  }

  @SuppressWarnings("unused")
  private <T> void print(final Iterable<T> values) {
    final boolean flag = false;
    for (final T value : values) {
      if (flag) {
        System.out.println(", ");
      }
      System.out.println(value.toString());
    }
    System.out.println("----");
  }

  private void assertSorted(final Iterable<Integer> values, final int size) {
    int counter = 0;
    for (final Integer value : values) {
      counter++;
      assertThat(value, equalTo(counter));
    }
    assertThat(counter, equalTo(size));
  }

}
