/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.oval.internal.util.Assert;

/**
 * Commons Logging Wrapper
 *
 * @author Sebastian Thomschke
 */
public class LoggerJCLImpl implements Logger {
   private final Log jclLoggger;

   /**
    * @param name the name of the logger
    * @throws IllegalArgumentException if <code>name == null</code>
    */
   public LoggerJCLImpl(final String name) throws IllegalArgumentException {
      Assert.argumentNotNull("name", name);

      jclLoggger = LogFactory.getLog(name);
   }

   @Override
   public void debug(final String msg) {
      jclLoggger.debug(msg);
   }

   @Override
   public void debug(final String msg, final Throwable t) {
      jclLoggger.debug(msg, t);
   }

   @Override
   public void error(final String msg) {
      jclLoggger.error(msg);
   }

   @Override
   public void error(final String msg, final Throwable t) {
      jclLoggger.error(msg, t);
   }

   @Override
   public void info(final String msg) {
      jclLoggger.info(msg);
   }

   @Override
   public void info(final String msg, final Throwable t) {
      jclLoggger.info(msg, t);
   }

   @Override
   public boolean isDebug() {
      return jclLoggger.isDebugEnabled();
   }

   @Override
   public boolean isError() {
      return jclLoggger.isErrorEnabled();
   }

   @Override
   public boolean isInfo() {
      return jclLoggger.isInfoEnabled();
   }

   @Override
   public boolean isTrace() {
      return jclLoggger.isTraceEnabled();
   }

   @Override
   public boolean isWarn() {
      return jclLoggger.isWarnEnabled();
   }

   @Override
   public void trace(final String msg) {
      jclLoggger.trace(msg);
   }

   @Override
   public void trace(final String msg, final Throwable t) {
      jclLoggger.trace(msg, t);
   }

   @Override
   public void warn(final String msg) {
      jclLoggger.warn(msg);
   }

   @Override
   public void warn(final String msg, final Throwable t) {
      jclLoggger.warn(msg, t);
   }
}
