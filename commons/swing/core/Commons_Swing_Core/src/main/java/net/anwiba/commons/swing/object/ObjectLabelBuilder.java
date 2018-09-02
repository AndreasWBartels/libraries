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
package net.anwiba.commons.swing.object;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.anwiba.commons.lang.object.IObjectReceiver;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.ui.IObjectUi;
import net.anwiba.commons.swing.ui.ObjectUiBuilder;
import net.anwiba.commons.swing.utilities.GuiUtilities;

public class ObjectLabelBuilder<T> {

  public static final class ObjectLabel<T> implements IObjectLabel<T> {

    private final IObjectModel<T> model;
    private final IObjectUi<T> objectUi;

    public ObjectLabel(final IObjectModel<T> model, final IObjectUi<T> objectUi) {
      this.model = model;
      this.objectUi = objectUi;
    }

    @Override
    public JComponent getComponent() {
      final JLabel label = new JLabel(
          this.objectUi.getText(this.model.get()),
          this.objectUi.getIcon(this.model.get()),
          SwingConstants.LEADING);
      label.setToolTipText(this.objectUi.getToolTipText(this.model.get()));
      this.model.addChangeListener(() -> GuiUtilities.invokeLater(() -> {
        label.setIcon(this.objectUi.getIcon(this.model.get()));
        label.setText(this.objectUi.getText(this.model.get()));
        label.setToolTipText(this.objectUi.getToolTipText(this.model.get()));
      }));
      return label;
    }

    @Override
    public IObjectReceiver<T> getReciever() {
      return this.model;
    }
  }

  private IObjectUi<T> objectUi = new ObjectUiBuilder<T>()
      .text(object -> Optional.of(object).convert(o -> o.toString()).get())
      .build();
  private IObjectModel<T> model = new ObjectModel<>();

  public ObjectLabelBuilder<T> setModel(final IObjectModel<T> model) {
    this.model = model;
    return this;
  }

  public ObjectLabelBuilder<T> setObjectUi(final IObjectUi<T> objectUi) {
    this.objectUi = objectUi;
    return this;
  }

  public IObjectLabel<T> build() {
    return new ObjectLabel<>(this.model, this.objectUi);
  }
}
