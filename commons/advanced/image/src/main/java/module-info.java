/*
 * #%L
 * anwiba commons
 * %%
 * Copyright (C) 2007 - 2021 Andreas W. Bartels
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
module net.anwiba.commons.image {
  exports net.anwiba.commons.image.renderable;
  exports net.anwiba.commons.image;
  exports net.anwiba.commons.image.apache;
  exports net.anwiba.commons.image.awt;
  exports net.anwiba.commons.image.encoder;
  exports net.anwiba.commons.image.generator;
  exports net.anwiba.commons.image.imagen;
  exports net.anwiba.commons.image.imagen.decoder;
  exports net.anwiba.commons.image.imagen.encoder;
  exports net.anwiba.commons.image.operation;
  exports net.anwiba.commons.image.pnm;
  exports net.anwiba.commons.image.imageio;
  exports net.anwiba.commons.image.codec;

  requires java.desktop;
  requires org.eclipse.imagen.media.codec;
  requires org.eclipse.imagen;
  requires net.anwiba.commons.http;
  requires net.anwiba.commons.lang;
  requires net.anwiba.commons.logging;
  requires net.anwiba.commons.message;
  requires net.anwiba.commons.reference;
  requires net.anwiba.commons.thread;
  requires net.anwiba.commons.utilities;
  requires org.apache.commons.imaging;

}
