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
package net.anwiba.commons.swing.component;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IChangeableObjectListener;

public class CheckBox implements IComponentProvider {

  private final IBooleanModel model;
  private JCheckBox checkBox;
  private final String label;
  private final String tooltip;

  public CheckBox() {
    this(new BooleanModel(false));
  }

  public CheckBox(final IBooleanModel model) {
    this(null, model);
  }

  public CheckBox(final String label, final IBooleanModel model) {
    this(label, label, model);
  }

  public CheckBox(final String label, final String tooltip, final IBooleanModel model) {
    this.label = label;
    this.tooltip = tooltip;
    this.model = model;
    this.model.addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        updateView();
      }
    });
  }

  protected void updateView() {
    if (this.checkBox == null) {
      return;
    }
    this.checkBox.getModel().setSelected(this.model.isTrue());
  }

  @Override
  public JComponent getComponent() {
    if (this.checkBox == null) {
      initView();
    }
    return this.checkBox;
  }

  private void initView() {
    this.checkBox = new JCheckBox(this.label);
    this.checkBox.setToolTipText(this.tooltip);
    this.checkBox.getModel().addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(final ItemEvent e) {
        updateModel();
      }
    });
    updateView();
  }

  protected void updateModel() {
    this.model.set(this.checkBox.isSelected());
  }

  public void setEditable(final boolean isEditable) {
    if (this.checkBox == null) {
      initView();
    }
    this.checkBox.setEnabled(isEditable);
  }

  public IBooleanModel getModel() {
    return this.model;
  }

}