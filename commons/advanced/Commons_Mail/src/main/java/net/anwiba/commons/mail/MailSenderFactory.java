/*
 * #%L
 * anwiba commons core
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
 
package net.anwiba.commons.mail;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.crypto.IPassword;
import net.anwiba.crypto.IPasswordCoder;

public class MailSenderFactory {

  private final Session session;
  private final IPasswordCoder passwordCoder;

  public MailSenderFactory(final Session session, final IPasswordCoder passwordCoder) {
    this.session = session;
    this.passwordCoder = passwordCoder;
  }

  public IMailSender create(final String server, final String userName, final IPassword password)
      throws CreationException {
    try {
      return new MailSender(
          this.session.getTransport("smtps"), //$NON-NLS-1$
          this.passwordCoder,
          new MimeMessageFactory(this.session),
          server,
          userName,
          password);
    } catch (final NoSuchProviderException exception) {
      throw new CreationException(exception.getMessage(), exception);
    }
  }
}
