/*
 * #%L
 * anwiba commons swing
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
package net.anwiba.commons.swing.component.search.text;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.model.IChangeableListListener;
import net.anwiba.commons.model.IObjectListModel;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectListModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.component.search.ISearchEngine;
import net.anwiba.commons.utilities.string.IStringPart;
import net.anwiba.commons.utilities.string.StringUtilities;

public final class DocumentSearchEngine implements ISearchEngine<String, IStringPart> {

  private final ObjectModel<IStringPart> resultCursor = new ObjectModel<>();
  private final ObjectListModel<IStringPart> resultsModel = new ObjectListModel<>();
  private final Document document;
  private String condition;
  private int currentResultIndex;

  public DocumentSearchEngine(final Document document) {
    this.document = document;
    this.document.addDocumentListener(new DocumentListener() {

      @Override
      public void removeUpdate(final DocumentEvent e) {
        update();
      }

      @Override
      public void insertUpdate(final DocumentEvent e) {
        update();
      }

      @Override
      public void changedUpdate(final DocumentEvent e) {
        update();
      }
    });
    this.resultsModel.addListModelListener(new IChangeableListListener<IStringPart>() {

      @Override
      public void objectsRemoved(final Iterable<Integer> indeces, final Iterable<IStringPart> object) {
        updateCursor();
      }

      @Override
      public void objectsAdded(final Iterable<Integer> indeces, final Iterable<IStringPart> object) {
        updateCursor();
      }

      @Override
      public void objectsUpdated(
          final Iterable<Integer> indeces,
          final Iterable<IStringPart> oldObjects,
          final Iterable<IStringPart> newObjects) {
        updateCursor();
      }

      @Override
      public void objectsChanged(final Iterable<IStringPart> oldObjects, final Iterable<IStringPart> newObjects) {
        updateCursor();
      }
    });
  }

  protected void updateCursor() {
    this.currentResultIndex = this.resultsModel.isEmpty() ? -1 : 0;
    this.resultCursor.set(this.currentResultIndex == -1 ? null : this.resultsModel.get(this.currentResultIndex));
  }

  @Override
  public boolean hasNext() {
    return this.currentResultIndex != -1 && this.currentResultIndex + 1 < this.resultsModel.size();
  }

  @Override
  public boolean hasPrevious() {
    return this.currentResultIndex != -1 && this.currentResultIndex > 0;
  }

  @Override
  public void next() {
    if (!hasNext()) {
      return;
    }
    this.currentResultIndex++;
    this.resultCursor.set(this.resultsModel.get(this.currentResultIndex));
  }

  @Override
  public void previous() {
    if (!hasPrevious()) {
      return;
    }
    this.currentResultIndex--;
    this.resultCursor.set(this.resultsModel.get(this.currentResultIndex));
  }

  @Override
  public void search(final String condition) {
    if (ObjectUtilities.equals(condition, this.condition)) {
      return;
    }
    this.condition = condition;
    update();
  }

  private void update() {
    try {
      if (this.condition == null) {
        getSearchResultsModel().removeAll();
        return;
      }
      this.document.getText(0, this.document.getLength());
      final String text = this.document.getText(0, this.document.getLength());
      this.resultsModel.set(StringUtilities.getStringPositions(text, this.condition));
      this.currentResultIndex = this.resultsModel.isEmpty() ? -1 : 0;
    } catch (final BadLocationException exception) {
      throw new RuntimeException(exception);
    }
  }

  @Override
  public IObjectModel<IStringPart> getResultCursorModel() {
    return this.resultCursor;
  }

  @Override
  public boolean hasResult() {
    return this.resultCursor.get() != null;
  }

  @Override
  public String getCondition() {
    return this.condition;
  }

  @Override
  public IObjectListModel<IStringPart> getSearchResultsModel() {
    return this.resultsModel;
  }

  @Override
  public void reset() {
    this.resultsModel.removeAll();
  }
}