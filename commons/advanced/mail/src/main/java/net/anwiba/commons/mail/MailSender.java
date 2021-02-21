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

import java.io.IOException;

import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import net.anwiba.commons.crypto.CodingException;
import net.anwiba.commons.crypto.IPassword;
import net.anwiba.commons.crypto.IPasswordCoder;
import net.anwiba.commons.lang.exception.CreationException;

public class MailSender implements IMailSender {

  private final Transport transport;
  private final IPasswordCoder passwordCoder;
  private final MimeMessageFactory mimeMessageFactory;
  private final String user;
  private final IPassword password;

  public MailSender(
      final Transport transport,
      final IPasswordCoder passwordCoder,
      final MimeMessageFactory mimeMessageFactory,
      final String user,
      final IPassword password) {
    this.transport = transport;
    this.passwordCoder = passwordCoder;
    this.mimeMessageFactory = mimeMessageFactory;
    this.user = user;
    this.password = password;
  }

  @Override
  public void send(final IMail mail) throws IOException {
    try {
      this.transport.connect(this.user, this.passwordCoder.decode(this.password));
      final MimeMessage mimeMessage = this.mimeMessageFactory.create(mail);
      this.transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
    } catch (MessagingException | CodingException | CreationException exception) {
      throw new IOException(exception);
    }
  }

  @Override
  public void close() throws IOException {
    try {
      this.transport.close();
    } catch (final MessagingException exception) {
      throw new IOException(exception);
    }
  }

}
