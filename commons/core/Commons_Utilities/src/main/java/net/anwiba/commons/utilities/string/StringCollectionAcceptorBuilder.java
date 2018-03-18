package net.anwiba.commons.utilities.string;

import net.anwiba.commons.lang.functional.CollectionAcceptorBuilder;
import net.anwiba.commons.lang.functional.IAcceptor;

public class StringCollectionAcceptorBuilder extends CollectionAcceptorBuilder<String> {

  private boolean ignoreCase = false;

  public StringCollectionAcceptorBuilder ignoreCase() {
    this.ignoreCase = true;
    return this;
  }

  public StringCollectionAcceptorBuilder caseSensitive() {
    this.ignoreCase = false;
    return this;
  }

  public StringCollectionAcceptorBuilder accept(final String... values) {
    return accept(
        (IAcceptor<String>) value -> StringCollectionAcceptorBuilder.this.ignoreCase
            ? StringUtilities.containsIgnoreCase(value, values)
            : StringUtilities.contains(value, values));
  }

  @Override
  public StringCollectionAcceptorBuilder accept(final IAcceptor<String> acceptor) {
    super.accept(acceptor);
    return this;
  }

  @Override
  public StringCollectionAcceptorBuilder otherwise(final IAcceptor<String> otherwise) {
    super.otherwise(otherwise);
    return this;
  }
}
