/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2006-2007 Sebastian
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
package net.sf.oval.guard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.sf.oval.exception.ReflectionException;

import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;

/**
 * This implementation that uses com.thoughtworks.paranamer.Paranamer (ttp://paranamer.codehaus.org/)
 * to determinethe names of paramater names as a fallback the results of a 
 * ParameterNameResolverEnumerationImpl are returned
 *  
 * @author Sebastian Thomschke
 */
public class ParameterNameResolverParanamerImpl implements ParameterNameResolver
{
	private final Paranamer paranamer = new CachingParanamer();
	private static final ParameterNameResolver fallback = new ParameterNameResolverEnumerationImpl();

	public String[] getParameterNames(final Method method) throws ReflectionException
	{
		final String[] parameterNames = paranamer.lookupParameterNames(method);

		if (parameterNames == null) return fallback.getParameterNames(method);

		return parameterNames;
	}

	public String[] getParameterNames(final Constructor constructor) throws ReflectionException
	{
		return fallback.getParameterNames(constructor);
	}
}
