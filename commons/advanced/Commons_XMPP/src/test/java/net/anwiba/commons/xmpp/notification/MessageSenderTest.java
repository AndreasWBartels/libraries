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

import org.jivesoftware.smack.SmackConfiguration;
import org.junit.Ignore;
import org.junit.Test;

import net.anwiba.commons.logging.LoggingUtilities;
import net.anwiba.commons.message.Message;
import net.anwiba.commons.message.MessageType;
import net.anwiba.commons.message.notification.NotificationException;
import net.anwiba.commons.xmpp.MessageSenderBuilder;
import net.anwiba.commons.xmpp.SecurityMode;

public class MessageSenderTest {

  @SuppressWarnings("nls")
  @Ignore
  @Test
  public void testSend() throws NotificationException {
    LoggingUtilities.initialize("DEBUG");
    SmackConfiguration.setPacketReplyTimeout(5000);
    final MessageSenderBuilder builder = new MessageSenderBuilder("www.host.de", "foo", "foo");
    // builder.setServiceName("xabber.de");
    builder
        .setSASLAuthenticationEnabled(true)
        .setSendPresence(false)
        .setSecurityMode(SecurityMode.ENABLE)
        // .addSASLAuthenticationType("PLAIN");
        // .addSASLAuthenticationType("DIGEST-MD5")
        .addReceiver("foo@host.de");
    builder.build().send(Message.create("Hallo Welt", null, MessageType.INFO));
  }
}
