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

package net.anwiba.commons.utilities.string;

import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;

import net.anwiba.commons.utilities.provider.IContextValueProvider;
import net.anwiba.commons.utilities.time.UserDateTimeUtilities;

public class DateStringResolver extends AbstractStringResolver {

  public DateStringResolver(
      final IStringAppender stringAppender) {
    super(stringAppender,
        value -> value,
        DATE_PATTERN,
        new IContextValueProvider<String, String, RuntimeException>() {

          @Override
          public String getValue(final String name) throws RuntimeException {
            try {
              return UserDateTimeUtilities.now().format(DateTimeFormatter.ofPattern(name));
            } catch (DateTimeException exception) {
              return name;
            }
          }
        });
  }

}
