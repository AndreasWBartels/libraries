// Copyright (c) 2016 by Andreas W. Bartels (bartels@anwiba.de)

package net.anwiba.commons.utilities.string;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.utilities.string.IStringSubstituter;
import net.anwiba.commons.utilities.string.StringUtilities;

public class PrefixToSystemPropertySubstituter implements IStringSubstituter {

  private final List<IConverter<String, String, RuntimeException>> converters = new LinkedList<>();

  public PrefixToSystemPropertySubstituter(final List<String> propertyNames) {
    new LinkedHashSet<>(propertyNames).forEach(property -> this.converters.add(string -> {
      final String value = System.getProperty(property);
      if (StringUtilities.isNullOrTrimmedEmpty(value) || !string.startsWith(value)) {
        return string;
      }
      return "$SYSTEM{" + property + "}" + string.substring(value.length(), string.length()); //$NON-NLS-1$ //$NON-NLS-2$
    }));
  }

  @Override
  public String substitute(final String string) {
    String value = string;
    for (final IConverter<String, String, RuntimeException> converter : this.converters) {
      value = converter.convert(value);
    }
    return value;
  }
}