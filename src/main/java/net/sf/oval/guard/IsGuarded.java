/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
