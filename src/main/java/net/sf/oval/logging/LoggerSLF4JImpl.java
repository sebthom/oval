/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.logging;

import net.sf.oval.internal.util.Assert;

/**
 * SLF4J Wrapper
 *
 * @author Sebastian Thomschke
 */
public class LoggerSLF4JImpl implements Logger {
   private final org.slf4j.Logger slf4jLogger;

   /**
    * @param name the name of the logger
    * @throws IllegalArgumentException if <code>name == null</code>
    */
   public LoggerSLF4JImpl(final String name) throws IllegalArgumentException {
      Assert.argumentNotNull("name", name);
      slf4jLogger = org.slf4j.LoggerFactory.getLogger(name);
   }

   @Override
   public void debug(final String msg) {
      slf4jLogger.debug(msg);
   }

   @Override
   public void debug(final String msg, final Throwable t) {
      slf4jLogger.debug(msg, t);
   }

   @Override
   public void error(final String msg) {
      slf4jLogger.error(msg);
   }

   @Override
   public void error(final String msg, final Throwable t) {
      slf4jLogger.error(msg, t);
   }

   @Override
   public void info(final String msg) {
      slf4jLogger.info(msg);
   }

   @Override
   public void info(final String msg, final Throwable t) {
      slf4jLogger.info(msg, t);
   }

   @Override
   public boolean isDebug() {
      return slf4jLogger.isDebugEnabled();
   }

   @Override
   public boolean isError() {
      return slf4jLogger.isErrorEnabled();
   }

   @Override
   public boolean isInfo() {
      return slf4jLogger.isInfoEnabled();
   }

   @Override
   public boolean isTrace() {
      return slf4jLogger.isTraceEnabled();
   }

   @Override
   public boolean isWarn() {
      return slf4jLogger.isWarnEnabled();
   }

   @Override
   public void trace(final String msg) {
      slf4jLogger.trace(msg);
   }

   @Override
   public void trace(final String msg, final Throwable t) {
      slf4jLogger.trace(msg, t);
   }

   @Override
   public void warn(final String msg) {
      slf4jLogger.warn(msg);
   }

   @Override
   public void warn(final String msg, final Throwable t) {
      slf4jLogger.warn(msg, t);
   }
}
