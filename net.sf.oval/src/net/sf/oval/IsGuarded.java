/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005, 2006 Sebastian
 * Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.oval;

/**
 * Marker interface that is added to advised classes by the GuardAspect
 * to indicate that constraints are actually enforced via AOP.<br>
 * <br>
 * <b>Important:</b> Do NOT directly implement this interface in your classes.
 * 
 * @author Sebastian Thomschke
 * 
 * @see net.sf.oval.aspectj.GuardAspect
 */
public interface IsGuarded
{

}
