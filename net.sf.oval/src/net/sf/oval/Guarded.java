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
 * Marker interface that is added to advised classes by the ConstraintsEnforcerAspect
 * to indicate that constraints are actually enforced via AOP. Do not use it manually.
 * 
 * @author Sebastian Thomschke
 */
public interface Guarded
{

}
