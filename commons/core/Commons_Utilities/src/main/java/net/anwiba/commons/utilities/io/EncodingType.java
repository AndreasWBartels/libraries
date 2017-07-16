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
  // https://www.clicketyclick.dk/databases/xbase/format/dbf.html#DBF_STRUCT
  // http://webhelp.esri.com/arcpad/8.0/referenceguide/index.htm#locales/task_code.htm
  // https://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html
  IBM_437((byte) 0x01, "IBM437", "Cp437"), //$NON-NLS-1$
  IBM_850((byte) 0x02, "IBM850", "Cp850"), //$NON-NLS-1$
  WINDOWS_1252((byte) 0x03, "windows-1252", "Cp1252"), //$NON-NLS-1$
  DANISH((byte) 0x08, "IBM865", "Cp865"), //$NON-NLS-1$
  IBM_775((byte) 0x09, "IBM775", "Cp775"), //$NON-NLS-1$
  DUTCH((byte) 0x0A, "IBM850", "Cp850"), //$NON-NLS-1$
  FINNISH_LEAGACY((byte) 0x0B, "IBM437", "Cp437"), //$NON-NLS-1$
  FRENCH_LEAGACY((byte) 0x0D, "IBM437", "Cp437"), //$NON-NLS-1$
  FRENCH((byte) 0x0E, "IBM850", "Cp850"), //$NON-NLS-1$
  GERMAN_LEAGACY((byte) 0x0F, "IBM437", "Cp437"), //$NON-NLS-1$
  GERMAN((byte) 0x10, "IBM850", "Cp850"), //$NON-NLS-1$
  ITALIAN_LEAGACY((byte) 0x11, "IBM437", "Cp437"), //$NON-NLS-1$
  ITALIAN((byte) 0x12, "IBM850", "Cp850"), //$NON-NLS-1$
  JAPANESE_SHIFT_JIS((byte) 0x13, "IBM932", "Cp932"), //$NON-NLS-1$
  SPANISH((byte) 0x14, "IBM850", "Cp850"), //$NON-NLS-1$
  SWEDISH_LEAGACY((byte) 0x15, "IBM437", "Cp437"), //$NON-NLS-1$
  SWEDISH((byte) 0x16, "IBM850", "Cp850"), //$NON-NLS-1$
  NORWEGIAN((byte) 0x17, "IBM865", "Cp865"), //$NON-NLS-1$
  SPANISH_LEAGACY((byte) 0x18, "IBM437", "Cp437"), //$NON-NLS-1$
  ENGLISH_LEAGACY_BRITAIN((byte) 0x19, "IBM437", "Cp437"), //$NON-NLS-1$
  ENGLISH_BRITAIN((byte) 0x1A, "IBM850", "Cp850"), //$NON-NLS-1$
  ENGLISH_LEAGACY_US((byte) 0x1B, "IBM437", "Cp437"), //$NON-NLS-1$
  FRENCH_CANADA((byte) 0x1C, "IBM863", "Cp863"), //$NON-NLS-1$
  FRENCH_X((byte) 0x1D, "IBM850", "Cp850"), //$NON-NLS-1$
  CZECH((byte) 0x1F, "IBM852", "Cp852"), //$NON-NLS-1$
  HUNGARIAN((byte) 0x22, "IBM852", "Cp852"), //$NON-NLS-1$
  POLISH((byte) 0x23, "IBM852", "Cp852"), //$NON-NLS-1$
  PORTUGESE((byte) 0x24, "IBM860", "Cp860"), //$NON-NLS-1$
  PORTUGESE_LEAGACY((byte) 0x25, "IBM850", "Cp850"), //$NON-NLS-1$
  RUSSIAN((byte) 0x26, "IBM866", "Cp866"), //$NON-NLS-1$
  ENGLISH_US((byte) 0x37, "IBM850", "Cp850"), //$NON-NLS-1$
  ROMANIAN((byte) 0x40, "IBM852", "Cp852"), //$NON-NLS-1$
  CHINESE_GBK_PRC((byte) 0x4D, "IBM936", "Cp936"), //$NON-NLS-1$
  KOREAN_ANSI((byte) 0x4E, "IBM949", "Cp949"), //$NON-NLS-1$
  CHINESE_BIG_5_TAIWAN((byte) 0x4F, "IBM950", "Cp950"), //$NON-NLS-1$
  THAI_ANSI((byte) 0x50, "x-IBM874", "Cp874"), //$NON-NLS-1$
  ANSI((byte) 0x57, "windows-1252", "Cp1252"), //$NON-NLS-1$
  WESTERN_EUROPEAN_ANSI((byte) 0x58, "windows-1252", "Cp1252"), //$NON-NLS-1$
  SPANISH_ANSI((byte) 0x59, "windows-1252", "Cp1252"), //$NON-NLS-1$
  IBM_852((byte) 0x64, "IBM852", "Cp852"), //$NON-NLS-1$
  IBM_865((byte) 0x65, "IBM865", "Cp865"), //$NON-NLS-1$
  IBM_866((byte) 0x66, "IBM866", "Cp866"), //$NON-NLS-1$
  IBM_861((byte) 0x67, "IBM861", "Cp861"), //$NON-NLS-1$
  IBM_737((byte) 0x6A, "x-IBM737", "Cp737"), //$NON-NLS-1$
  IBM_857((byte) 0x6B, "IBM857", "Cp857"), //$NON-NLS-1$
  IBM_863((byte) 0x6C, "IBM863", "Cp863"), //$NON-NLS-1$
  TAIWAN_BIG_5((byte) 0x78, "IBM950", "Cp950"), //$NON-NLS-1$
  HANGUL_WANSUNG((byte) 0x79, "IBM949", "Cp949"), //$NON-NLS-1$
  PRC_GBK((byte) 0x7A, "IBM936", "Cp936"), //$NON-NLS-1$
  X_JAPANESE_SHIFT_JIS((byte) 0x7B, "IBM932", "Cp932"), //$NON-NLS-1$
  THAI_WINDOWS_MS_DOS((byte) 0x7C, "IBM874", "Cp874"), //$NON-NLS-1$
  GREEK((byte) 0x86, "IBM737", "Cp737"), //$NON-NLS-1$
  SLOVENIAN((byte) 0x87, "IBM852", "Cp852"), //$NON-NLS-1$
  TURKISH((byte) 0x88, "IBM857", "Cp857"), //$NON-NLS-1$
  WINDOWS_1250((byte) 0xc8, "windows-1250", "Cp1250"), //$NON-NLS-1$
  WINDOWS_1251((byte) 0xC9, "windows-1251", "Cp1251"), //$NON-NLS-1$
  WINDOWS_1253((byte) 0xCB, "windows-1253", "Cp1253"), //$NON-NLS-1$
  WINDOWS_1254((byte) 0xCA, "windows-1254", "Cp1254"), //$NON-NLS-1$
  WINDOWS_1257((byte) 0xCC, "windows-1257", "Cp1257"), //$NON-NLS-1$

  UTF_8((byte) 0x00, "UTF-8", "UTF8"), //$NON-NLS-1$
  IBM_858((byte) 0x00, "IBM00858", "Cp858"), //$NON-NLS-1$
  IBM_855((byte) 0x00, "IBM855", "Cp855"), //$NON-NLS-1$
  IBM_862((byte) 0x00, "IBM862", "Cp862"), //$NON-NLS-1$
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
  UNICODE_BIG((byte) 0x00, "Not available", "UnicodeBig"), //$NON-NLS-1$
  EBCDIC((byte) 0x00, "IBM1047", "Cp1047");

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
      if (type.code != (byte) 0x00 && type.code == code) {
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
