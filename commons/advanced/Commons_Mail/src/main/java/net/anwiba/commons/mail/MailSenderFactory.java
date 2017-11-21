// Copyright (c) 2016 by Andreas W. Bartels (bartels@anwiba.de)
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
