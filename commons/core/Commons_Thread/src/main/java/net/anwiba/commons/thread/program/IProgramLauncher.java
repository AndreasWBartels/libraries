/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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
package net.anwiba.commons.thread.program;

import java.io.IOException;
import java.io.InputStream;

import net.anwiba.commons.lang.functional.IConsumer;
import net.anwiba.commons.thread.cancel.ICanceler;

public interface IProgramLauncher {

  IProgramLauncher waitNot();

  IProgramLauncher waitFor();

  IProgramLauncher streamToLogger();

  IProgramLauncher command(String command);

  IProgramLauncher argument(String argument);

  IProgramLauncher errorStreamConsumer(IConsumer<InputStream, IOException> errorStreamConsumer);

  IProgramLauncher inputStreamConsumer(IConsumer<InputStream, IOException> inputStreamConsumer);

  Process launch() throws IOException, InterruptedException;

  Process launch(ICanceler canceler) throws IOException, InterruptedException;

}
