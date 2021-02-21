// Copyright (c) 2010 by Andreas W. Bartels (bartels@anwiba.de)
package net.anwiba.eclipse.project.dependency.internal.java;

public final class PathFactory {

  public static final char DEFAULT_SEPERATOR = '/';
  private final char seperator;
  private final String basePath;

  public PathFactory(final String... nodes) {
    this(DEFAULT_SEPERATOR, nodes);
  }

  public PathFactory(final char seperator, final String... nodes) {
    this.seperator = seperator;
    if (nodes.length == 0) {
      this.basePath = null;
      return;
    }
    final StringBuilder builder = new StringBuilder();
    builder.append(this.seperator);
    builder.append(createPath(nodes));
    this.basePath = builder.toString();
  }

  public String create(final String... nodes) {
    if (nodes.length == 0) {
      return this.basePath == null ? "" : this.basePath; //$NON-NLS-1$
    }
    final StringBuilder builder = new StringBuilder();
    if (this.basePath != null) {
      builder.append(this.basePath);
      builder.append(this.seperator);
    }
    builder.append(createPath(nodes));
    return builder.toString();
  }

  private String createPath(final String... nodes) {
    final StringBuilder builder = new StringBuilder();
    boolean flag = false;
    for (final String node : nodes) {
      if (flag) {
        builder.append(this.seperator);
      }
      builder.append(cleanSeperatorDuplications(node));
      flag = true;
    }
    return builder.toString();
  }

  private String cleanSeperatorDuplications(final String string) {
    final StringBuilder builder = new StringBuilder();
    boolean flag = true;
    for (final char c : string.toCharArray()) {
      if (c == this.seperator) {
        if (flag) {
          continue;
        }
        flag = true;
        builder.append(c);
        continue;
      }
      flag = false;
      builder.append(c);
    }
    return builder.toString();
  }
}