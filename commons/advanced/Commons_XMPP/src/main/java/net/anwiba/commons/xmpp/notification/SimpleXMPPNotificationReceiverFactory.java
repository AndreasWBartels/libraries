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
package net.anwiba.commons.xmpp.notification;

import java.util.List;

import net.anwiba.commons.message.notification.INotificationReceiver;
import net.anwiba.commons.message.notification.INotificationReceiverFactory;
import net.anwiba.commons.xmpp.MessageSenderBuilder;
import net.anwiba.commons.xmpp.SecurityMode;

public class SimpleXMPPNotificationReceiverFactory implements INotificationReceiverFactory {

  private final String host;
  private final String userName;
  private final String password;
  private final List<String> receivers;

  SimpleXMPPNotificationReceiverFactory(
      final String host,
      final String userName,
      final String password,
      final List<String> receivers) {
    this.host = host;
    this.userName = userName;
    this.password = password;
    this.receivers = receivers;
  }

  @Override
  public INotificationReceiver create() {
    final MessageSenderBuilder builder = new MessageSenderBuilder(this.host, this.userName, this.password);
    builder.setSASLAuthenticationEnabled(true).setSendPresence(false).setSecurityMode(SecurityMode.ENABLE);
    // .addSASLAuthenticationType("PLAIN");
    // .addSASLAuthenticationType("DIGEST-MD5")
    for (final String receiver : this.receivers) {
      builder.addReceiver(receiver);
    }

    return new XMPPNotificationReceiver(builder.build());
  }

}
