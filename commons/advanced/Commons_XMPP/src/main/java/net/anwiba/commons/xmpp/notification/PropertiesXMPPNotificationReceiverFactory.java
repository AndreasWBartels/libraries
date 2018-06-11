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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Properties;

import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.exception.InitializeException;
import net.anwiba.commons.message.notification.INotificationReceiver;
import net.anwiba.commons.message.notification.INotificationReceiverFactory;
import net.anwiba.commons.reference.IResourceReferenceFactory;
import net.anwiba.commons.reference.ResourceReferenceFactory;
import net.anwiba.commons.reference.ResourceReferenceUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.commons.xmpp.MessageSenderBuilder;
import net.anwiba.commons.xmpp.SecurityMode;

public class PropertiesXMPPNotificationReceiverFactory implements INotificationReceiverFactory {

  private static final String XMPP_NOTIFIER_PROPERTIES = "xmpp.notifier.properties"; //$NON-NLS-1$
  private static final String PROPERTY_XMPP_RECEIVER = "xmpp.receiver"; //$NON-NLS-1$
  private static final String PROPERTY_XMPP_RECEIVER_NUMBER = "xmpp.receiver.number"; //$NON-NLS-1$
  private static final String PROPERTY_XMPP_PASSWORD = "xmpp.password"; //$NON-NLS-1$
  private static final String PROPERTY_XMPP_USERNAME = "xmpp.user"; //$NON-NLS-1$
  private static final String PROPERTY_XMPP_HOST = "xmpp.host"; //$NON-NLS-1$

  @Override
  public INotificationReceiver create() throws CreationException {
    Properties properties;
    try {
      properties = getXmppProperties();
      final String host = getRequiredProperty(properties, PROPERTY_XMPP_HOST);
      final String userName = getRequiredProperty(properties, PROPERTY_XMPP_USERNAME);
      final String password = getRequiredProperty(properties, PROPERTY_XMPP_PASSWORD);
      final int numberOfReceivers = Integer.valueOf(getRequiredProperty(properties, PROPERTY_XMPP_RECEIVER_NUMBER));
      final MessageSenderBuilder builder = new MessageSenderBuilder(host, userName, password);
      builder.setSASLAuthenticationEnabled(true).setSendPresence(false).setSecurityMode(SecurityMode.ENABLE);
      // .addSASLAuthenticationType("PLAIN");
      // .addSASLAuthenticationType("DIGEST-MD5")
      for (int i = 0; i < numberOfReceivers; i++) {
        final String receiver = getRequiredProperty(
            properties,
            MessageFormat.format("{0}.{1,number,00}", PROPERTY_XMPP_RECEIVER, i)); //$NON-NLS-1$
        builder.addReceiver(receiver);
      }
      return new XMPPNotificationReceiver(builder.build());
    } catch (final IOException | InitializeException exception) {
      throw new CreationException(exception.getMessage(), exception);
    }
  }

  private String getRequiredProperty(final Properties properties, final String propertyName)
      throws InitializeException {
    final String value = properties.getProperty(propertyName);
    if (StringUtilities.isNullOrTrimmedEmpty(value)) {
      throw new InitializeException(MessageFormat.format("missing propertie ''{0}''", propertyName)); //$NON-NLS-1$
    }
    return value;
  }

  private Properties getXmppProperties() throws FileNotFoundException, IOException {
    final String propertiesFileName = System.getProperty(XMPP_NOTIFIER_PROPERTIES);
    final File propertiesFile = getPropertiesFile(propertiesFileName);
    if (propertiesFile == null || !propertiesFile.exists() || !propertiesFile.canRead()) {
      return System.getProperties();
    }
    try (FileReader reader = new FileReader(propertiesFile);) {
      final Properties properties = new Properties();
      properties.load(reader);
      return properties;
    }
  }

  private static File getPropertiesFile(final String propertiesFileName) throws IOException {
    try {
      if (propertiesFileName == null) {
        return null;
      }
      final IResourceReferenceFactory referenceFactory = new ResourceReferenceFactory();
      return ResourceReferenceUtilities.getFile(referenceFactory.create(propertiesFileName));
    } catch (final URISyntaxException | CreationException exception) {
      throw new IOException(exception.getMessage(), exception);
    }
  }

}
