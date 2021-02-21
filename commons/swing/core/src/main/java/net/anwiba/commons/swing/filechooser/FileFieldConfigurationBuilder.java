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
package net.anwiba.commons.swing.filechooser;

import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Document;

import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.model.IBooleanDistributor;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.swing.action.ConfigurableActionBuilder;
import net.anwiba.commons.swing.action.IActionProcedure;
import net.anwiba.commons.swing.icons.GuiIcons;
import net.anwiba.commons.swing.object.AbstractObjectFieldConfigurationBuilder;
import net.anwiba.commons.swing.object.IActionFactory;
import net.anwiba.commons.swing.object.IToolTipFactory;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.commons.utilities.validation.IValidationResult;
import net.anwiba.commons.utilities.validation.IValidator;

public class FileFieldConfigurationBuilder
    extends
    AbstractObjectFieldConfigurationBuilder<File, FileFieldConfigurationBuilder> {

  private final List<FileFilter> fileFilters = new ArrayList<>();
  private boolean isAllFilterEnabled = true;

  public FileFieldConfigurationBuilder() {
    super(new IValidator<String>() {

      @Override
      public IValidationResult validate(final String value) {
        if (!isValid(value)) {
          return IValidationResult.inValid(FileChooserMessages.InvalidFileName);
        }
        return IValidationResult.valid();
      }
    }, new IConverter<String, File, RuntimeException>() {

      @Override
      public File convert(final String input) {
        if (StringUtilities.isNullOrEmpty(input)) {
          return null;
        }
        return new File(input);
      }
    }, new IConverter<File, String, RuntimeException>() {

      @Override
      public String convert(final File input) {
        if (input == null) {
          return ""; //$NON-NLS-1$
        }
        return input.toString();
      }
    });
    setToolTipFactory(new IToolTipFactory() {

      @Override
      public String create(final IValidationResult validationResult, final String context) throws RuntimeException {
        if (StringUtilities.isNullOrEmpty(context)) {
          return null;
        }
        return context;
      }
    });
  }

  protected static boolean isValid(final String value) {
    if (StringUtilities.isNullOrEmpty(value)) {
      return true;
    }
    return (value.indexOf('\u0000') < 0);
  }

  public FileFieldConfigurationBuilder setFileValidator() {
    setValidator(new IValidator<String>() {

      @Override
      public IValidationResult validate(final String value) {
        if (StringUtilities.isNullOrEmpty(value)) {
          return IValidationResult.valid();
        }
        if (!isValid(value)) {
          return IValidationResult.inValid(FileChooserMessages.InvalidFileName);
        }
        final File file = new File(value);
        if (!file.isFile()) {
          return IValidationResult.inValid(FileChooserMessages.InvalidFileName);
        }
        boolean flag = FileFieldConfigurationBuilder.this.fileFilters.isEmpty();
        for (final FileFilter fileFilter : FileFieldConfigurationBuilder.this.fileFilters) {
          flag |= fileFilter.accept(file);
        }
        if (!flag) {
          return IValidationResult.inValid(FileChooserMessages.InvalidFolderName);
        }
        return IValidationResult.valid();
      }
    });
    return this;
  }

  public FileFieldConfigurationBuilder setFolderValidator() {
    setValidator(value -> {
      if (StringUtilities.isNullOrEmpty(value)) {
        return IValidationResult.valid();
      }
      if (!isValid(value)) {
        return IValidationResult.inValid(FileChooserMessages.InvalidFolderName);
      }
      final File file = new File(value);
      if (!file.isDirectory()) {
        return IValidationResult.inValid(FileChooserMessages.InvalidFolderName);
      }
      boolean flag = FileFieldConfigurationBuilder.this.fileFilters.isEmpty();
      for (final FileFilter fileFilter : FileFieldConfigurationBuilder.this.fileFilters) {
        flag |= fileFilter.accept(file);
      }
      if (!flag) {
        return IValidationResult.inValid(FileChooserMessages.InvalidFolderName);
      }
      return IValidationResult.valid();
    });
    return this;
  }

  public FileFieldConfigurationBuilder addFileSaveChooser(final Window owner) {
    addActionFactory(new IActionFactory<File>() {

      @Override
      public AbstractAction create(
          final IObjectModel<File> context,
          final Document document,
          final IBooleanDistributor enabledDistributor,
          final IBlock<RuntimeException> clearBlock)
          throws RuntimeException {
        final IActionProcedure procedure = new IActionProcedure() {

          @Override
          public void execute(final Component value) throws RuntimeException {
            final ISaveFileChooserConfiguration configuration = new SaveFileChooserConfiguration(
                context.get(),
                FileFieldConfigurationBuilder.this.fileFilters,
                JFileChooser.FILES_ONLY,
                FileFieldConfigurationBuilder.this.fileFilters.size() == 0,
                false);
            final IFileChooserResult result = FileChoosers.show(owner, configuration);
            if (result.getReturnState() != JFileChooser.APPROVE_OPTION) {
              return;
            }
            context.set(result.getSelectedFile());
          }
        };
        return new ConfigurableActionBuilder()
            .setEnabledDistributor(enabledDistributor)
            .setIcon(GuiIcons.OPEN_ICON)
            .setProcedure(procedure)
            .build();
      }
    });
    return this;
  }

  public FileFieldConfigurationBuilder addFolderSaveChooserAction(final Window owner) {
    addActionFactory(new IActionFactory<File>() {

      @Override
      public AbstractAction create(
          final IObjectModel<File> context,
          final Document document,
          final IBooleanDistributor enabledDistributor,
          final IBlock<RuntimeException> clearBlock)
          throws RuntimeException {
        final IActionProcedure procedure = value -> {
          final ISaveFileChooserConfiguration configuration = new SaveFileChooserConfiguration(
              context.get(),
              Arrays.asList(),
              JFileChooser.DIRECTORIES_ONLY,
              false,
              false);
          final IFileChooserResult result = FileChoosers.show(owner, configuration);
          if (result.getReturnState() != JFileChooser.APPROVE_OPTION) {
            return;
          }
          context.set(result.getSelectedFile());
        };
        return new ConfigurableActionBuilder()
            .setEnabledDistributor(enabledDistributor)
            .setIcon(GuiIcons.FOLDER_ICON)
            .setProcedure(procedure)
            .build();
      }
    });
    return this;
  }

  public FileFieldConfigurationBuilder addFileFilter(final FileFilter fileFilter) {
    this.fileFilters.add(fileFilter);
    return this;
  }

  public FileFieldConfigurationBuilder addFileOpenChooserAction(final Window owner) {
    addActionFactory(new IActionFactory<File>() {

      @Override
      public AbstractAction create(
          final IObjectModel<File> context,
          final Document document,
          final IBooleanDistributor enabledDistributor,
          final IBlock<RuntimeException> clearBlock)
          throws RuntimeException {
        final IActionProcedure procedure = value -> {
          final IOpenFileChooserConfiguration configuration = new OpenFileChooserConfiguration(
              context.get(),
              FileFieldConfigurationBuilder.this.fileFilters,
              JFileChooser.FILES_ONLY,
              false);
          final IFileChooserResult result = FileChoosers.show(owner, configuration);
          if (result.getReturnState() != JFileChooser.APPROVE_OPTION) {
            return;
          }
          context.set(result.getSelectedFile());
        };
        return new ConfigurableActionBuilder()
            .setEnabledDistributor(enabledDistributor)
            .setIcon(GuiIcons.OPEN_ICON)
            .setProcedure(procedure)
            .build();
      }
    });
    return this;
  }

  public FileFieldConfigurationBuilder setAllFilterEnabled() {
    this.isAllFilterEnabled = true;
    return this;
  }

  public FileFieldConfigurationBuilder setAllFilterDisabled() {
    this.isAllFilterEnabled = false;
    return this;
  }

  public FileFieldConfigurationBuilder addFolderOpenChooserAction(final Window owner) {
    addActionFactory(new IActionFactory<File>() {

      @Override
      public AbstractAction create(
          final IObjectModel<File> context,
          final Document document,
          final IBooleanDistributor enabledDistributor,
          final IBlock<RuntimeException> clearBlock)
          throws RuntimeException {
        final IActionProcedure procedure = value -> {
          final IOpenFileChooserConfiguration configuration = new OpenFileChooserConfiguration(
              context.get(),
              FileFieldConfigurationBuilder.this.fileFilters,
              JFileChooser.DIRECTORIES_ONLY,
              FileFieldConfigurationBuilder.this.isAllFilterEnabled,
              false,
              c -> null);
          final IFileChooserResult result = FileChoosers.show(owner, configuration);
          if (result.getReturnState() != JFileChooser.APPROVE_OPTION) {
            return;
          }
          context.set(result.getSelectedFile());
        };
        return new ConfigurableActionBuilder()
            .setEnabledDistributor(enabledDistributor)
            .setIcon(GuiIcons.FOLDER_ICON)
            .setProcedure(procedure)
            .build();
      }
    });
    return this;
  }
}