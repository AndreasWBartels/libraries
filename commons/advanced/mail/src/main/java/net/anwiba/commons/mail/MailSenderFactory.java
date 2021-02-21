/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2019 Andreas W. Bartels
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
// Copyright (c) 2016 by Andreas W. Bartels
package net.anwiba.commons.mail;

import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Session;
import net.anwiba.commons.crypto.IPassword;
import net.anwiba.commons.crypto.IPasswordCoder;
import net.anwiba.commons.lang.exception.CreationException;

public class MailSenderFactory {

  private final Session session;
  private final IPasswordCoder passwordCoder;
  private final String senderAddress;

  public MailSenderFactory(final Session session, final String senderAddress, final IPasswordCoder passwordCoder) {
    this.session = session;
    this.senderAddress = senderAddress;
    this.passwordCoder = passwordCoder;
  }

  public IMailSender create(final String userName, final IPassword password) throws CreationException {
    try {
      return new MailSender(
          this.session.getTransport("smtps"), //$NON-NLS-1$
          this.passwordCoder,
          new MimeMessageFactory(this.session, this.senderAddress),
          userName,
          password);
    } catch (final NoSuchProviderException exception) {
      throw new CreationException(exception.getMessage(), exception);
    }
  }
}
