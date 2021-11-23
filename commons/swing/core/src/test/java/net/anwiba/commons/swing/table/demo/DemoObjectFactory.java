/*
 * #%L
 * anwiba commons swing
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
package net.anwiba.commons.swing.table.demo;

import java.util.ArrayList;
import java.util.List;

import net.anwiba.commons.lang.random.RandomIsNullDecider;
import net.anwiba.commons.lang.random.RandomObjectGenerator;

public class DemoObjectFactory {

  private final RandomObjectGenerator generator;

  public DemoObjectFactory(final long seed) {
    this.generator = new RandomObjectGenerator(seed, new RandomIsNullDecider(seed, .05));
  }

  public List<DemoObject> createObjectList(final int numberOfObjects) {
    final List<DemoObject> objects = new ArrayList<>();
    for (int i = 0; i < numberOfObjects; i++) {
      objects.add(createObject());
    }
    return objects;
  }

  public DemoObject createObject() {
    final Integer nummer = this.generator.generateInteger();
    final String name = this.generator.generateString();
    final Double value = this.generator.generateDouble();
    final Boolean flag = this.generator.generateBoolean();
    return new DemoObject(nummer, name, value, flag);
  }
}
