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
	protected final static class MethodInvocable implements Invocable
	{
		private final MethodInvocation methodInvocation;

		protected MethodInvocable(final MethodInvocation methodInvocation)
		{
			this.methodInvocation = methodInvocation;
		}

		public Object invoke() throws Exception
		{
			try
			{
				return methodInvocation.proceed();
			}
			catch (final Throwable e)
			{
				if (e instanceof Exception)
				{
					throw (Exception) e;
				}
				throw new RuntimeException(e);
			}
		}
	}

	private final static Log LOG = Log.getLog(GuardInterceptor.class);

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
		final Constructor< ? > CONSTRUCTOR = constructorInvocation.getConstructor();
		final Object[] args = constructorInvocation.getArguments();
		final Object TARGET = constructorInvocation.getThis();

		// pre conditions
		{
			guard.guardConstructorPre(TARGET, CONSTRUCTOR, args);
		}

		final Object result = constructorInvocation.proceed();

		// post conditions
		{
			guard.guardConstructorPost(TARGET, CONSTRUCTOR, args);
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
