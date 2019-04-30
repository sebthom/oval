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

/**
 * @author Sebastian Thomschke
 */
public class LoggerFactoryJCLImpl implements LoggerFactory {

   @Override
   public Logger createLogger(final String name) {
      return new LoggerJCLImpl(name);
   }
}
