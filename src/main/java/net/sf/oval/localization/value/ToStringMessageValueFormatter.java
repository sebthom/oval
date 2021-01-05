/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
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
