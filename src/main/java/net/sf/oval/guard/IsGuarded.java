/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.guard;

/**
 * Marker interface that is added to advised classes by the GuardAspect
 * to indicate that constraints are actually enforced via AOP.<br>
 * <br>
 * <b>Important:</b> Do NOT directly implement this interface in your classes.
 *
 * @author Sebastian Thomschke
 */
public interface IsGuarded {
   //
}
