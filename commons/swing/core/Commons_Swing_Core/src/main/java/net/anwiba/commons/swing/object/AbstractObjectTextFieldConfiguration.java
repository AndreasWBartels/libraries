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
package net.anwiba.commons.swing.object;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.utilities.validation.IValidationResult;

public abstract class AbstractObjectTextFieldConfiguration<T> implements IObjectFieldConfiguration<T> {

  private final boolean isEditable;
  private final int columns;
  private final IToolTipFactory factory;
  private final IObjectModel<IValidationResult> validStateModel;
  private final IObjectModel<T> model;
  private final List<IActionFactory<T>> actionFactorys = new ArrayList<>();
  private final Color backgroundColor;
  private final IKeyListenerFactory<T> keyListenerFactory;

  public AbstractObjectTextFieldConfiguration(
      final IObjectModel<T> model,
      final IObjectModel<IValidationResult> validStateModel,
      final IToolTipFactory toolTipFactory,
      final boolean isEditable,
      final int columns,
      final List<IActionFactory<T>> actionFactorys,
      final IKeyListenerFactory<T> keyListenerFactory,
      final Color backgroundColor) {
    this.model = model;
    this.validStateModel = validStateModel;
    this.factory = toolTipFactory;
    this.isEditable = isEditable;
    this.columns = columns;
    this.keyListenerFactory = keyListenerFactory;
    this.backgroundColor = backgroundColor;
    this.actionFactorys.addAll(actionFactorys);
  }

  @Override
  public Color getBackgroundColor() {
    return this.backgroundColor;
  }

  @Override
  public int getColumns() {
    return this.columns;
  }

  @Override
  public boolean isEditable() {
    return this.isEditable;
  }

  @Override
  public IToolTipFactory getToolTipFactory() {
    return this.factory;
  }

  @Override
  public IObjectModel<T> getModel() {
    return this.model;
  }

  @Override
  public IObjectModel<IValidationResult> getValidationResultModel() {
    return this.validStateModel;
  }

  @Override
  public Collection<IActionFactory<T>> getActionFactorys() {
    return this.actionFactorys;
  }

  @Override
  public IKeyListenerFactory<T> getKeyListenerFactory() {
    return this.keyListenerFactory;
  }
}
