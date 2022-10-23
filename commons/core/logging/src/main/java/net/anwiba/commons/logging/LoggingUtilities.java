/*
 * #%L
 * anwiba commons core
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
package net.anwiba.commons.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;

import net.anwiba.commons.logging.java.JavaLogging;

public class LoggingUtilities {

  private static final String CONSOLE = "console"; //$NON-NLS-1$
  private static final String DEFAULT_XML_LOG_CONFIGURATION_FILE = "log4j2.xml"; //$NON-NLS-1$
  private static final String DEFAULT_PROPERTIES_LOG_CONFIGURATION_FILE = "log.properties"; //$NON-NLS-1$
  private static final String NET_ANWIBA_LOGGING_CONFIGURATION = "net.anwiba.logging.configuration"; //$NON-NLS-1$
  private static final String CONSOLE_HANDLER = CONSOLE;
  private static final String NET_ANWIBA = "net.anwiba"; //$NON-NLS-1$
  private static final String FILE = "file"; //$NON-NLS-1$
  private static final String FILE_HANDLER = "FILE"; //$NON-NLS-1$
  private static final String HANDLER = "handler"; //$NON-NLS-1$
  private static final String LEVEL = "level"; //$NON-NLS-1$

  public static void initialize() {
    final String loggingConfigurationFileName = getLoggingConfigurationFileName();
    read(loggingConfigurationFileName);
  }

  private static String getLoggingConfigurationFileName() {
    final String loggingConfigurationFileName = System.getProperty(NET_ANWIBA_LOGGING_CONFIGURATION);
    if (loggingConfigurationFileName != null && new File(loggingConfigurationFileName).exists()) {
      return loggingConfigurationFileName;
    }
    final File xmlFile = getFile(DEFAULT_XML_LOG_CONFIGURATION_FILE);
    if (xmlFile.exists()) {
      return xmlFile.getAbsolutePath();
    }
    final File propertiesFile = getFile(DEFAULT_PROPERTIES_LOG_CONFIGURATION_FILE);
    if (propertiesFile.exists()) {
      return propertiesFile.getAbsolutePath();
    }
    return null;
  }

  public static File getFile(final String name) {
    final String classpath = System.getProperty("java.class.path"); //$NON-NLS-1$
    final StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);
    if (tokenizer.hasMoreTokens()) {
      final String pathName = tokenizer.nextToken();
      final File path = new File(pathName);
      if (new File(path.getParentFile(), name).exists()) {
        return new File(path.getParentFile(), name);
      }
    }
    return new File(new File(System.getProperty("user.dir")), name); //$NON-NLS-1$
  }

  public static void read(final InputStream inputStream) {
    try {
      final LoggerContext loggerContext = LoggerContext.getContext(false);
      final ConfigurationSource configSource = new ConfigurationSource(inputStream);
      XmlConfiguration configuration = new XmlConfiguration(loggerContext, configSource);
      loggerContext.setConfiguration(configuration);
    } catch (IOException exception) {
      Configurator.initialize(createDefaultConfiguration("INFO", Set.of(CONSOLE, FILE)));
    }
  }

  public static void initialize(final String level, final String... namespaces) {
    Configurator.initialize(createDefaultConfiguration(level.toUpperCase(), Set.of(CONSOLE, FILE), namespaces));
  }

  public static void read(final String loggingConfigurationFileName) {
    if (isLog4J2Available()) {
      if (loggingConfigurationFileName == null || loggingConfigurationFileName.trim().isEmpty()) {
        Configurator.initialize(createDefaultConfiguration("INFO", Set.of(CONSOLE, FILE)));
        return;
      }
      if (loggingConfigurationFileName.trim().toLowerCase().endsWith(".xml")) { //$NON-NLS-1$
        final ILogger logger = Logging.getLogger(LoggingUtilities.class.getName());
        logger.log(ILevel.DEBUG, "initialized logging by file " + loggingConfigurationFileName); //$NON-NLS-1$
        final File loggingConfigurationFile = new File(loggingConfigurationFileName);
        try (InputStream inputStream = new FileInputStream(loggingConfigurationFile)) {
          final LoggerContext loggerContext = LoggerContext.getContext(false);
          final ConfigurationSource configSource = new ConfigurationSource(inputStream, loggingConfigurationFile);
          XmlConfiguration configuration = new XmlConfiguration(loggerContext, configSource);
          loggerContext.setConfiguration(configuration);
        } catch (IOException exception) {
          Configurator.initialize(createDefaultConfiguration("INFO", Set.of(CONSOLE, FILE)));
        }
        return;
      }
      if (loggingConfigurationFileName.trim().toLowerCase().endsWith(".properties")) { //$NON-NLS-1$
        final ILogger logger = Logging.getLogger(LoggingUtilities.class.getName());
        logger.log(ILevel.DEBUG, "initialized logging by file " + loggingConfigurationFileName); //$NON-NLS-1$
        return;
      }
      return;
    }
    initializeJavaLogging(loggingConfigurationFileName);
  }

  private static boolean isLog4J2Available() {
    try {
      Class.forName("org.apache.logging.log4j.Logger"); //$NON-NLS-1$
      return true;
    } catch (final Throwable exception) {
      return false;
    }
  }

  private static BuiltConfiguration createDefaultConfiguration(
      final String levelName,
      final Set<String> appenders,
      final String... namespaces) {

    ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory
        .newConfigurationBuilder();

    if (appenders.contains(CONSOLE)) {
      builder.add(builder.newAppender(CONSOLE, ConsoleAppender.PLUGIN_NAME)
          .add(builder.newLayout("PatternLayout")
//              .addAttribute("pattern", "%d{ISO8601} %-5p [%t] %-40.40c - %m%n")));
      .addAttribute("pattern", "%m%n")));
    }

    builder.setConfigurationName("Default builder");
    builder.setStatusLevel(org.apache.logging.log4j.Level.ERROR);

    RootLoggerComponentBuilder rootLogger = builder.newRootLogger(org.apache.logging.log4j.Level.ERROR);
    rootLogger.add(builder.newAppenderRef(CONSOLE));
    builder.add(rootLogger);

    org.apache.logging.log4j.Level level = org.apache.logging.log4j.Level.getLevel(levelName);
    builder.add(builder.newLogger("net.anwiba", level));
    for (final String namespace : namespaces) {
      LoggerComponentBuilder logger = builder
          .newLogger(namespace, level)
//          .add(builder.newAppenderRef(CONSOLE))
//          .addAttribute("additivity", false)
          ;
      builder.add(logger);
    }

//    if (appenders.contains(FILE)) {
//      builder.add(builder
//          .newAppender("rolling", "RollingFile")
//          .addAttribute("fileName", "log")
//          .addAttribute("filePattern", "log.%d{yyyy-MM-dd}-%i.gz")
//          .add(builder.newLayout("PatternLayout")
//              .addAttribute("pattern", "[%p] [%d{dd.MM.yyyy-HH:mm:ss}] %m%n"))
//          .addComponent(builder
//              .newComponent("Policies")
//              .addComponent(
//                  builder
//                      .newComponent("SizeBasedTriggeringPolicy")
//                      .addAttribute("size", "200M"))));
//    }
    return builder.build();
  }

  public static void initializeJavaLogging(final String propertiesFileName) {
    try {
      if (propertiesFileName == null || propertiesFileName.trim().isEmpty()) {
        return;
      }
      final Properties properties = loadProperties(propertiesFileName);
      final SimpleFormatter formatter = new SimpleFormatter();
      final Handler[] handlers = createHandler(properties, formatter);
      final String levelName = (String) properties.get(LEVEL);
      initializeLogger(levelName == null ? JavaLogging.create(ILevel.DEBUG) : Level.parse(levelName), handlers);
      final ILogger logger = Logging.getLogger(LoggingUtilities.class.getName());
      logger.log(ILevel.DEBUG, "initialized logging by file " + propertiesFileName); //$NON-NLS-1$
      logger.log(ILevel.DEBUG, "for file " + properties.getProperty("log4j.appender.file.File", "none")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      logger.log(ILevel.DEBUG, "initialized logging level " + levelName); //$NON-NLS-1$
    } catch (final SecurityException exception) {
      exception.printStackTrace();
    } catch (final IOException exception) {
      exception.printStackTrace();
    }
  }

  private static void initializeLogger(final Level level, final Handler... handlers) {
    Logging.setHandler(handlers);
    Logging.setLevel(level, NET_ANWIBA);
  }

  private static Handler[] createHandler(final Properties properties, final Formatter formatter)
      throws SecurityException,
      IOException {
    final String handlerList = (String) properties.get(HANDLER);
    if (handlerList == null) {
      return createDefaultHandler(formatter);
    }
    final List<Handler> handlers = new ArrayList<>();
    final StringTokenizer tokenizer = new StringTokenizer(handlerList, ","); //$NON-NLS-1$
    while (tokenizer.hasMoreElements()) {
      final String handlerType = (String) tokenizer.nextElement();
      final Handler handler;
      if (handlerType.equalsIgnoreCase(FILE_HANDLER)) {
        final String file = (String) properties.get(FILE);
        handler = new FileHandler(getLogFileName(file), false);
      } else if (handlerType.equalsIgnoreCase(CONSOLE_HANDLER)) {
        handler = new ConsoleHandler();
      } else {
        continue;
      }
      handler.setFormatter(formatter);
      handlers.add(handler);
    }
    return handlers.toArray(new Handler[handlers.size()]);
  }

  private static Handler[] createDefaultHandler(final Formatter formatter) throws IOException {
    final Handler fileHandler = new FileHandler(getLogFileName(null), false);
    fileHandler.setFormatter(formatter);
    return new Handler[] { fileHandler };
  }

  private static Properties loadProperties(final String propertiesFileName) throws IOException, FileNotFoundException {
    final Properties properties = new Properties();
    final File propertiesFile = new File(propertiesFileName);
    if (!propertiesFile.exists()) {
      return properties;
    }
    try (FileReader reader = new FileReader(propertiesFile);) {
      properties.load(reader);
    }
    return properties;
  }

  private static String getLogFileName(final String file) {
    if (file == null) {
      return getPathName() + File.separator + "viewer.log.%u"; //$NON-NLS-1$
    }
    return file;
  }

  private static String getPathName() {
    final String pathName = System.getProperty("user.home") + File.separator + ".anwiba"; //$NON-NLS-1$ //$NON-NLS-2$
    final File file = new File(pathName);
    if (!file.exists()) {
      if (!file.mkdirs()) {
        return System.getProperty("user.home"); //$NON-NLS-1$
      }
    }
    return pathName;
  }
}