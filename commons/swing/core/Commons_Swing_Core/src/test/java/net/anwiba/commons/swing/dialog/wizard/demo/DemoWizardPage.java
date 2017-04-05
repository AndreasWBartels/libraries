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
package net.anwiba.commons.swing.dialog.wizard.demo;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import net.anwiba.commons.model.BooleanModel;
import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.dialog.DataState;
import net.anwiba.commons.swing.dialog.wizard.IWizardPage;

public class DemoWizardPage implements IWizardPage {

  private final String message;
  private final IObjectModel<String> messageModel = new ObjectModel<>();
  private final BooleanModel nextEnabledModel = new BooleanModel(true);
  private final BooleanModel backEnabledModel = new BooleanModel(true);
  private final IObjectModel<DataState> dataStateModel = new ObjectModel<>();
  private final Icon icon;
  private final boolean finishable;

  public DemoWizardPage(final String message, final Icon icon, final boolean finishable, final DataState state) {
    this.message = message;
    this.icon = icon;
    this.finishable = finishable;
    this.dataStateModel.set(state);
  }

  @Override
  public JComponent getComponent() {
    return new JLabel(this.message);
  }

  @Override
  public String getTitle() {
    return this.message;
  }

  @Override
  public String getMessage() {
    return this.message;
  }

  @Override
  public IBooleanDistributor getNextEnabledModel() {
    return this.nextEnabledModel;
  }

  @Override
  public IBooleanDistributor getBackEnabledModel() {
    return this.backEnabledModel;
  }

  @Override
  public Icon getIcon() {
    return this.icon;
  }

  @Override
  public IObjectModel<DataState> getDataStateModel() {
    return this.dataStateModel;
  }

  @Override
  public boolean finishable() {
    return this.finishable;
  }

  @Override
  public boolean isApplicable(final IWizardPage object) {
    return true;
  }

  @Override
  public IObjectDistributor<String> getMessageDistributor() {
    return this.messageModel;
  }
}
