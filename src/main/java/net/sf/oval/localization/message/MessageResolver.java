/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.localization.message;

/**
 * @author Sebastian Thomschke
 */
public interface MessageResolver {

   /**
    * @return null if not found
    */
   String getMessage(String key);
}
