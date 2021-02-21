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
package net.anwiba.commons.swing.dialog.wizard;

import java.awt.Container;

import javax.swing.Icon;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.IChangeableObjectListener;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.swing.dialog.DataState;

public interface IWizardController {

  void next();

  void previous();

  boolean cancel();

  boolean finish();

  IMessage getMessage();

  IWizardState getWizardState();

  void addChangeListener(IChangeableObjectListener changeableObjectListener);

  void removeChangeListener(IChangeableObjectListener changeableObjectListener);

  IBooleanDistributor getNextEnabledDistributor();

  IBooleanDistributor getBackEnabledDistributor();

  IObjectDistributor<DataState> getDataStateDistributor();

  IObjectDistributor<String> getMessageDistributor();

  Icon getIcon();

  boolean hasNext();

  boolean hasPrevious();

  boolean apply();

  boolean isFinishable();

  Container getContentPane();

}
