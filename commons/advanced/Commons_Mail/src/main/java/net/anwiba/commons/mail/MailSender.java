// Copyright (c) 2016 by Andreas W. Bartels (bartels@anwiba.de)

package net.anwiba.commons.mail;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.crypto.CodingException;
import net.anwiba.crypto.IPassword;
import net.anwiba.crypto.IPasswordCoder;

public class MailSender implements IMailSender {

  private final Transport transport;
  private final IPasswordCoder passwordCoder;
  private final MimeMessageFactory mimeMessageFactory;
  private final String host;
  private final String user;
  private final IPassword password;

  public MailSender(
      final Transport transport,
      final IPasswordCoder passwordCoder,
      final MimeMessageFactory mimeMessageFactory,
      final String host,
      final String user,
      final IPassword password) {
    this.transport = transport;
    this.passwordCoder = passwordCoder;
    this.mimeMessageFactory = mimeMessageFactory;
    this.host = host;
    this.user = user;
    this.password = password;
  }

  @Override
  public void send(final IMail mail) throws IOException {
    try {
      this.transport.connect(this.host, this.user, this.passwordCoder.decode(this.password));
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
