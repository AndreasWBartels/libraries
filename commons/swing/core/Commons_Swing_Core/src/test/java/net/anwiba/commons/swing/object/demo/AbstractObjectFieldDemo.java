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

import java.awt.Component;
import java.text.MessageFormat;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import de.jdemo.extensions.SwingDemoCase;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.swing.object.IObjectField;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.swing.utilities.SpringLayoutUtilities;
import net.anwiba.commons.utilities.validation.IValidationResult;

public class AbstractObjectFieldDemo extends SwingDemoCase {

  private static ILogger logger = Logging.getLogger(AbstractObjectFieldDemo.class.getName());

  protected JPanel createPanel(final IObjectField<?> field) {
    return createPanel(field.getComponent(), field);
  }

  protected JPanel createPanel(final Component component, final IObjectField<?> field) {
    final JTextField textField = new JTextField();
    textField.setEditable(false);
    final JTextField validField = new JTextField();
    validField.setEditable(false);
    field.getModel().addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        logger.log(
            ILevel.DEBUG,
            MessageFormat.format(
                "Object changed to: {0}", //$NON-NLS-1$
                field.getModel().get()));
        update(field, textField, validField);
      }
    });
    field.getValidationResultDistributor().addChangeListener(new IChangeableObjectListener() {

      @Override
      public void objectChanged() {
        logger.log(
            ILevel.DEBUG,
            MessageFormat.format(
                "valid state changed to: {0}", //$NON-NLS-1$
                Boolean.valueOf(field.getValidationResultDistributor().get().isValid())));
        update(field, textField, validField);
      }
    });
    update(field, textField, validField);
    final JPanel panel = new JPanel();
    panel.setLayout(new SpringLayout());
    panel.add(component);
    panel.add(textField);
    panel.add(validField);
    SpringLayoutUtilities.makeCompactGrid(panel, 1, 3, 0, 0, 0, 0);
    return panel;
  }

  protected void update(final IObjectField<?> field, final JTextField textField, final JTextField validField) {
    final IValidationResult state = field.getValidationResultDistributor().get();
    final Object value = field.getModel().get();
    GuiUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        validField.setText(state.isValid() ? "valid" : MessageFormat.format("invalid: {0}", state.getMessage())); //$NON-NLS-1$//$NON-NLS-2$
        if (state.isValid()) {
          final String text = value == null ? null : value.toString();
          textField.setText(text);
          textField.setToolTipText(text);
        }
      }
    });
  }
}