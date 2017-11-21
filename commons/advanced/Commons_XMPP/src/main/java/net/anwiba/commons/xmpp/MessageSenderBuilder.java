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

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.ConnectionConfiguration;

public class MessageSenderBuilder {

  private final String host;
  private final String userName;
  private final String password;
  private boolean isSASLAuthenticationEnabled = false;
  private boolean isSendPresence = false;
  private SecurityMode securityMode = SecurityMode.ENABLE;
  private final List<String> saslAuthenticationTypes = new ArrayList<>();
  private final List<String> receivers = new ArrayList<>();
  private String serviceName;
  private int port = 5222;

  public MessageSenderBuilder(final String host, final String userName, final String password) {
    this.host = host;
    this.userName = userName;
    this.password = password;
  }

  public MessageSender build() {
    final ConnectionConfiguration configuration = new ConnectionConfiguration(
        this.host,
        this.port,
        this.serviceName == null ? this.host : this.serviceName);
    configuration.setSASLAuthenticationEnabled(this.isSASLAuthenticationEnabled);
    configuration.setSendPresence(this.isSendPresence);
    configuration.setSecurityMode(this.securityMode.getConfigurationValue());
    return new MessageSender(
        configuration,
        this.userName,
        this.password,
        this.saslAuthenticationTypes,
        this.receivers);
  }

  public void setPort(final int port) {
    this.port = port;
  }

  public void setServiceName(final String serviceName) {
    this.serviceName = serviceName;
  }

  public MessageSenderBuilder setSASLAuthenticationEnabled(final boolean isSASLAuthenticationEnabled) {
    this.isSASLAuthenticationEnabled = isSASLAuthenticationEnabled;
    return this;
  }

  public MessageSenderBuilder setSendPresence(final boolean isSendPresence) {
    this.isSendPresence = isSendPresence;
    return this;
  }

  public MessageSenderBuilder setSecurityMode(final SecurityMode isSecurityMode) {
    this.securityMode = isSecurityMode;
    return this;
  }

  public MessageSenderBuilder addReceiver(final String receiver) {
    this.receivers.add(receiver);
    return this;
  }

  public MessageSenderBuilder addSASLAuthenticationType(final String string) {
    this.saslAuthenticationTypes.add(string);
    return this;
  }
}
