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
package net.anwiba.commons.swing.action;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import net.anwiba.commons.swing.utilities.JComboBoxUtilities;

@SuppressWarnings("rawtypes")
public class JComboBoxActionProvider<T> implements IActionContainerProvider<JComboBox> {

  final JComboBox<T> comboBox;
  JButton button;
  private final Action action;

  public JComboBoxActionProvider(final ComboBoxModel<T> comboBoxModel, final Action action) {
    this(comboBoxModel, action, null);
  }

  @SuppressWarnings("unchecked")
  public JComboBoxActionProvider(final ComboBoxModel<T> comboBoxModel, final Action action, final String toolTip) {
    this.action = action;
    this.comboBox = new JComboBox(comboBoxModel);
    final JButton comboBoxButton = JComboBoxUtilities.getComboBoxButton(this.comboBox);
    final JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    toolBar.setBorder(BorderFactory.createEmptyBorder());
    this.button = toolBar.add(action);
    this.comboBox.setEditor(new ComboBoxEditor() {

      @Override
      public void addActionListener(final ActionListener l) {
        // nothing todo
      }

      @Override
      public Component getEditorComponent() {
        return toolBar;
      }

      @Override
      public Object getItem() {
        return JComboBoxActionProvider.this.comboBox.getSelectedItem();
      }

      @Override
      public void removeActionListener(final ActionListener l) {
        // nothing todo
      }

      @Override
      public void selectAll() {
        // nothing todo
      }

      @Override
      public void setItem(final Object anObject) {
        // nothing todo
      }
    });
    final FocusTraversalPolicy policy = new FocusTraversalPolicy() {

      @Override
      public Component getLastComponent(final Container container) {
        return JComboBoxActionProvider.this.button;
      }

      @Override
      public Component getFirstComponent(final Container container) {
        return JComboBoxActionProvider.this.button;
      }

      @Override
      public Component getDefaultComponent(final Container container) {
        return JComboBoxActionProvider.this.button;
      }

      @Override
      public Component getComponentBefore(final Container container, final Component component) {
        return JComboBoxActionProvider.this.button;
      }

      @Override
      public Component getComponentAfter(final Container container, final Component component) {
        return JComboBoxActionProvider.this.button;
      }
    };
    this.comboBox.setFocusTraversalPolicy(policy);
    this.comboBox.setFocusTraversalPolicyProvider(true);
    this.comboBox.setEditable(true);
    this.comboBox.setBorder(BorderFactory.createEmptyBorder());
    this.comboBox.setMaximumSize(new Dimension(42, 28));
    comboBoxButton.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    comboBoxButton.setFocusable(true);
    comboBoxButton.setToolTipText(toolTip);
  }

  @Override
  public Action getAction() {
    return this.action;
  }

  @Override
  public JComboBox<T> getContainer() {
    return this.comboBox;
  }

  @Override
  public AbstractButton getButton() {
    return this.button;
  }
}
