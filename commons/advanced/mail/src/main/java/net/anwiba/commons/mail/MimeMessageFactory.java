/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2016 Andreas W. Bartels
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

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.utilities.string.StringUtilities;

public final class MimeMessageFactory {

  private final Session session;
  private final String senderAddress;

  public MimeMessageFactory(final Session session, final String senderAddress) {
    this.session = session;
    this.senderAddress = senderAddress;
  }

  public MimeMessage create(final IMail mail) throws CreationException {
    try {
      final MimeMessage mimeMessage = new MimeMessage(this.session);
      mimeMessage
          .setFrom(
              new InternetAddress(
                  StringUtilities.isNullOrTrimmedEmpty(mail.getSender())
                      ? this.senderAddress
                      : mail.getSender()));
      if (mail.getReplayRecipient() != null) {
        mimeMessage.setReplyTo(new Address[] { new InternetAddress(mail.getReplayRecipient()) });
      }
      mimeMessage.setRecipients(jakarta.mail.Message.RecipientType.TO,
          InternetAddress.parse(mail.getRecipient(), false));
      mimeMessage.setSubject(mail.getSubject());
      final Multipart multiPart = new MimeMultipart();
      final MimeBodyPart messageBody = new MimeBodyPart();
      IContent content = mail.getContent();
      messageBody.setText(content.getText(), content.getCharset(), content.getSubTpye());
      multiPart.addBodyPart(messageBody);
      for (final IAttachment attachment : mail.getAttachments()) {
        final MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.setFileName(attachment.getFilename());
        attachmentPart
            .setContent(
                attachment.getContent() == null
                    ? "--empty--" //$NON-NLS-1$
                    : attachment.getContent(),
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
