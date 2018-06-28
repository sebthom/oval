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

/**
 * @author Sebastian Thomschke
 */
public interface Logger {
   void debug(String msg);

   void debug(String msg, Throwable t);

   void error(String msg);

   void error(String msg, Throwable t);

   void info(String msg);

   void info(String msg, Throwable t);

   boolean isDebug();

   boolean isError();

   boolean isInfo();

   boolean isTrace();

   boolean isWarn();

   void trace(String msg);

   void trace(String msg, Throwable t);

   void warn(String msg);

   void warn(String msg, Throwable t);
}
