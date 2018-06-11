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
package net.anwiba.commons.swing.process;

import java.time.LocalDateTime;
import java.util.ArrayList;

import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.swing.table.AbstractObjectTableModel;
import net.anwiba.commons.swing.table.IColumnClassProvider;

public class ProcessMessageContextTableModel extends AbstractObjectTableModel<IProcessMessageContext> {

  private static final long serialVersionUID = 1L;

  private final Class<?>[] classes = new Class<?>[]{
      MessageType.class,
      LocalDateTime.class,
      String.class,
      String.class };

  public ProcessMessageContextTableModel() {
    super(new ArrayList<IProcessMessageContext>(), new IColumnClassProvider() {

      @Override
      public Class<?> getClass(final int columnIndex) {
        return Object.class;
      }
    });
  }

  @Override
  public int getColumnCount() {
    return 4;
  }

  @Override
  public Class<?> getColumnClass(final int column) {
    return this.classes[column];
  }

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    final IProcessMessageContext context = get(rowIndex);
    if (context == null) {
      return null;
    }
    if (columnIndex == 0) {
      return context.getMessage().getMessageType();
    }
    if (columnIndex == 1) {
      return context.getTime();
    }
    if (columnIndex == 2) {
      return context.getProcessDescription();
    }
    if (columnIndex == 3) {
      return context.getMessage().getText();
    }
    return null;
  }

  public IProcessMessageContext getProsessMessageContext(final int row) {
    return get(row);
  }
}
