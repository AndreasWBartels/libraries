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
package net.anwiba.commons.swing.dialog.tabbed;

import javax.swing.Icon;

import net.anwiba.commons.message.IMessage;

public abstract class AbstractNoneEditTabbedDialogTab extends AbstractDialogTab {

  public AbstractNoneEditTabbedDialogTab(final String title, final IMessage defaultMessage, final Icon defaultIcon) {
    super(title, defaultMessage, defaultIcon);
  }

  @Override
  public final void checkFieldValues() {
    // nothing to do
  }

  @Override
  public final boolean apply() {
    return true;
  }

  @Override
  public final void updateView() {
    // nothing to do
  }

}
