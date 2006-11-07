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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.sf.oval.exceptions.ReflectionException;

/**
 * @author Sebastian Thomschke
 */
public interface ParameterNameResolver
{
	String[] getParameterNames(Method method) throws ReflectionException;

	String[] getParameterNames(Constructor constructor) throws ReflectionException;
}
