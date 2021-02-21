/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2019 Andreas W. Bartels
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
package net.anwiba.commons.swing.spinner;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.swing.utilities.GuiUtilities;

public class SpinnerPanel {

  private final JComponent spinnerComponent;
  private final JComponent backgroundComponent;

  private final BooleanModel visibleModel = new BooleanModel(false);

  public SpinnerPanel(final JComponent backgroundComponent) {
    this.spinnerComponent = new JPanel(new BorderLayout());
    this.spinnerComponent.add(new JLabel(net.anwiba.commons.swing.icons.GuiIcons.SPINNER_ICON.getLargeIcon()),
        BorderLayout.CENTER);
    this.spinnerComponent.setOpaque(false);
    this.spinnerComponent.setVisible(false);
    this.backgroundComponent = backgroundComponent;
    this.visibleModel.addChangeListener(() -> GuiUtilities.invokeLater(() -> activateSpinner(this.visibleModel.isTrue())));
  }

  public void activate() {
    setActive(true);
  }

  public void deactivate() {
    setActive(false);
  }

  public JComponent getComponent() {
    return this.spinnerComponent;
  }

  private void activateSpinner(final boolean value) {
    this.spinnerComponent.setVisible(value);
    GuiUtilities.setContainerEnabled(this.backgroundComponent, !value);
  }

  public synchronized void setActive(final boolean showSpinner) {
    this.visibleModel.set(showSpinner);
  }

  public synchronized void execute(final Runnable runnable) {
    try {
      activate();
      runnable.run();
    } finally {
      deactivate();
    }
  }

}
