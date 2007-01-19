/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005, 2006 Sebastian
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
import net.sf.oval.constraints.AssertFieldConstraints;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.exceptions.ConstraintsViolatedException;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class InheritanceTest extends TestCase
{
	@Guarded(applyFieldConstraintsToSetter = true)
	public static class SuperEntity
	{
		@NotNull
		protected String name;

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
		public void setName(String name)
		{
			this.name = name;
		}
	}

	@Guarded
	public static class Entity extends SuperEntity
	{
		/**
		 * @param name the name to set
		 */
		public void setName2(@AssertFieldConstraints
		String name)
		{
			this.name = name;
		}
	}

	public void testInheritance()
	{
		Entity e = new Entity();

		try
		{
			e.setName(null);
			fail("ConstraintViolationException should have been thrown");
		}
		catch (ConstraintsViolatedException ex)
		{}

		try
		{
			e.setName2(null);
			fail("ConstraintViolationException should have been thrown");
		}
		catch (ConstraintsViolatedException ex)
		{}
	}
}
