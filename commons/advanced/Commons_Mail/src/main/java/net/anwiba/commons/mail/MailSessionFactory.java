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

import java.util.Properties;

import javax.mail.Session;

import net.anwiba.commons.mail.schema.account.Account;

public final class MailSessionFactory {

  @SuppressWarnings("nls")
  public Session create(final Account account) {
    final Properties properties = System.getProperties();
    properties.setProperty("mail.smtps.host", account.getServer());
    properties.setProperty("mail.smtp.socketFactory.class", javax.net.ssl.SSLSocketFactory.class.getName());
    properties.setProperty("mail.smtp.socketFactory.fallback", "false");
    properties.setProperty("mail.smtp.port", String.valueOf(account.getPort()));
    properties.setProperty("mail.smtp.socketFactory.port", String.valueOf(account.getPort()));
    properties.setProperty("mail.smtps.auth", "true");
    properties.setProperty("mail.smtp.sasl.enable", "true");
    properties.put("mail.smtps.quitwait", "false");
    final Session session = Session.getInstance(properties, null);
    return session;
  }
} 
