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

import java.security.SecureRandom;

import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.DESedeKeyGenerator;
import org.bouncycastle.crypto.params.DESedeParameters;

public class KeyGenerator {

  private final DESedeKeyGenerator keyGenerator;

  private KeyGenerator(final DESedeKeyGenerator keyGenerator) {
    this.keyGenerator = keyGenerator;
  }

  public static KeyGenerator create() {
    final SecureRandom random = new SecureRandom();
    final KeyGenerationParameters parameters = new KeyGenerationParameters(
        random,
        DESedeParameters.DES_EDE_KEY_LENGTH * 8);
    final DESedeKeyGenerator keyGenerator = new DESedeKeyGenerator();
    keyGenerator.init(parameters);
    return new KeyGenerator(keyGenerator);
  }

  public byte[] generateKey() {
    return this.keyGenerator.generateKey();
  }
} 
