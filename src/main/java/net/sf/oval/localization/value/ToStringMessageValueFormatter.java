/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.localization.value;

/**
 * @author Sebastian Thomschke
 */
public class ToStringMessageValueFormatter implements MessageValueFormatter {
   public static final ToStringMessageValueFormatter INSTANCE = new ToStringMessageValueFormatter();

   @Override
   public String format(final Object value) {
      if (value == null)
         return "null";
      return value.toString();
   }
}
