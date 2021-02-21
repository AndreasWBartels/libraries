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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class MailBuilder {

  private String sender;
  private String replayRecipient;
  private String recipient;
  private String subject;
  private IContent content;
  private final List<IAttachment> attachments = new ArrayList<>();

  public MailBuilder setSender(final String sender) {
    this.sender = sender;
    return this;
  }

  public IMail build() {
    return new Mail(this.sender, this.replayRecipient, this.recipient, this.subject, this.content, this.attachments);
  }

  public MailBuilder setRecipient(final String recipient) {
    this.recipient = recipient;
    return this;
  }

  public MailBuilder setSubject(final String subject) {
    this.subject = subject;
    return this;
  }

  public MailBuilder setContent(final String content) {
    this.content = new Content(content, Charset.defaultCharset().name(), "plain");
    return this;
  }

  public MailBuilder setContent(final String content, final String subtype) {
    this.content = new Content(content, Charset.defaultCharset().name(), subtype);
    return this;
  }

  public MailBuilder addAttachment(
      @SuppressWarnings("hiding") final String content,
      final String filename,
      final String mimeTpye) {
    this.attachments.add(new Attachment(content, filename, mimeTpye));
    return this;
  }

  public MailBuilder setReplayRecipient(final String replayRecipient) {
    this.replayRecipient = replayRecipient;
    return this;
  }

}
