/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2016 Andreas W. Bartels (bartels@anwiba.de)
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

import java.io.InputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.xml.sax.SAXException;

import net.anwiba.commons.mail.schema.account.Account;
import net.anwiba.commons.xml.jaxb.JaxbContextBuilder;
import net.anwiba.commons.xml.jaxb.UnmarshallerFactory;

public final class AccountReader {

  @SuppressWarnings("unchecked")
  public Account read(final InputStream inputStream) throws JAXBException, SAXException {
    final Unmarshaller unmarshaller = createUnmarshaller();
    final Object object = unmarshaller.unmarshal(inputStream);
    return ((JAXBElement<Account>) object).getValue();
  }

  private Unmarshaller createUnmarshaller() throws JAXBException, SAXException {
    final JaxbContextBuilder builder = new JaxbContextBuilder().add(
        net.anwiba.commons.mail.schema.account.ObjectFactory.class,
        "/net/anwiba/commons/mail/schema/account/account.xsd"); //$NON-NLS-1$
    return new UnmarshallerFactory().create(builder.build());
  }
}
