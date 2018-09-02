/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
package net.anwiba.commons.swing.dialog.pane;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import net.anwiba.commons.message.Message;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.dialog.DataState;
import net.anwiba.commons.swing.object.IObjectFieldConfiguration;
import net.anwiba.commons.swing.object.StringField;
import net.anwiba.commons.swing.utilities.SpringLayoutUtilities;
import net.anwiba.commons.utilities.validation.IValidationResult;

public final class StringFieldContentPane extends AbstractContentPane {

  final StringField stringField;
  private JComponent contentPanel;
  private final String label;

  public StringFieldContentPane(final String label, final IObjectFieldConfiguration<String> configuration) {
    this.label = label;
    this.stringField = new StringField(configuration);
    this.stringField.getModel().addChangeListener(() -> {
      final IValidationResult validationResult = this.stringField.getValidationResultDistributor().get();
      if (validationResult.isValid()) {
        getDataStateModel().set(DataState.VALIDE);
        getMessageModel().set(null);
        return;
      }
      getDataStateModel().set(DataState.INVALIDE);
      getMessageModel()
          .set(Message.create(validationResult.getMessage(), validationResult.getMessage(), MessageType.ERROR));
    });
  }

  public IObjectModel<String> getModel() {
    return this.stringField.getModel();
  }

  @Override
  public JComponent getComponent() {
    if (this.contentPanel != null) {
      return this.contentPanel;
    }
    final JPanel panel = new JPanel();
    panel.setLayout(new SpringLayout());
    if (this.label == null) {
      panel.add(this.stringField.getComponent());
      SpringLayoutUtilities.makeCompactGrid(panel, 1, 1, 6, 6, 6, 6);
      this.contentPanel = new JScrollPane(panel);
      return this.contentPanel;
    }
    panel.add(new JLabel(this.label));
    panel.add(this.stringField.getComponent());
    SpringLayoutUtilities.makeCompactGrid(panel, 2, 1, 6, 6, 6, 6);
    this.contentPanel = new JScrollPane(panel);
    return this.contentPanel;
  }

  @Override
  public IObjectModel<DataState> getDataStateModel() {
    return super.getDataStateModel();
  }
}
