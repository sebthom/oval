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
import net.sf.oval.ConstraintsViolatedException;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.ExceptionTranslatorJDKExceptionsImpl;
import net.sf.oval.guard.Guard;
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
		final Guard guard = new Guard();
		TestGuardAspect.aspectOf().setGuard(guard);

		assertNull(guard.getExceptionTranslator());

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
			guard.setExceptionTranslator(new ExceptionTranslatorJDKExceptionsImpl());
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
			guard.setExceptionTranslator(null);
		}
	}
}
