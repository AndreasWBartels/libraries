/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
package net.anwiba.commons.message;

import net.anwiba.commons.lang.exception.Throwables;

final class MessageExceptionToMessagePresentationConverter implements
    IThrowableToMessagePresentationConverter {
  @Override
  public String toText(final Throwable throwable) {
    return getMessage(throwable).getText();
  }

  @Override
  public String toDescription(final Throwable throwable) {
    return getMessage(throwable).getDescription();
  }

  @Override
  public String toDetailInfo(final Throwable throwable) {
    if (Throwables.isApplicable(throwable)) {
      return String.join("\n", Throwables.toString(throwable), " ", Throwables.toStackTraceString(throwable));
    }
    return Throwables.toStackTraceString(throwable);
  }

  private IMessage getMessage(final Throwable throwable) {
    return ((MessageException) throwable).getMessageObject();
  }

  @Override
  public boolean isApplicable(final Throwable throwable) {
    return throwable instanceof MessageException;
  }
}
