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

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.swing.combobox.ComboBoxObjectFieldEditor;
import net.anwiba.commons.swing.object.AbstractObjectTextField;
import net.anwiba.commons.swing.object.EnumField;
import net.anwiba.commons.swing.object.EnumerationObjectFieldConfigurationBuilder;

public class EnumComboBoxDemo extends AbstractObjectFieldDemo {
  private static enum Type {
    ZERO, ONE, TWO, THREE;
  }

  private AbstractObjectTextField<Enum<?>> createField() {
    final EnumerationObjectFieldConfigurationBuilder builder = new EnumerationObjectFieldConfigurationBuilder(
        Type.class);
    return new EnumField(builder.build());
  }

  @Test
  public void demoEnumComboBox() {
    final AbstractObjectTextField<Enum<?>> field = createField();
    final JComboBox<Enum<?>> comboBox = new JComboBox<>(Type.values());
    final ComboBoxObjectFieldEditor<Enum<?>> editor = new ComboBoxObjectFieldEditor<>(field);
    comboBox.setEditor(editor);
    final JPanel panel = createPanel(comboBox, field);
    comboBox.setEditable(true);
    editor.getModel().set(Type.ONE);
    editor.getModel().addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        comboBox.getModel().setSelectedItem(field.getModel().get());
      }
    });
    show(panel);
  }
}