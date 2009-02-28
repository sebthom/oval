/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2009 Sebastian
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

import java.lang.reflect.Method;
import java.util.LinkedList;

import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.exception.InvokingMethodFailedException;
import net.sf.oval.internal.util.MethodInvocationCommand;

/**
 * 
 * @author Sebastian Thomschke
 */
public class ProbeModeListener extends ConstraintsViolatedAdapter
{
	private final Object target;
	private final LinkedList<MethodInvocationCommand> commands = new LinkedList<MethodInvocationCommand>();

	/**
	 * Creates a new instance for the given target object.
	 * @param target the target object
	 */
	ProbeModeListener(final Object target)
	{
		this.target = target;
	}

	/**
	 * Executes the collected method calls and clears the internal list holding them.
	 */
	public synchronized void commit() throws InvokingMethodFailedException, ConstraintsViolatedException
	{
		for (final MethodInvocationCommand cmd : commands)
		{
			cmd.execute();
		}
		commands.clear();
	}

	/**
	 * Returns the object that is/was in probe mode.
	 * @return the object that is/was in probe mode
	 */
	public Object getTarget()
	{
		return target;
	}

	/**
	 * Adds the given method and method arguments to the method call stack.
	 * @param method the method
	 * @param args the method arguments
	 */
	void onMethodCall(final Method method, final Object[] args)
	{
		commands.add(new MethodInvocationCommand(target, method, args));
	}
}
