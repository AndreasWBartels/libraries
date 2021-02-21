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
package net.anwiba.tools.definition.schema.json.gramma.element;

import java.util.ArrayList;
import java.util.List;

public class JFieldBuilder implements IAnnotatable {

  private final List<JAnnotation> annotations = new ArrayList<>();
  private JValue value;
  private String name = "value"; //$NON-NLS-1$
  private JType type;
  private String fieldComment;
  private String setterComment;
  private String getterComment;

  public Iterable<JAnnotation> annotations() {
    return this.annotations;
  }

  @Override
  public void add(final JAnnotation annotation) {
    this.annotations.add(annotation);
  }

  public JField build() {
    return new JField(this.name,
        this.type,
        this.value == null ? new JValue(null) : this.value,
        this.annotations,
        this.fieldComment,
        this.setterComment,
        this.getterComment);
  }

  public void value(@SuppressWarnings("hiding") final JValue value) {
    this.value = value;
  }

  public void name(@SuppressWarnings("hiding") final String name) {
    this.name = name;
  }

  public void type(@SuppressWarnings("hiding") final JType type) {
    this.type = type;
  }

  private String clean(final String comment) {
    if (comment == null) {
      return null;
    }
    return comment
        .substring(2, comment.length() - 2)
        .trim()
        .lines()
        .map(line -> line.trim())
        .map(line -> line.startsWith("*") ? line.substring(1, line.length())
            .trim() : line)
        .reduce(null, (s, n) -> s == null ? n : s + "\n" + n);
  }

  public void fieldComment(final String comment) {
    this.fieldComment = clean(comment);
  }

  public void setterComment(final String comment) {
    this.setterComment = clean(comment);

  }

  public void getterComment(final String comment) {
    this.getterComment = clean(comment);
  }
}
