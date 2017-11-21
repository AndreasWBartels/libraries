/*
 * #%L
 * anwiba commons core
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
package net.anwiba.crypto;

public class PasswordCoder implements IPasswordCoder {

  private final byte[] key;

  public PasswordCoder(final byte[] key) {
    this.key = key;
  }

  @Override
  public String decode(final String password) throws CodingException {
    return new String(CryptoUtilities.getDecodedBytes(this.key, password.getBytes()));
  }

  @Override
  public String decode(final IPassword password) throws CodingException {
    if (!password.isEncrypted()) {
      return password.getValue();
    }
    return decode(password.getValue());
  }

  @Override
  public String encode(final String password) throws CodingException {
    final byte[] encodedBytes = CryptoUtilities.getEncodedBytes(this.key, password.getBytes());
    return new String(encodedBytes);
  }
}