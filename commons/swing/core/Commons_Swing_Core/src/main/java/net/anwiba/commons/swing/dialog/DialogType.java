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
package net.anwiba.commons.swing.dialog;

public enum DialogType {
  CLOSE(false) {
    @Override
    public <T> T accept(final IDialogTypeVisitor<T> visitor) {
      return visitor.visitClose();
    }
  },
  CLOSE_DETIALS(false) {
    @Override
    public <T> T accept(final IDialogTypeVisitor<T> visitor) {
      return visitor.visitCloseDetails();
    }
  },
  CANCEL(false) {
    @Override
    public <T> T accept(final IDialogTypeVisitor<T> visitor) {
      return visitor.visitCancel();
    }
  },
  CANCEL_OK(true) {
    @Override
    public <T> T accept(final IDialogTypeVisitor<T> visitor) {
      return visitor.visitCancelOk();
    }
  },
  CANCEL_APPLY_OK(true) {
    @Override
    public <T> T accept(final IDialogTypeVisitor<T> visitor) {
      return visitor.visitCancelApplyOk();
    }
  },
  CANCEL_TRY_OK(true) {
    @Override
    public <T> T accept(final IDialogTypeVisitor<T> visitor) {
      return visitor.visitCancelTryOk();
    }
  },
  YES_NO(true) {
    @Override
    public <T> T accept(final IDialogTypeVisitor<T> visitor) {
      return visitor.visitYesNo();
    }
  },
  NONE(false) {
    @Override
    public <T> T accept(final IDialogTypeVisitor<T> visitor) {
      return visitor.visitNone();
    }
  },
  OK(true) {
    @Override
    public <T> T accept(final IDialogTypeVisitor<T> visitor) {
      return visitor.visitOk();
    }
  };

  private final boolean isConfirmable;

  private DialogType(final boolean isConfirmDialog) {
    this.isConfirmable = isConfirmDialog;
  }

  public abstract <T> T accept(IDialogTypeVisitor<T> visitor);

  public boolean isConfirmable() {
    return this.isConfirmable;
  }
}
