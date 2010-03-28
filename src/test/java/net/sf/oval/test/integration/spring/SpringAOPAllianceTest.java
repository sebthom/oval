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

import junit.framework.TestCase;
import net.sf.oval.constraint.MaxLength;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.SuppressOValWarnings;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
* @author Sebastian Thomschke
*/
public class SpringAOPAllianceTest extends TestCase
{
	/**
	 * class based service
	 */
	public static class TestCGLIBProxyService
	{
		@SuppressOValWarnings
		@MaxLength(value = 5, message = "MAX_LENGTH")
		public String getSomething(@NotNull(message = "NOT_NULL") final String input)
		{
			return input;
		}
	}

	public static interface TestJDKProxyService
	{
		@MaxLength(value = 5, message = "MAX_LENGTH")
		String getSomething(@NotNull(message = "NOT_NULL") final String input);
	}

	/**
	 * interface based service
	 */
	public static class TestJDKProxyServiceImpl implements TestJDKProxyService
	{
		public String getSomething(final String input)
		{
			return input;
		}
	}

	public void testCGLIBProxyService()
	{
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"SpringAOPAllianceTestCGLIBProxy.xml", SpringAOPAllianceTest.class);
		final TestCGLIBProxyService testService = (TestCGLIBProxyService) ctx.getBean("testService");

		try
		{
			testService.getSomething(null);
			fail();
		}
		catch (final ConstraintsViolatedException ex)
		{
			assertEquals("NOT_NULL", ex.getConstraintViolations()[0].getMessage());
		}

		try
		{
			testService.getSomething("123456");
			fail();
		}
		catch (final ConstraintsViolatedException ex)
		{
			assertEquals("MAX_LENGTH", ex.getConstraintViolations()[0].getMessage());
		}
	}

	public void testJDKProxyService()
	{
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"SpringAOPAllianceTestJDKProxy.xml", SpringAOPAllianceTest.class);
		final TestJDKProxyService testService = (TestJDKProxyService) ctx.getBean("testService",
				TestJDKProxyService.class);

		try
		{
			testService.getSomething(null);
			fail();
		}
		catch (final ConstraintsViolatedException ex)
		{
			assertEquals("NOT_NULL", ex.getConstraintViolations()[0].getMessage());
		}

		try
		{
			testService.getSomething("123456");
			fail();
		}
		catch (final ConstraintsViolatedException ex)
		{
			assertEquals("MAX_LENGTH", ex.getConstraintViolations()[0].getMessage());
		}
	}
}
