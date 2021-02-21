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
package net.anwiba.commons.swing.object.demo;

import static net.anwiba.testing.demo.JFrames.show;

import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.swing.object.EnumField;
import net.anwiba.commons.swing.object.EnumerationObjectFieldConfigurationBuilder;
import net.anwiba.commons.swing.object.IObjectField;

public class EnumFieldDemo extends AbstractObjectFieldDemo {

  private static enum Type {
    ZERO, ONE, TWO, THREE;
  }

  @Test
  public void demoEnumField() {
    final IObjectField<?> field = createField();
    final JPanel panel = createPanel(field);
    show(panel);
  }

  private IObjectField<Enum<?>> createField() {
    final EnumerationObjectFieldConfigurationBuilder builder =
        new EnumerationObjectFieldConfigurationBuilder(Type.class);
    final IObjectField<Enum<?>> field = new EnumField(builder.build());
    return field;
  }

  @Test
  public void demoPreSetEnumField() {
    final IObjectField<Enum<?>> field = createField();
    final JPanel panel = createPanel(field);
    field.getModel().set(Type.ONE);
    show(panel);
  }

  @Test
  public void demoEnumComboBox() {
    final IObjectField<Enum<?>> field = createField();
    final JPanel panel = createPanel(field);
    field.getModel().set(Type.ONE);
    show(panel);
  }
}