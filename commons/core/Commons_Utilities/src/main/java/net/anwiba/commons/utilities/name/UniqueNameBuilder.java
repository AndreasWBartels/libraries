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
package net.anwiba.commons.utilities.name;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.anwiba.commons.lang.functional.IConverter;

// NOT_PUBLISHED
public class UniqueNameBuilder {
  private final Set<String> existingNames = new HashSet<>();
  private final IConverter<String, String, RuntimeException> namePrepareConverter;
  private final int maxLength;

  public UniqueNameBuilder(final IConverter<String, String, RuntimeException> namePrepareTransformer) {
    this(10, namePrepareTransformer, new ArrayList<String>());
  }

  public UniqueNameBuilder(
      final IConverter<String, String, RuntimeException> namePrepareTransformer,
      final Collection<String> attributeBlackList) {
    this(10, namePrepareTransformer, attributeBlackList);
  }

  public UniqueNameBuilder(
      final int maxLength,
      final IConverter<String, String, RuntimeException> nameConverter,
      final Collection<String> attributeBlackList) {
    this.maxLength = maxLength;
    this.namePrepareConverter = nameConverter;
    if (attributeBlackList != null) {
      attributeBlackList.forEach(n -> build(nameConverter.convert(n)));
    }
  }

  public String build(final String name) {
    for (int i = 0;; ++i) {
      final String validName = createValidName(name, i);
      if (!this.existingNames.contains(validName)) {
        this.existingNames.add(validName);
        return validName;
      }
    }
  }

  private String createValidName(final String name, final int index) {
    final String preparedName = this.namePrepareConverter.convert(name);
    if (this.maxLength <= 0 || Integer.MAX_VALUE == this.maxLength) {
      if (index == 0) {
        return preparedName;
      }
      final String append = "_" + index; //$NON-NLS-1$
      return preparedName.replaceAll("_[0-9]*$", "") + append; //$NON-NLS-1$ //$NON-NLS-2$
    }
    if (index == 0) {
      return getCroppedString(preparedName, this.maxLength);
    }
    final String append = "_" + index; //$NON-NLS-1$
    return getCroppedString(preparedName.replaceAll("_[0-9]*$", ""), this.maxLength - append.length()) + append; //$NON-NLS-1$ //$NON-NLS-2$
  }

  private String getCroppedString(final String name, final int maximumLength) {
    return name.substring(0, Math.min(name.length(), maximumLength));
  }
}