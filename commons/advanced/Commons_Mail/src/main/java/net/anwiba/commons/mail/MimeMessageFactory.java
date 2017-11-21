/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2016 Andreas W. Bartels (bartels@anwiba.de)
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

import java.util.Date;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import net.anwiba.commons.lang.exception.CreationException;

public final class MimeMessageFactory {

  private final Session session;

  public MimeMessageFactory(final Session session) {
    this.session = session;
  }

  public MimeMessage create(final IMail mail) throws CreationException {
    try {
      final MimeMessage mimeMessage = new MimeMessage(this.session);
      mimeMessage.setFrom(new InternetAddress(mail.getSender()));
      if (mail.getReplayRecipient() != null) {
        mimeMessage.setReplyTo(new Address[]{ new InternetAddress(mail.getReplayRecipient()) });
      }
      mimeMessage.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(mail.getRecipient(), false));
      mimeMessage.setSubject(mail.getSubject());
      final Multipart multiPart = new MimeMultipart();
      final BodyPart messageBody = new MimeBodyPart();
      messageBody.setText(mail.getContent());
      multiPart.addBodyPart(messageBody);
      for (final Attachment attachment : mail.getAttachments()) {
        final MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.setFileName(attachment.getFilename());
        attachmentPart.setContent(
            attachment.getContent() == null ? "--empty--" : attachment.getContent(), //$NON-NLS-1$
            attachment.getMimeTpye());
        multiPart.addBodyPart(attachmentPart);
      }
      mimeMessage.setContent(multiPart);
      mimeMessage.setSentDate(new Date());
      return mimeMessage;
    } catch (final MessagingException exception) {
      throw new CreationException(exception.getMessage(), exception);
    }
  }
}
