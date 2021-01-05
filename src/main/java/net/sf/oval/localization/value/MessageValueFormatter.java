/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.localization.value;

/**
 * @author Sebastian Thomschke
 */
public interface MessageValueFormatter {
   /**
    * @return a string representation of the given value object
    */
   String format(Object value);
}
