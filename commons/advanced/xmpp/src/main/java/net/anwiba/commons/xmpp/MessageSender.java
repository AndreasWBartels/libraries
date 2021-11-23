/*
 * #%L
 * anwiba commons advanced
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
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
package net.anwiba.commons.xmpp;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.message.notification.NotificationException;

public class MessageSender {

  private final static ILogger logger = Logging.getLogger(MessageSender.class);

  private final ConnectionConfiguration configuration;
  private final String userName;
  private final String password;

  private final Iterable<String> saslAuthenticationTypes;

  private final Iterable<String> bodies;

  public MessageSender(
      final ConnectionConfiguration configuration,
      final String userName,
      final String password,
      final List<String> saslAuthenticationTypes,
      final List<String> bodies) {
    this.configuration = configuration;
    this.userName = userName;
    this.password = password;
    this.bodies = bodies;
    this.saslAuthenticationTypes = Collections.unmodifiableList(saslAuthenticationTypes);
  }

  public void send(final IMessage message) throws NotificationException {
    XMPPConnection connection = null;
    try {
      connection = createConnection();
      final ChatManager chatManager = connection.getChatManager();
      for (final String body : this.bodies) {
        send(chatManager, body, message);
      }
    } catch (final XMPPException exception) {
      logger.log(ILevel.ERROR, exception.getMessage(), exception);
      throw new NotificationException("Coudn't send notification. " + toString(message), exception); //$NON-NLS-1$
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  private XMPPConnection createConnection() throws XMPPException {
    for (final String type : this.saslAuthenticationTypes) {
      SASLAuthentication.supportSASLMechanism(type, 0);
    }
    final XMPPConnection connection = new XMPPConnection(this.configuration);
    connection.connect();
    connection.login(this.userName, this.password);
    return connection;
  }

  private void send(final ChatManager chatManager, final String body, final IMessage message) throws XMPPException {
    final Chat chat = chatManager.createChat(body, new DeafMessageListener());
    chat.sendMessage(toString(message));
  }

  private String toString(final IMessage message) {
    switch (message.getMessageType()) {
      case DEFAULT: {
        return message.getText();
      }
      default: {
        return message.getThrowable() == null
            ? MessageFormat.format("{0}: {1}", message.getMessageType().name(), message.getText()) //$NON-NLS-1$
            : MessageFormat.format(
                "{0}: {1} {2}", //$NON-NLS-1$
                message.getMessageType().name(),
                message.getText(),
                Message.toDetailInfo(message.getThrowable()));
      }
    }
  }
}