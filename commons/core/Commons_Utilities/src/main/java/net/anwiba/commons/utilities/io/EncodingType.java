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
package net.anwiba.commons.utilities.io;

@SuppressWarnings("nls")
public enum EncodingType {
  UTF_8((byte) 0x00, "UTF-8", "UTF8"), //$NON-NLS-1$
  IBM_00858((byte) 0x00, "IBM00858", "Cp858"), //$NON-NLS-1$
  IBM_437((byte) 0x01, "IBM437", "Cp437"), //$NON-NLS-1$
  IBM_775((byte) 0x09, "IBM775", "Cp775"), //$NON-NLS-1$
  IBM_850((byte) 0x02, "IBM850", "Cp850"), //$NON-NLS-1$
  IBM_852((byte) 0x00, "IBM852", "Cp852"), //$NON-NLS-1$
  IBM_855((byte) 0x00, "IBM855", "Cp855"), //$NON-NLS-1$
  IBM_857((byte) 0x00, "IBM857", "Cp857"), //$NON-NLS-1$
  IBM_862((byte) 0x00, "IBM862", "Cp862"), //$NON-NLS-1$
  IBM_865((byte) 0x65, "IBM865", "Cp865"), //$NON-NLS-1$
  IBM_866((byte) 0x66, "IBM866", "Cp866"), //$NON-NLS-1$
  ISO_8859_1((byte) 0x00, "ISO-8859-1", "ISO8859_1"), //$NON-NLS-1$
  ISO_8859_2((byte) 0x00, "ISO-8859-2", "ISO8859_2"), //$NON-NLS-1$
  ISO_8859_4((byte) 0x00, "ISO-8859-4", "ISO8859_4"), //$NON-NLS-1$
  ISO_8859_5((byte) 0x00, "ISO-8859-5", "ISO8859_5"), //$NON-NLS-1$
  ISO_8859_7((byte) 0x00, "ISO-8859-7", "ISO8859_7"), //$NON-NLS-1$
  ISO_8859_9((byte) 0x00, "ISO-8859-9", "ISO8859_9"), //$NON-NLS-1$
  ISO_8859_13((byte) 0x00, "ISO-8859-13", "ISO8859_13"), //$NON-NLS-1$
  ISO_8859_15((byte) 0x00, "ISO-8859-15", "ISO8859_15"), //$NON-NLS-1$
  KOI_8_R((byte) 0x00, "KOI8-R", "KOI8_R"), //$NON-NLS-1$
  KOI_8_U((byte) 0x00, "KOI8-U", "KOI8_U"), //$NON-NLS-1$
  US_ASCII((byte) 0x00, "US-ASCII", "ASCII"), //$NON-NLS-1$
  UTF_16((byte) 0x00, "UTF-16", "UTF-16"), //$NON-NLS-1$
  UTF_16BE((byte) 0x00, "UTF-16BE", "UnicodeBigUnmarked"), //$NON-NLS-1$
  UTF_16LE((byte) 0x00, "UTF-16LE", "UnicodeLittleUnmarked"), //$NON-NLS-1$
  UTF_32((byte) 0x00, "UTF-32", "UTF_32"), //$NON-NLS-1$
  UTF_32BE((byte) 0x00, "UTF-32BE", "UTF_32BE"), //$NON-NLS-1$
  UTF_32LE((byte) 0x00, "UTF-32LE", "UTF_32LE"), //$NON-NLS-1$
  X_UTF_16LE_BOM((byte) 0x00, "x-UTF-16LE-BOM", "UnicodeLittle"), //$NON-NLS-1$
  X_UTF_32BE_BOM((byte) 0x00, "x-UTF-32BE-BOM", "UTF_32BE_BOM"), //$NON-NLS-1$
  X_UTF_32LE_BOM((byte) 0x00, "x-UTF-32LE-BOM", "UTF_32LE_BOM"), //$NON-NLS-1$
  WINDOWS_1250((byte) 0xc8, "windows-1250", "Cp1250"), //$NON-NLS-1$
  WINDOWS_1251((byte) 0x00, "windows-1251", "Cp1251"), //$NON-NLS-1$
  WINDOWS_1252((byte) 0x03, "windows-1252", "Cp1252"), //$NON-NLS-1$
  WINDOWS_1253((byte) 0x00, "windows-1253", "Cp1253"), //$NON-NLS-1$
  WINDOWS_1254((byte) 0x00, "windows-1254", "Cp1254"), //$NON-NLS-1$
  WINDOWS_1257((byte) 0x00, "windows-1257", "Cp1257"), //$NON-NLS-1$
  UNICODE_BIG((byte) 0x00, "Not available", "UnicodeBig"), //$NON-NLS-1$
  X_IBM_737((byte) 0x00, "x-IBM737", "Cp737"), //$NON-NLS-1$
  X_IBM_874((byte) 0x00, "x-IBM874", "Cp874"); //$NON-NLS-1$

  private final String key;
  private final byte code;
  private final String keyLangApi;

  private EncodingType(final byte code, final String key, final String keyLangApi) {
    this.code = code;
    this.key = key;
    this.keyLangApi = keyLangApi;
  }

  public byte getCode() {
    return this.code;
  }

  public String getKeyLangApi() {
    return this.keyLangApi;
  }

  public String getKeyNioApi() {
    return this.key;
  }

  public static EncodingType getByCode(final byte code) {
    for (final EncodingType type : values()) {
      if (type.code != (byte) 0x00 && type.code == java.lang.Character.toUpperCase(code)) {
        return type;
      }
    }
    return null;
  }

  public static EncodingType getByName(final String name) {
    for (final EncodingType type : values()) {
      if (type.key.equalsIgnoreCase(name) || type.keyLangApi.equalsIgnoreCase(name)) {
        return type;
      }
    }
    return null;
  }
}