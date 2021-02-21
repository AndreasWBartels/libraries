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
package net.anwiba.crypto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.bouncycastle.crypto.params.KeyParameter;
import org.junit.jupiter.api.Test;

import net.anwiba.commons.crypto.CryptoUtilities;

public class CryptoUtilitiesTest {

  final static String text = "Dieser Text ist ein Test\nDer kann etwas länger\nsein. Vermutlich aber nicht Lang genug."; //$NON-NLS-1$
  final byte[] key = "g3r96zt2".getBytes(); //$NON-NLS-1$

  @Test
  public void performCoding() throws Exception {
    final KeyParameter keyParameter = new KeyParameter(this.key);
    final byte[] encryptText = CryptoUtilities.performEncoding(keyParameter, CryptoUtilitiesTest.text.getBytes());
    final byte[] decryptText = CryptoUtilities.performDecoding(keyParameter, encryptText);
    assertThat(new String(decryptText), equalTo(CryptoUtilitiesTest.text));
  }

  @Test
  public void getCodedBytes() throws Exception {
    final byte[] encryptText = CryptoUtilities.getEncodedBytes(this.key, CryptoUtilitiesTest.text.getBytes());
    final byte[] decryptText = CryptoUtilities.getDecodedBytes(this.key, encryptText);
    assertThat(new String(decryptText), equalTo(CryptoUtilitiesTest.text));
  }
}