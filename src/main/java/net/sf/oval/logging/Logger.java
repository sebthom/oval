/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
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
