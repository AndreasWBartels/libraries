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
package net.anwiba.commons.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

public class CryptoUtilities {

  //  private static PrivateKey createPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
  //    final BigInteger modulus = new BigInteger(
  //        "101282528254465191245638235769421195098729680757155615353885580874028187206525893479812787906405237912656286156489972110167314352136262695727797291542151015612802830990266748780701689991962277384074988365579264931643722233013355068470357119971639618441805775179998369022402892028145409354611203812301941721061"); //$NON-NLS-1$
  //    final BigInteger privateExponent = new BigInteger(
  //        "31682761062373787108455215702716082834415020296968846153623270724910384453377288283406960417309516493696179600747683266407374636207722683752926930953593199987614309909943376571602227967104627199892436014762702859017776868410864126224330708881359356027478569186899516844841863031027547660474737295420102908125"); //$NON-NLS-1$
  //    return createPrivateKey(modulus, privateExponent);
  //  }

  //  private static PrivateKey createPrivateKey(final BigInteger modulus, final BigInteger privateExponent)
  //      throws NoSuchAlgorithmException,
  //      InvalidKeySpecException {
  //    final KeyFactory keyFactory = KeyFactory.getInstance("RSA"); //$NON-NLS-1$
  //    final RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(modulus, privateExponent);
  //    return keyFactory.generatePrivate(privateKeySpec);
  //  }

  public static String sign(final String string) throws CodingException {
    try {
      return Base64.toBase64String(sign(string.getBytes("UTF-8"))); //$NON-NLS-1$
    } catch (final UnsupportedEncodingException exception) {
      throw new CodingException(exception.getMessage(), exception);
    }
  }

  public static byte[] sign(final byte[] data) throws CodingException {
    try {
      Security.addProvider(new BouncyCastleProvider());
      final MessageDigest md = MessageDigest.getInstance("SHA-256", "BC"); //$NON-NLS-1$ //$NON-NLS-2$
      md.update(data); // Change this to "UTF-16" if needed
      return md.digest();
      //      final Signature signer = Signature.getInstance("SHA-1", "BC"); //$NON-NLS-1$ //$NON-NLS-2$
      //      signer.initSign(createPrivateKey());
      //      signer.update(data);
      //      return signer.sign();
    } catch (final NoSuchAlgorithmException | NoSuchProviderException exception) {
      throw new CodingException(exception.getMessage(), exception);
    }
  }

  public static byte[] getEncodedBytes(final byte[] key, final byte[] buffer) throws CodingException {
    if (key == null) {
      return buffer;
    }
    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);) {
      final byte[] encoding = performEncoding(new KeyParameter(key), inputStream);
      return Base64.encode(encoding);
    } catch (final DataLengthException exception) {
      throw new CodingException(exception.getMessage(), exception);
    } catch (final IllegalStateException exception) {
      throw new CodingException(exception.getMessage(), exception);
    } catch (final InvalidCipherTextException exception) {
      throw new CodingException(exception.getMessage(), exception);
    } catch (final IOException exception) {
      throw new CodingException(exception.getMessage(), exception);
    }
  }

  public static byte[] getDecodedBytes(final byte[] key, final byte[] array) throws CodingException {
    if (key == null) {
      return array;
    }
    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.decode(array));) {
      return performDecoding(new KeyParameter(key), inputStream);
    } catch (final DataLengthException exception) {
      throw new CodingException(exception.getMessage(), exception);
    } catch (final InvalidCipherTextException exception) {
      throw new CodingException(exception.getMessage(), exception);
    } catch (final IllegalStateException exception) {
      throw new CodingException(exception.getMessage(), exception);
    } catch (final IOException exception) {
      throw new CodingException(exception.getMessage(), exception);
    }
  }

  public static byte[] performDecoding(final KeyParameter keyParameter, final byte[] array)
      throws DataLengthException,
      IllegalStateException,
      InvalidCipherTextException,
      IOException {
    return performDecoding(keyParameter, new ByteArrayInputStream(array));
  }

  public static byte[] performDecoding(final KeyParameter keyParameter, final InputStream inputStream)
      throws InvalidCipherTextException,
      DataLengthException,
      IllegalStateException,
      IOException {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
      performCoding(inputStream, outputStream, createDecodingCiper(keyParameter));
      return outputStream.toByteArray();
    }
  }

  public static byte[] performEncoding(final KeyParameter keyParameter, final byte[] array)
      throws DataLengthException,
      IllegalStateException,
      InvalidCipherTextException,
      IOException {
    return performEncoding(keyParameter, new ByteArrayInputStream(array));
  }

  private static byte[] performEncoding(final KeyParameter keyParameter, final InputStream inputStream)
      throws IOException,
      InvalidCipherTextException {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
      performCoding(inputStream, outputStream, createEncodingChiper(keyParameter));
      return outputStream.toByteArray();
    }
  }

  private static PaddedBufferedBlockCipher createDecodingCiper(final KeyParameter keyParameter) {
    return createCipher(keyParameter, false);
  }

  private static PaddedBufferedBlockCipher createEncodingChiper(final KeyParameter keyParameter) {
    return createCipher(keyParameter, true);
  }

  private static PaddedBufferedBlockCipher createCipher(final KeyParameter keyParameter, final boolean forEncryption) {
    final PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new DESEngine());
    cipher.init(forEncryption, keyParameter);
    return cipher;
  }

  private static void performCoding(
      final InputStream inputStream,
      final OutputStream outputStream,
      final PaddedBufferedBlockCipher cipher) throws IOException, InvalidCipherTextException {
    final byte[] input = new byte[32];
    final byte[] output = new byte[cipher.getOutputSize(input.length)];
    int length;
    while ((length = inputStream.read(input, 0, input.length)) > 0) {
      final int outputLength = cipher.processBytes(input, 0, length, output, 0);
      outputStream.write(output, 0, outputLength);
    }
    final int outputLength = cipher.doFinal(output, 0);
    outputStream.write(output, 0, outputLength);
  }
}