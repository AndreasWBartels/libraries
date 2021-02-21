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

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.combobox.ComboBoxObjectFieldEditor;
import net.anwiba.commons.swing.object.StringField;

public class StringComboBoxDemo extends AbstractObjectFieldDemo {

  @Test
  public void demoStringComboBox() {
    final StringField field = createField();
    final ComboBoxObjectFieldEditor<String> editor = new ComboBoxObjectFieldEditor<>(field);
    final IObjectModel<String> editorModel = editor.getModel();
    final JComboBox<String> comboBox = createDisabledEditableComboBox(editor);
    final ComboBoxModel<String> comboBoxModel = comboBox.getModel();
    editorModel.set("value"); //$NON-NLS-1$
    editorModel.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        comboBoxModel.setSelectedItem(editorModel.get());
      }
    });
    final JPanel panel = createPanel(comboBox, field);
    show(panel);
  }

  private JComboBox<String> createDisabledEditableComboBox(final ComboBoxObjectFieldEditor<String> editor) {
    final JComboBox<String> comboBox = new JComboBox<>();
    comboBox.setEditor(editor);
    comboBox.setEditable(true);
    comboBox.setAutoscrolls(false);
    comboBox.setEnabled(false);
    comboBox.setMaximumRowCount(0);
    editor.getEditorComponent().setEnabled(true);
    return comboBox;
  }

  private StringField createField() {
    return new StringField();
  }
}