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
package net.sf.oval.test.guard;

import junit.framework.TestCase;
import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class InheritanceTest extends TestCase
{
	@Guarded
	public static class Entity extends SuperEntity
	{
		/**
		 * @param name the name to set
		 */
		public void setName2(@AssertFieldConstraints
		final String name)
		{
			this.name = name;
		}
	}

	@Guarded(applyFieldConstraintsToSetters = true)
	public static class SuperEntity
	{
		@NotNull
		protected String name = "";

		/**
		 * @return the name
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(final String name)
		{
			this.name = name;
		}
	}

	public void testInheritance()
	{
		final Guard guard = new Guard();
		TestGuardAspect.aspectOf().setGuard(guard);

		final Entity e = new Entity();

		try
		{
			e.setName(null);
			fail("ConstraintViolationException should have been thrown");
		}
		catch (final ConstraintsViolatedException ex)
		{
			// expected
		}

		try
		{
			e.setName2(null);
			fail("ConstraintViolationException should have been thrown");
		}
		catch (final ConstraintsViolatedException ex)
		{
			// expected
		}
	}
}
