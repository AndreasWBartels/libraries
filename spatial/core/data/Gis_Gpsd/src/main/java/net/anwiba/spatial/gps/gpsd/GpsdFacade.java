/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2017 Andreas W. Bartels
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;

import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.lang.stream.Streams;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.spatial.gps.gpsd.response.Device;
import net.anwiba.spatial.gps.gpsd.response.Poll;
import net.anwiba.spatial.gps.gpsd.response.Tpv;
import net.anwiba.spatial.gps.gpsd.response.Version;

public class GpsdFacade implements IGpsdFacade {

  @SuppressWarnings("nls")
  public static void main(final String args[]) {
    final GpsdFacade client = new GpsdFacade();
    try {

      final Iterable<IGpsDevice> gpsDevices = client.devices();

      final String deviceName = Streams
          .create(IOException.class, gpsDevices)
          .first(d -> "/dev/ttyUSB0".equals(d.getPath()))
          .convert(d -> d.getPath())
          .getOr(
              () -> Optional
                  .of(IOException.class, gpsDevices.iterator())
                  .accept(i -> i.hasNext())
                  .convert(i -> i.next().getPath())
                  .getOrThrow(() -> new IOException("Couldn't find any device")));

      try (Connection connection = client.connect()) {
        for (int i = 0; i < 30; i++) {
          final Poll poll = connection.poll();
          final Tpv[] tpvs = poll.getTpv();
          for (final Tpv tpv : tpvs) {
            final StringBuilder builder = new StringBuilder();
            builder.append("time: ");
            builder.append(tpv.getTime());
            builder.append(", device:");
            builder.append(tpv.getDevice());
            builder.append(", mode: ");
            builder.append(MessageFormat.format("{0,number,#0}", tpv.getMode()));
            builder.append(", latitude: ");
            builder.append(MessageFormat.format("{0,number,##0.0000000}", tpv.getLat()));
            builder.append(", longitude: ");
            builder.append(MessageFormat.format("{0,number,##0.0000000}", tpv.getLon()));
            builder.append(", altitude: ");
            builder.append(MessageFormat.format("{0,number,##0.000}", tpv.getAlt()));
            System.out.println(builder.toString());
            //            final IRecord record = new Record(tpv.getTime(), tpv.getMode(), tpv.getLat(), tpv.getLon(), tpv.getAlt());
          }
          Thread.sleep(500);
        }
        connection.watch(false, deviceName);
        connection.poll();
      }
    } catch (final Exception e) {
      logger.log(ILevel.ERROR, e.getMessage(), e);
      System.exit(1);
    }
  }
  private final static ILogger logger = Logging.getLogger(GpsdFacade.class.getName());

  private final String host;
  private final int port;
  private final int timeout;

  public GpsdFacade() {
    this("localhost", 2947, 10000); //$NON-NLS-1$
  }

  public GpsdFacade(final String host, final int port, final int timeout) {
    this.host = host;
    this.port = port;
    this.timeout = timeout;
  }

  @SuppressWarnings("resource")
  public Connection connect() throws IOException {
    final Socket socket = new Socket(this.host, this.port);
    socket.setSoTimeout(this.timeout);
    final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    final Connection connection = new Connection(socket, in, out);
    connection.recieve(Version.class);
    return connection;
  }

  @Override
  public Iterable<IGpsDevice> devices() throws IOException {
    try (Connection connection = connect()) {
      final ArrayList<IGpsDevice> result = new ArrayList<>();
      for (final Device device : connection.devices()) {
        final String path = device.getPath();
        result.add(new GpsDevice(this, path));
      }
      return result;
    }
  }

  @Override
  public IGpsDevice device(final String path) throws IOException {
    try (Connection connection = connect()) {
      for (final Device device : connection.devices()) {
        if (Objects.equals(path, device.getPath())) {
          return new GpsDevice(this, path);
        }
      }
      throw new NoSuchElementException();
    }
  }

  @Override
  public Version version() throws IOException {
    try (Connection connection = connect()) {
      return connection.version();
    }
  }
}
