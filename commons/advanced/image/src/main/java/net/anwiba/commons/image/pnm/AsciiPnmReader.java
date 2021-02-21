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
package net.anwiba.commons.image.pnm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.StringTokenizer;

import net.anwiba.commons.reference.utilities.IoUtilities;

public class AsciiPnmReader {

  public Number[][] read(final URL resource) throws IOException {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(resource.openStream()));
      Number values[][] = new Number[][] {};
      String line = null;
      if ((line = reader.readLine()) == null || !line.equals("P2")) { //$NON-NLS-1$
        throw new IOException();
      }
      while ((line = reader.readLine()) != null) {
        if (line.charAt(0) != '#') {
          values = createArray(line);
          break;
        }
      }
      while ((line = reader.readLine()) != null) {
        if (line.charAt(0) != '#') {
          break;
        }
      }
      int i = 0;
      int j = 0;
      while ((line = reader.readLine()) != null && i < values.length) {
        if (line.charAt(0) != '#') {
          values[i][j] = createValue(line);
          j++;
          if (j < values[i].length) {
            continue;
          }
          j = 0;
          i++;
        }
      }
      return values;
    } finally {
      IoUtilities.close(reader);
    }
  }

  private Number createValue(final String line) {
    return Integer.valueOf(line);
  }

  private Number[][] createArray(final String line) {
    final StringTokenizer tokenizer = new StringTokenizer(line);
    final int j = Integer.parseInt(tokenizer.nextToken());
    final int i = Integer.parseInt(tokenizer.nextToken());
    return new Number[i][j];
  }
}
