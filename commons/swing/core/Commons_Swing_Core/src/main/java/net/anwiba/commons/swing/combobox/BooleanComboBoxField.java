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
package net.anwiba.commons.swing.combobox;

import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.object.IActionNotifier;
import net.anwiba.commons.swing.object.IObjectField;
import net.anwiba.commons.utilities.validation.IValidationResult;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;

public class BooleanComboBoxField implements IObjectField<Boolean> {
  final ObjectModel<Boolean> model = new ObjectModel<>();
  final IObjectModel<IValidationResult> validStateModel = new ObjectModel<>(IValidationResult.valid());
  final JComboBox<Boolean> booleanComboBox = new JComboBox<>(new Boolean[] { Boolean.TRUE, Boolean.FALSE });
  {
    this.booleanComboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        BooleanComboBoxField.this.model.set((Boolean) BooleanComboBoxField.this.booleanComboBox.getSelectedItem());
      }
    });
    this.model.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        BooleanComboBoxField.this.booleanComboBox.setSelectedItem(BooleanComboBoxField.this.model.get());
      }

    });
  }

  private final IActionNotifier actionNotifier = new IActionNotifier() {

    @Override
    public void removeActionListener(final ActionListener l) {
      BooleanComboBoxField.this.booleanComboBox.removeActionListener(l);
    }

    @Override
    public void addActionListener(final ActionListener l) {
      BooleanComboBoxField.this.booleanComboBox.addActionListener(l);
    }
  };

  @Override
  public IObjectDistributor<IValidationResult> getValidationResultDistributor() {
    return this.validStateModel;
  }

  @Override
  public ObjectModel<Boolean> getModel() {
    return this.model;
  }

  @Override
  public JComponent getComponent() {
    return this.booleanComboBox;
  }

  public IActionNotifier getActionNotifier() {
    return this.actionNotifier;
  }

}