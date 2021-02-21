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

import java.util.ArrayList;
import java.util.List;

public final class Mail implements IMail {

  private final String sender;
  private final String replayRecipient;
  private final String recipient;
  private final String subject;
  private final IContent content;
  private final List<IAttachment> attachments = new ArrayList<>();

  public Mail(
    final String sender,
    final String replayRecipient,
    final String recipient,
    final String subject,
    final IContent content,
    final List<IAttachment> attachments) {
    super();
    this.sender = sender;
    this.replayRecipient = replayRecipient;
    this.recipient = recipient;
    this.subject = subject;
    this.content = content;
    this.attachments.addAll(attachments);
  }

  @Override
  public String getSender() {
    return this.sender;
  }

  @Override
  public String getReplayRecipient() {
    return this.replayRecipient;
  }

  @Override
  public String getRecipient() {
    return this.recipient;
  }

  @Override
  public String getSubject() {
    return this.subject;
  }

  @Override
  public IContent getContent() {
    return this.content;
  }

  @Override
  public List<IAttachment> getAttachments() {
    return this.attachments;
  }

}
