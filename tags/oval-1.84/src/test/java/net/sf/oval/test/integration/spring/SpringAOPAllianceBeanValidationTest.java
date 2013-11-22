/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
package net.sf.oval.test.integration.spring;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import junit.framework.TestCase;
import net.sf.oval.configuration.annotation.BeanValidationAnnotationsConfigurer;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.GuardInterceptor;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.SuppressOValWarnings;

import org.springframework.aop.framework.ProxyFactory;

/**
* @author Sebastian Thomschke
*/
public class SpringAOPAllianceBeanValidationTest extends TestCase
{
	public static interface TestServiceInterface
	{
		@Size(max = 5, message = "MAX_LENGTH")
		String getSomething(@NotNull(message = "NOT_NULL") final String input);
	}

	/**
	 * interface based service
	 */
	@Guarded(inspectInterfaces = true)
	public static class TestServiceWithInterface implements TestServiceInterface
	{
		public String getSomething(final String input)
		{
			return input;
		}
	}

	/**
	 * class based service
	 */
	public static class TestServiceWithoutInterface
	{
		@SuppressOValWarnings
		@Size(max = 5, message = "MAX_LENGTH")
		public String getSomething(@NotNull(message = "NOT_NULL") final String input)
		{
			return input;
		}
	}

	public void testCGLIBProxying()
	{
		{
			final ProxyFactory prFactory = new ProxyFactory(new TestServiceWithoutInterface());
			prFactory.setProxyTargetClass(true);
			prFactory.addAdvice(new GuardInterceptor(new Guard(new BeanValidationAnnotationsConfigurer())));
			final TestServiceWithoutInterface testServiceWithoutInterface = (TestServiceWithoutInterface) prFactory
					.getProxy();

			try
			{
				testServiceWithoutInterface.getSomething(null);
				fail();
			}
			catch (final ConstraintsViolatedException ex)
			{
				assertEquals("NOT_NULL", ex.getConstraintViolations()[0].getMessage());
			}

			try
			{
				testServiceWithoutInterface.getSomething("123456");
				fail();
			}
			catch (final ConstraintsViolatedException ex)
			{
				assertEquals("MAX_LENGTH", ex.getConstraintViolations()[0].getMessage());
			}
		}

		{
			final ProxyFactory prFactory = new ProxyFactory(new TestServiceWithInterface());
			prFactory.setProxyTargetClass(true);
			prFactory.addAdvice(new GuardInterceptor(new Guard(new BeanValidationAnnotationsConfigurer())));
			final TestServiceWithInterface testServiceWithInterface = (TestServiceWithInterface) prFactory.getProxy();

			try
			{
				testServiceWithInterface.getSomething(null);
				fail();
			}
			catch (final ConstraintsViolatedException ex)
			{
				assertEquals("NOT_NULL", ex.getConstraintViolations()[0].getMessage());
			}

			try
			{
				testServiceWithInterface.getSomething("123456");
				fail();
			}
			catch (final ConstraintsViolatedException ex)
			{
				assertEquals("MAX_LENGTH", ex.getConstraintViolations()[0].getMessage());
			}
		}
	}

	public void testJDKProxying()
	{
		final ProxyFactory prFactory = new ProxyFactory(new TestServiceWithInterface());
		prFactory.setProxyTargetClass(false);
		prFactory.addAdvice(new GuardInterceptor(new Guard(new BeanValidationAnnotationsConfigurer())));
		final TestServiceInterface testServiceWithInterface = (TestServiceInterface) prFactory.getProxy();

		try
		{
			testServiceWithInterface.getSomething(null);
			fail();
		}
		catch (final ConstraintsViolatedException ex)
		{
			assertEquals("NOT_NULL", ex.getConstraintViolations()[0].getMessage());
		}

		try
		{
			testServiceWithInterface.getSomething("123456");
			fail();
		}
		catch (final ConstraintsViolatedException ex)
		{
			assertEquals("MAX_LENGTH", ex.getConstraintViolations()[0].getMessage());
		}
	}
}
