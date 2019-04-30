/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.sf.oval.internal.util.Assert;

/**
 * JDK Logging Wrapper
 *
 * @author Sebastian Thomschke
 */
public class LoggerJDKImpl implements Logger {
   private final java.util.logging.Logger jdkLogger;
   private final String name;

   /**
    * @param name the name of the logger
    * @throws IllegalArgumentException if <code>name == null</code>
    */
   public LoggerJDKImpl(final String name) throws IllegalArgumentException {
      Assert.argumentNotNull("name", name);

      this.name = name;
      jdkLogger = java.util.logging.Logger.getLogger(name);
   }

   @Override
   public void debug(final String msg) {
      log(Level.FINE, msg, null);
   }

   @Override
   public void debug(final String msg, final Throwable t) {
      log(Level.FINE, msg, t);
   }

   @Override
   public void error(final String msg) {
      log(Level.SEVERE, msg, null);
   }

   @Override
   public void error(final String msg, final Throwable t) {
      log(Level.SEVERE, msg, t);
   }

   @Override
   public void info(final String msg) {
      log(Level.INFO, msg, null);
   }

   @Override
   public void info(final String msg, final Throwable t) {
      log(Level.INFO, msg, t);
   }

   @Override
   public boolean isDebug() {
      return jdkLogger.isLoggable(Level.FINE);
   }

   @Override
   public boolean isError() {
      return jdkLogger.isLoggable(Level.SEVERE);
   }

   @Override
   public boolean isInfo() {
      return jdkLogger.isLoggable(Level.INFO);
   }

   @Override
   public boolean isTrace() {
      return jdkLogger.isLoggable(Level.FINEST);
   }

   @Override
   public boolean isWarn() {
      return jdkLogger.isLoggable(Level.WARNING);
   }

   private void log(final Level level, final String msg, final Throwable t) {
      final LogRecord record = new LogRecord(level, msg);
      record.setLoggerName(name);
      record.setThrown(t);

      /* java.lang.Throwable
       *    at net.sf.oval.logging.LoggerJDKImpl.log(LoggerJDKImpl.java:123)
       *    at net.sf.oval.logging.LoggerJDKImpl.warn(LoggerJDKImpl.java:136)
       *    at net.sf.oval.internal.Log.warn(Log.java:180)
       */
      final int offset = 2;
      final StackTraceElement[] steArray = new Throwable().getStackTrace();
      record.setSourceClassName(steArray[offset].getClassName());
      record.setSourceMethodName(steArray[offset].getMethodName());

      jdkLogger.log(record);
   }

   @Override
   public void trace(final String msg) {
      log(Level.FINEST, msg, null);
   }

   @Override
   public void trace(final String msg, final Throwable t) {
      log(Level.FINEST, msg, t);
   }

   @Override
   public void warn(final String msg) {
      log(Level.WARNING, msg, null);
   }

   @Override
   public void warn(final String msg, final Throwable t) {
      log(Level.WARNING, msg, t);
   }
}
