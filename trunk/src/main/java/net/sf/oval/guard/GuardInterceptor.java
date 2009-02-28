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

import java.lang.reflect.Constructor;

import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.Invocable;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.ConstructorInvocation;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * AOP Alliance Interceptor implementation of the Guard aspect
 * 
 * @author Sebastian Thomschke
 */
public class GuardInterceptor implements MethodInterceptor, ConstructorInterceptor
{
	protected static final class MethodInvocable implements Invocable
	{
		private final MethodInvocation methodInvocation;

		protected MethodInvocable(final MethodInvocation methodInvocation)
		{
			this.methodInvocation = methodInvocation;
		}

		/**
		 * {@inheritDoc}
		 */
		public Object invoke() throws Throwable
		{
			return methodInvocation.proceed();
		}
	}

	private static final Log LOG = Log.getLog(GuardInterceptor.class);

	private Guard guard;

	public GuardInterceptor()
	{
		this(new Guard());
	}

	public GuardInterceptor(final Guard guard)
	{
		LOG.info("Instantiated");

		setGuard(guard);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object construct(final ConstructorInvocation constructorInvocation) throws Throwable
	{
		final Constructor< ? > ctor = constructorInvocation.getConstructor();
		final Object[] args = constructorInvocation.getArguments();
		final Object target = constructorInvocation.getThis();

		// pre conditions
		{
			guard.guardConstructorPre(target, ctor, args);
		}

		final Object result = constructorInvocation.proceed();

		// post conditions
		{
			guard.guardConstructorPost(target, ctor, args);
		}

		return result;
	}

	/**
	 * @return the guard
	 */
	public Guard getGuard()
	{
		return guard;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object invoke(final MethodInvocation methodInvocation) throws Throwable
	{
		return guard.guardMethod(methodInvocation.getThis(), methodInvocation.getMethod(), methodInvocation
				.getArguments(), new MethodInvocable(methodInvocation));
	}

	public void setGuard(final Guard guard)
	{
		this.guard = guard;
	}
}
