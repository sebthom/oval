/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.logging;

/**
 * @author Sebastian Thomschke
 */
public class LoggerFactoryJCLImpl implements LoggerFactory {

   @Override
   public Logger createLogger(final String name) {
      return new LoggerJCLImpl(name);
   }
}
