/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.oval.internal.util.StringUtils;
import net.sf.oval.logging.Logger;
import net.sf.oval.logging.LoggerFactory;
import net.sf.oval.logging.LoggerFactoryJDKImpl;

/**
 * @author Sebastian Thomschke
 */
public final class Log {
   private static LoggerFactory loggerFactory = new LoggerFactoryJDKImpl();

   /* cannot use CollectionFactoryHolder.getFactory().createMap(32) here, since
    * the collection factory uses the Log itself which is not yet initialized
    */
   private static final Map<String, Log> LOG_REGISTRY = new HashMap<>(32);

   public static synchronized Log getLog(final Class<?> clazz) throws IllegalArgumentException {
      final String name = clazz.getName();
      final Log log = LOG_REGISTRY.get(name);
      if (log == null)
         return new Log(loggerFactory.createLogger(name));
      return log;
   }

   public static LoggerFactory getLoggerFactory() {
      synchronized (LOG_REGISTRY) {
         return loggerFactory;
      }
   }

   public static void setLoggerFactory(final LoggerFactory loggerFactory) throws IllegalArgumentException {
      synchronized (LOG_REGISTRY) {
         Log.loggerFactory = loggerFactory;
         for (final Entry<String, Log> entry : LOG_REGISTRY.entrySet()) {
            entry.getValue().setLogger(loggerFactory.createLogger(entry.getKey()));
         }
      }
   }

   private Logger logger;

   /**
    * private constructor to avoid external instantiation
    */
   private Log(final Logger logger) {
      setLogger(logger);
   }

   public void debug(final String msg) {
      logger.debug(msg);
   }

