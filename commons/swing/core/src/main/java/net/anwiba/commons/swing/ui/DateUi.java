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
package net.anwiba.commons.swing.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Icon;

import net.anwiba.commons.swing.dialog.DialogMessages;
import net.anwiba.commons.swing.icons.GuiIcons;

public class DateUi extends AbstractObjectUi<Date> {

  private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DialogMessages.FORMAT_DATE);

  @Override
  public String getText(final Date date) {
    return this.DATE_FORMAT.format(date);
  }

  @Override
  public Icon getIcon(final Date object) {
    return GuiIcons.DATE_ICON.getSmallIcon();
  }
}
