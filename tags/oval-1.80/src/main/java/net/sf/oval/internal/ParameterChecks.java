/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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
package net.sf.oval.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;

import net.sf.oval.Check;
import net.sf.oval.CheckExclusion;
import net.sf.oval.context.ConstructorParameterContext;
import net.sf.oval.context.MethodParameterContext;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.util.LinkedSet;

/**
 * @author Sebastian Thomschke
 */
public final class ParameterChecks
{
	public final Set<Check> checks = new LinkedSet<Check>(2);
	public final Set<CheckExclusion> checkExclusions = new LinkedSet<CheckExclusion>(2);

	public final int parameterIndex;

	public final OValContext context;

	public ParameterChecks(final Constructor< ? > ctor, final int paramIndex, final String paramName)
	{
		context = new ConstructorParameterContext(ctor, paramIndex, paramName);
		parameterIndex = paramIndex;
	}

	public ParameterChecks(final Method method, final int paramIndex, final String paramName)
	{
		context = new MethodParameterContext(method, paramIndex, paramName);
		parameterIndex = paramIndex;
	}

	public boolean hasChecks()
	{
		return checks.size() > 0;
	}

	public boolean hasExclusions()
	{
		return checkExclusions.size() > 0;
	}

	public boolean isEmpty()
	{
		return checks.size() == 0 && checkExclusions.size() == 0;
	}
}
