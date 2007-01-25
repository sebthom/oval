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
package net.sf.oval.test.guard;

import junit.framework.TestCase;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.guard.ConstraintsViolatedException;
import net.sf.oval.guard.ExceptionTranslatorJREExceptionsImpl;
import net.sf.oval.guard.Guarded;

/**
 * 
 * @author Sebastian Thomschke
 */
public class ExceptionTranslatorTest extends TestCase
{
	@Guarded
	public final static class TestEntity
	{
		public void setName(@NotNull(message = "NULL")
		String name)
		{
		// ...
		}
	}

	public void testExceptionTranslator()
	{
		assertNull(TestGuardAspect.aspectOf().getGuard().getExceptionTranslator());

		try
		{
			final TestEntity t = new TestEntity();
			t.setName(null);
		}
		catch (ConstraintsViolatedException ex)
		{
			assertEquals(ex.getMessage(), "NULL");
		}

		try
		{
			TestGuardAspect.aspectOf().getGuard().setExceptionTranslator(
					new ExceptionTranslatorJREExceptionsImpl());
			try
			{
				final TestEntity t = new TestEntity();
				t.setName(null);
				fail();
			}
			catch (IllegalArgumentException ex)
			{
				assertEquals(ex.getMessage(), "NULL");
			}

		}
		finally
		{
			TestGuardAspect.aspectOf().getGuard().setExceptionTranslator(null);
		}
	}
}
