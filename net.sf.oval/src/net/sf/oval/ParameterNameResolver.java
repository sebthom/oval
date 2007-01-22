/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2007 Sebastian
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.sf.oval.exceptions.ReflectionException;

/**
 * @author Sebastian Thomschke
 */
public interface ParameterNameResolver
{
	/**
	 * returns an empty String array if no parameters are declared
	 */
	String[] getParameterNames(Method method) throws ReflectionException;

	/**
	 * returns an empty String array if no parameters are declared
	 */
	String[] getParameterNames(Constructor constructor) throws ReflectionException;
}
