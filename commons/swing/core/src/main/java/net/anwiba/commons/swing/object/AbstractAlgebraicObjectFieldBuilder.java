/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels 
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
package net.anwiba.commons.swing.object;

import java.util.function.Function;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.Timer;

import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.swing.icon.GuiIcon;
import net.anwiba.commons.swing.utilities.GuiUtilities;

public abstract class AbstractAlgebraicObjectFieldBuilder<O, C extends AbstractObjectFieldConfigurationBuilder<O, C>, B extends AbstractObjectFieldBuilder<O, C, B>>
    extends
    AbstractObjectFieldBuilder<O, C, B> {

  public AbstractAlgebraicObjectFieldBuilder(final C builder) {
    super(builder);
  }

  protected IButtonFactory<O> createButton(
      final GuiIcon icon,
      final Function<O, O> funtion,
      final Function<O, Boolean> enabler,
      final int initialDelay,
      final int delay) {
    return (model, document, enabledDistributor, clearBlock) -> {

      final IBooleanModel enabledModel = new BooleanModel(enabler.apply(model.get()));
      final JButton button = new JButton(icon.getSmallIcon()) {

        private static final long serialVersionUID = 1L;

        @Override
        public void setEnabled(final boolean value) {
          super.setEnabled(enabledDistributor.isTrue() && enabledModel.isTrue() && value);
        };
      };
      final ButtonModel buttonModel = button.getModel();

      final Timer timer = new Timer(initialDelay, event -> {
        if (buttonModel.isPressed()) {
          GuiUtilities.invokeLater(() -> model.set(funtion.apply(model.get())));
        } else {
          ((Timer) event.getSource()).stop();
        }
      });
      timer.setDelay(delay);

      button.addChangeListener(event -> {
        if (buttonModel.isPressed()) {
          model.set(funtion.apply(model.get()));
          timer.start();
        }
      });
      model.addChangeListener(() -> enabledModel.set(enabler.apply(model.get())));

      button.setEnabled(enabledModel.isTrue());
      enabledModel.addChangeListener(() -> GuiUtilities.invokeLater(() -> button.setEnabled(enabledModel.isTrue())));
      enabledDistributor
          .addChangeListener(() -> GuiUtilities.invokeLater(() -> button.setEnabled(enabledDistributor.isTrue())));
      return button;
    };
  }

}