   public void debug(final String msgFormat, final Object arg1) {
      if (logger.isDebug()) {
         logger.debug(StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString()));
      }
   }

   public void debug(final String msgFormat, final Object arg1, final Object arg2) {
      if (logger.isDebug()) {
         String msg = StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString());
         msg = StringUtils.replaceAll(msg, "{2}", arg2 == null ? "null" : arg2.toString());

         logger.debug(msg);
      }
   }

   public void debug(final String msgFormat, final Object arg1, final Object arg2, final Object arg3) {
      if (logger.isDebug()) {
         String msg = StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString());
         msg = StringUtils.replaceAll(msg, "{2}", arg2 == null ? "null" : arg2.toString());
         msg = StringUtils.replaceAll(msg, "{3}", arg3 == null ? "null" : arg3.toString());

         logger.debug(msg);
      }
   }

   public void debug(final String msgFormat, final Object arg1, final Object arg2, final Throwable t) {
      if (logger.isDebug()) {
         String msg = StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString());
         msg = StringUtils.replaceAll(msg, "{2}", arg2 == null ? "null" : arg2.toString());

         logger.debug(msg, t);
      }
   }

   public void debug(final String msgFormat, final Object arg1, final Throwable t) {
      if (logger.isDebug()) {
         logger.debug(StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString()), t);
      }
   }

   public void debug(final String msg, final Throwable t) {
      logger.debug(msg, t);
   }

   public void error(final String msg) {
      logger.error(msg);
   }

   public void error(final String msgFormat, final Object arg1) {
      if (logger.isError()) {
         logger.error(StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString()));
      }
   }

   public void error(final String msgFormat, final Object arg1, final Object arg2) {
      if (logger.isError()) {
         String msg = StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString());
         msg = StringUtils.replaceAll(msg, "{2}", arg2 == null ? "null" : arg2.toString());

         logger.error(msg);
      }
   }

   public void error(final String msgFormat, final Object arg1, final Object arg2, final Object arg3) {
      if (logger.isError()) {
         String msg = StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString());
         msg = StringUtils.replaceAll(msg, "{2}", arg2 == null ? "null" : arg2.toString());
         msg = StringUtils.replaceAll(msg, "{3}", arg3 == null ? "null" : arg3.toString());

         logger.error(msg);
      }
   }

   public void error(final String msgFormat, final Object arg1, final Object arg2, final Throwable t) {
      if (logger.isError()) {
         String msg = StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString());
         msg = StringUtils.replaceAll(msg, "{2}", arg2 == null ? "null" : arg2.toString());

         logger.error(msg, t);
      }
   }

   public void error(final String msgFormat, final Object arg1, final Throwable t) {
      if (logger.isError()) {
         logger.error(StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString()), t);
      }
   }

   public void error(final String msg, final Throwable t) {
      logger.error(msg, t);
   }

   public void info(final String msg) {
      logger.info(msg);
   }

   public void info(final String msgFormat, final Object arg1) {
      if (logger.isInfo()) {
         logger.info(StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString()));
      }
   }

   public void info(final String msgFormat, final Object arg1, final Object arg2) {
      if (logger.isInfo()) {
         String msg = StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString());
         msg = StringUtils.replaceAll(msg, "{2}", arg2 == null ? "null" : arg2.toString());

         logger.info(msg);
      }
   }

   public void info(final String msgFormat, final Object arg1, final Object arg2, final Object arg3) {
      if (logger.isInfo()) {
         String msg = StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString());
         msg = StringUtils.replaceAll(msg, "{2}", arg2 == null ? "null" : arg2.toString());
         msg = StringUtils.replaceAll(msg, "{3}", arg3 == null ? "null" : arg3.toString());

         logger.info(msg);
      }
   }

   public void info(final String msgFormat, final Object arg1, final Object arg2, final Throwable t) {
      if (logger.isInfo()) {
         String msg = StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString());
         msg = StringUtils.replaceAll(msg, "{2}", arg2 == null ? "null" : arg2.toString());

         logger.info(msg, t);
      }
   }

   public void info(final String msgFormat, final Object arg1, final Throwable t) {
      if (logger.isInfo()) {
         logger.info(StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString()), t);
      }
   }

   public void info(final String msg, final Throwable t) {
      logger.info(msg, t);
   }

   public boolean isDebug() {
      return logger.isDebug();
   }

   public boolean isError() {
      return logger.isError();
   }

   public boolean isInfo() {
      return logger.isInfo();
   }

   public boolean isTrace() {
      return logger.isTrace();
   }

   public boolean isWarn() {
      return logger.isWarn();
   }

   private void setLogger(final Logger logger) {
      this.logger = logger;
   }

   public void trace(final String msg) {
      logger.debug(msg);
   }

   public void trace(final String msgFormat, final Object arg1) {
      if (logger.isDebug()) {
         logger.trace(StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString()));
      }
   }

   public void trace(final String msgFormat, final Object arg1, final Object arg2) {
      if (logger.isTrace()) {
         String msg = StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString());
         msg = StringUtils.replaceAll(msg, "{2}", arg2 == null ? "null" : arg2.toString());

         logger.trace(msg);
      }
   }

   public void trace(final String msgFormat, final Object arg1, final Object arg2, final Object arg3) {
      if (logger.isTrace()) {
         String msg = StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString());
         msg = StringUtils.replaceAll(msg, "{2}", arg2 == null ? "null" : arg2.toString());
         msg = StringUtils.replaceAll(msg, "{3}", arg3 == null ? "null" : arg3.toString());

         logger.trace(msg);
      }
   }

   public void trace(final String msgFormat, final Object arg1, final Object arg2, final Throwable t) {
      if (logger.isTrace()) {
         String msg = StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString());
         msg = StringUtils.replaceAll(msg, "{2}", arg2 == null ? "null" : arg2.toString());

         logger.trace(msg, t);
      }
   }

   public void trace(final String msgFormat, final Object arg1, final Throwable t) {
      if (logger.isDebug()) {
         logger.trace(StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString()), t);
      }
   }

   public void trace(final String msg, final Throwable t) {
      logger.trace(msg, t);
   }

   public void warn(final String msg) {
      logger.warn(msg);
   }

   public void warn(final String msgFormat, final Object arg1) {
      if (logger.isWarn()) {
         logger.warn(StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString()));
      }
   }

   public void warn(final String msgFormat, final Object arg1, final Object arg2) {
      if (logger.isWarn()) {
         String msg = StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString());
         msg = StringUtils.replaceAll(msg, "{2}", arg2 == null ? "null" : arg2.toString());

         logger.warn(msg);
      }
   }

   public void warn(final String msgFormat, final Object arg1, final Object arg2, final Object arg3) {
      if (logger.isWarn()) {
         String msg = StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString());
         msg = StringUtils.replaceAll(msg, "{2}", arg2 == null ? "null" : arg2.toString());
         msg = StringUtils.replaceAll(msg, "{3}", arg3 == null ? "null" : arg3.toString());

         logger.warn(msg);
      }
   }

   public void warn(final String msgFormat, final Object arg1, final Object arg2, final Throwable t) {
      if (logger.isWarn()) {
         String msg = StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString());
         msg = StringUtils.replaceAll(msg, "{2}", arg2 == null ? "null" : arg2.toString());

         logger.warn(msg, t);
      }
   }

   public void warn(final String msgFormat, final Object arg1, final Throwable t) {
      if (logger.isWarn()) {
         logger.warn(StringUtils.replaceAll(msgFormat, "{1}", arg1 == null ? "null" : arg1.toString()), t);
      }
   }

   public void warn(final String msg, final Throwable t) {
      logger.warn(msg, t);
   }
}
