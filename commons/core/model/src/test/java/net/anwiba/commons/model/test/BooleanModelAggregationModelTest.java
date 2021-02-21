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
package net.anwiba.commons.model.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.BooleanModelAggregationModel;

public class BooleanModelAggregationModelTest {

  @Test
  public void test() {
    final BooleanModelAggregationModel model = new BooleanModelAggregationModel();
    assertTrue(model.isTrue());
    final BooleanModel booleanModel = new BooleanModel(true);
    model.add(booleanModel);
    assertTrue(model.isTrue());
    booleanModel.set(false);
    assertFalse(model.isTrue());
    model.remove(booleanModel);
    assertTrue(model.isTrue());
  }
}
