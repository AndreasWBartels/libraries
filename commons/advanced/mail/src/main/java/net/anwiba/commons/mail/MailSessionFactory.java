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

import jakarta.mail.Session;
import net.anwiba.commons.mail.schema.account.AbstractAuthentification;
import net.anwiba.commons.mail.schema.account.Authorization;
import net.anwiba.commons.mail.schema.account.Encryption;
import net.anwiba.commons.mail.schema.account.LoginAuthentification;
import net.anwiba.commons.mail.schema.account.NoneAuthentification;
import net.anwiba.commons.mail.schema.account.PlainAuthentification;
import net.anwiba.commons.mail.schema.account.Server;

public final class MailSessionFactory {

  @SuppressWarnings("nls")
  public Session create(final Server server) {
    final Properties properties = new Properties();
    String protocol = protocol(server.getEncryption());
    properties.setProperty("mail.transport.protocol", protocol);
    properties.setProperty("mail." + protocol + ".host", server.getHost());
    properties.setProperty("mail." + protocol + ".port", String.valueOf(server.getPort()));

    Encryption encryptionStrategy = server.getEncryption();
    switch (encryptionStrategy) {
      case NONE: {
        properties.setProperty("mail." + protocol + ".ssl.trust", "*");
        properties.setProperty("mail." + protocol + ".ssl.checkserveridentity", "false");
        properties.setProperty("mail." + protocol + ".starttls.enable", "true");
        properties.setProperty("mail." + protocol + ".starttls.required", "false");
        break;
      }
      case TLS: {
        properties
            .setProperty("mail." + protocol + ".socketFactory.class", javax.net.ssl.SSLSocketFactory.class.getName());
        properties.setProperty("mail." + protocol + ".socketFactory.fallback", "false");
        properties.setProperty("mail." + protocol + ".socketFactory.port", String.valueOf(server.getPort()));
        properties.setProperty("mail." + protocol + ".ssl.checkserveridentity", "true");
        properties.setProperty("mail." + protocol + ".starttls.enable", "false");
        properties.setProperty("mail." + protocol + ".starttls.required", "false");
        break;
      }
      case STARTTLS: {
        properties.setProperty("mail." + protocol + ".ssl.trust", "*");
        properties.setProperty("mail." + protocol + ".ssl.checkserveridentity", "false");
        properties.setProperty("mail." + protocol + ".starttls.enable", "true");
        properties.setProperty("mail." + protocol + ".starttls.required", "true");
        break;
      }
    }

    Authorization authorization = server.getAuthorization();
    AbstractAuthentification authentification = authorization.getAuthentification().getValue();

    if (authentification instanceof PlainAuthentification) {
      properties.setProperty("mail." + protocol + ".sasl.enable", "true");
      properties.setProperty("mail." + protocol + ".auth", "true");
      properties.setProperty("mail." + protocol + ".auth.login.disable", "true");
      properties.setProperty("mail." + protocol + ".auth.plain.disable", "false");
      properties.setProperty("mail." + protocol + ".auth.digest-md5.disable", "true");
      properties.setProperty("mail." + protocol + ".auth.ntlm.disable", "true");
    } else if (authentification instanceof LoginAuthentification) {
      properties.setProperty("mail." + protocol + ".sasl.enable", "true");
      properties.setProperty("mail." + protocol + ".auth", "true");
      properties.setProperty("mail." + protocol + ".auth.login.disable", "false");
      properties.setProperty("mail." + protocol + ".auth.plain.disable", "true");
      properties.setProperty("mail." + protocol + ".auth.digest-md5.disable", "true");
      properties.setProperty("mail." + protocol + ".auth.ntlm.disable", "true");
    } else if (authentification instanceof NoneAuthentification) {
      properties.setProperty("mail." + protocol + ".sasl.enable", "false");
      properties.setProperty("mail." + protocol + ".auth", "false");
      properties.setProperty("mail." + protocol + ".auth.login.disable", "true");
      properties.setProperty("mail." + protocol + ".auth.plain.disable", "true");
      properties.setProperty("mail." + protocol + ".auth.digest-md5.disable", "true");
      properties.setProperty("mail." + protocol + ".auth.ntlm.disable", "true");
    } else {
      properties.setProperty("mail." + protocol + ".sasl.enable", "true");
      properties.setProperty("mail." + protocol + ".auth", "true");
      properties.setProperty("mail." + protocol + ".auth.login.disable", "false");
      properties.setProperty("mail." + protocol + ".auth.plain.disable", "false");
      properties.setProperty("mail." + protocol + ".auth.digest-md5.disable", "false");
      properties.setProperty("mail." + protocol + ".auth.ntlm.disable", "false");
    }

    // switch (authorizationStrategy) {
    // case DIGEST_MD5: {
    // properties.setProperty("mail." + protocol + ".sasl.enable", "true");
    // properties.setProperty("mail." + protocol + ".auth", "true");
    // properties.setProperty("mail." + protocol + ".auth.login.disable", "true");
    // properties.setProperty("mail." + protocol + ".auth.plain.disable", "true");
    // properties.setProperty("mail." + protocol + ".auth.digest-md5.disable", "false");
    // properties.setProperty("mail." + protocol + ".auth.ntlm.disable", "true");
    // break;
    // }
    // case NTLM: {
    // properties.setProperty("mail." + protocol + ".sasl.enable", "true");
    // properties.setProperty("mail." + protocol + ".auth", "true");
    // properties.setProperty("mail." + protocol + ".auth.login.disable", "true");
    // properties.setProperty("mail." + protocol + ".auth.plain.disable", "true");
    // properties.setProperty("mail." + protocol + ".auth.digest-md5.disable", "true");
    // properties.setProperty("mail." + protocol + ".auth.ntlm.disable", "false");
    // break;
    // }
    // }
    return Session.getInstance(properties);
  }

  private String protocol(final Encryption encryption) {
    switch (encryption) {
      case NONE:
      case STARTTLS: {
        return "smtp";
      }
      case TLS: {
        return "smtps";
      }
    }
    return "smtp";
  }
}
