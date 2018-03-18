/*
 * #%L
 * *
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
package net.anwiba.commons.swing.dialog.wizard;

import javax.swing.Icon;

import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.object.IObjectReceiver;
import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanModel;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.dialog.DataState;
import net.anwiba.commons.utilities.validation.IValidationResult;

public abstract class AbstractWizardPage implements IWizardPage {

  private final String message;
  private final IBooleanModel nextEnabledModel = new BooleanModel(true);
  private final IBooleanModel backEnabledModel = new BooleanModel(true);
  private final IObjectModel<DataState> dataStateModel = new ObjectModel<>(DataState.UNKNOWN);
  private final Icon icon;
  private final IApplicable<IWizardPage> applicable;
  private final IObjectModel<String> messageReciever = new ObjectModel<>();
  private final IObjectModel<String> titleReciever = new ObjectModel<>("title"); //$NON-NLS-1$

  public AbstractWizardPage(
      final String title,
      final String message,
      final Icon icon,
      final IApplicable<IWizardPage> applicable) {
    this.message = message;
    this.icon = icon;
    this.titleReciever.set(title);
    this.applicable = applicable;
  }

  @Override
  public String getTitle() {
    return this.titleReciever.get();
  }

  @Override
  public String getMessage() {
    final String messageRecieverValue = this.messageReciever.get();
    if (messageRecieverValue != null) {
      return messageRecieverValue;
    }
    return this.message;
  }

  protected boolean isValid(final IObjectDistributor<IValidationResult> validationResultDistributor) {
    final IValidationResult result = validationResultDistributor.get();
    return result == null || result.isValid();
  }

  public IObjectModel<String> getTitleReciever() {
    return this.titleReciever;
  }

  public final IObjectReceiver<String> getMessageReciever() {
    return this.messageReciever;
  }

  @Override
  public final IObjectDistributor<String> getMessageDistributor() {
    return this.messageReciever;
  }

  @Override
  public final IBooleanModel getNextEnabledModel() {
    return this.nextEnabledModel;
  }

  @Override
  public final IBooleanModel getBackEnabledModel() {
    return this.backEnabledModel;
  }

  @Override
  public Icon getIcon() {
    return this.icon;
  }

  @Override
  public final IObjectModel<DataState> getDataStateModel() {
    return this.dataStateModel;
  }

  @Override
  public boolean isApplicable(final IWizardPage object) {
    return this.applicable.isApplicable(object);
  }
}
