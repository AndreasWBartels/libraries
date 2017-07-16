/*
 * #%L
 * *
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.spatial.gps.gpsd;

import java.io.IOException;
import java.util.NoSuchElementException;

import net.anwiba.commons.lang.object.ObjectPair;
import net.anwiba.commons.utilities.io.IClosableIterator;
import net.anwiba.spatial.gps.gpsd.response.Devices;
import net.anwiba.spatial.gps.gpsd.response.Poll;
import net.anwiba.spatial.gps.gpsd.response.Tpv;
import net.anwiba.spatial.gps.gpsd.response.Watch;

public final class GpsDevice implements IGpsDevice {

  private final GpsdFacade gpsdFacade;
  private final String path;

  public GpsDevice(final GpsdFacade gpsdFacade, final String path) {
    this.gpsdFacade = gpsdFacade;
    this.path = path;
  }

  @SuppressWarnings("resource")
  @Override
  public IClosableIterator<IRecord, IOException> records() throws IOException {
    final Connection connection = this.gpsdFacade.connect();
    return new IClosableIterator<IRecord, IOException>() {

      IRecord record = null;

      @Override
      public void close() throws IOException {
        connection.close();
      }

      @Override
      public IRecord next() throws IOException {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        try {
          return this.record;
        } finally {
          this.record = null;
        }
      }

      @Override
      public boolean hasNext() throws IOException {
        final ObjectPair<Devices, Watch> watch = connection.watch(false, GpsDevice.this.path);
        if (!watch.getSecondObject().isEnable()) {
          return false;
        }
        final Poll poll = connection.poll();
        final Tpv[] tpvs = poll.getTpv();
        if (tpvs.length == 0) {
          return false;
        }
        final Tpv tpv = tpvs[0];
        this.record = new Record(tpv.getTime(), tpv.getMode(), tpv.getLat(), tpv.getLon(), tpv.getAlt());
        return true;
      }
    };
  }

  @Override
  public String getPath() {
    return this.path;
  }
}
