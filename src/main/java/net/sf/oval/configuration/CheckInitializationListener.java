/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.configuration;

import net.sf.oval.Check;

/**
 * @author Sebastian Thomschke
 */
public interface CheckInitializationListener {

   void onCheckInitialized(Check check);
}
