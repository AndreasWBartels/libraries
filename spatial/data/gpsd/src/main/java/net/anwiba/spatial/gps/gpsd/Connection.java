/*
 * #%L
 *
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
import java.net.Socket;
import java.util.ArrayList;

import net.anwiba.commons.json.JsonObjectUtilities;
import net.anwiba.commons.lang.object.ObjectPair;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.reference.utilities.IoUtilities;
import net.anwiba.spatial.gps.gpsd.response.Device;
import net.anwiba.spatial.gps.gpsd.response.Devices;
import net.anwiba.spatial.gps.gpsd.response.Error;
import net.anwiba.spatial.gps.gpsd.response.Poll;
import net.anwiba.spatial.gps.gpsd.response.Response;
import net.anwiba.spatial.gps.gpsd.response.Version;
import net.anwiba.spatial.gps.gpsd.response.Watch;

public class Connection implements AutoCloseable {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(Connection.class);
  private final Object mutex = new Object();
  private final Socket socket;
  private final BufferedReader in;
  private final BufferedWriter out;

  public Connection(final Socket socket, final BufferedReader in, final BufferedWriter out) {
    this.socket = socket;
    this.in = in;
    this.out = out;
  }

  public ObjectPair<Devices, Watch> watch(final boolean enable, final String deviceName) throws IOException {
    final Watch watch = new Watch();
    watch.setDevice(deviceName);
    watch.setEnable(enable);
    watch.setJson(enable);
    watch.setJson(enable);
    call("?WATCH=" + JsonObjectUtilities.marshall(watch) + ";"); //$NON-NLS-1$ //$NON-NLS-2$
    final Devices devices = recieve(Devices.class);
    final Watch watchResponse = recieve(Watch.class);
    return new ObjectPair<>(devices, watchResponse);
  }

  public Iterable<Device> devices() throws IOException {
    call("?DEVICES;"); //$NON-NLS-1$
    final Devices devices = recieve(Devices.class);
    final ArrayList<Device> result = new ArrayList<>();
    for (final Device device : devices.getDevices()) {
      result.add(device);
    }
    return result;
  }

  public Version version() throws IOException {
    call("?VERSION;"); //$NON-NLS-1$
    return recieve(Version.class);
  }

  public Poll poll() throws IOException {
    call("?POLL;"); //$NON-NLS-1$
    return recieve(Poll.class);
  }

  @SuppressWarnings({ "unchecked" })
  public <T> T recieve(final Class<T> clazz) throws IOException {
    try {
      final String line = recieve();
      final Response response = JsonObjectUtilities.unmarshall(Response.class, line);
      if (response instanceof Error) {
        throw new IOException(((Error) response).getMessage());
      }
      if (clazz.isInstance(response)) {
        return (T) response;
      }
      throw new IOException("unexpected response '" + response.getClass().getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
    } catch (final Exception exception) {
      throw new IOException(exception);
    }
  }

  private String recieve() throws IOException {
    final StringBuilder builder = new StringBuilder();
    synchronized (this.mutex) {
      builder.append(this.in.readLine());
    }
    final String line = builder.toString();
    logger.log(ILevel.DEBUG, line);
    return line;
  }

  public void call(final String cmd) throws IOException {
    synchronized (this.mutex) {
      this.out.write(cmd + "\n"); //$NON-NLS-1$
      this.out.flush();
    }
  }

  @Override
  public void close() throws IOException {
    IOException ioException = null;
    if (this.out != null) {
      ioException = IoUtilities.close(this.out, ioException);
    }
    if (this.in != null) {
      ioException = IoUtilities.close(this.in, ioException);
    }
    if (this.socket != null) {
      ioException = IoUtilities.close(this.socket, ioException);
    }
    if (ioException == null) {
      return;
    }
    throw ioException;
  }

}
