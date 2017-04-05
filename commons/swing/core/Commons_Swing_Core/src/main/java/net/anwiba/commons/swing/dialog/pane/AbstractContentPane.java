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
package net.anwiba.commons.swing.dialog.pane;

import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.dialog.DataState;

public abstract class AbstractContentPane implements IContentPanel {

  private final IObjectModel<DataState> dataStateModel = new ObjectModel<>(DataState.UNKNOWN);
  private final IObjectModel<IMessage> messageModel = new ObjectModel<>();

  @Override
  public IObjectModel<DataState> getDataStateModel() {
    return this.dataStateModel;
  }

  @Override
  public IObjectModel<IMessage> getMessageModel() {
    return this.messageModel;
  }

  @Override
  public boolean apply() {
    return true;
  }

  @Override
  public boolean tryOut() {
    return true;
  }

  @Override
  public boolean cancel() {
    return true;
  }

  @Override
  public void close() {
    // nothing to do
  }

}
