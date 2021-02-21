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
package net.anwiba.commons.swing.transferable;

import java.awt.datatransfer.Transferable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.anwiba.commons.lang.optional.IOptional;
import net.anwiba.commons.lang.stream.Streams;

public final class TransferHandlers implements ITransferHandlers {

  private final List<ITransferHandler> handlers = new LinkedList<>();

  public TransferHandlers(final Collection<ITransferHandler> handlers) {
    this.handlers.addAll(handlers);
  }

  @Override
  public IOptional<ITransferHandler, RuntimeException> getApplicable(
      final Object userObject,
      final Transferable transferable) {
    return Streams.of(this.handlers).filter(c -> c.isApplicable(userObject, transferable)).notNull().first();
  }
}
